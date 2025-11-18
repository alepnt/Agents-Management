package com.example.server.service;

import com.example.common.dto.UserDTO;
import com.example.server.domain.Agent;
import com.example.server.domain.Role;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequest;
import com.example.server.dto.RegisterRequest;
import com.example.server.dto.UserSummary;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.mapper.UserMapper;
import com.example.server.security.MsalClientProvider;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final String DEFAULT_ROLE = "Agent";
    private static final String DEFAULT_TEAM = "Vendite";

    private final MsalClientProvider msalClientProvider;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;
    private final Clock clock;
    private final Set<String> scopes;

    public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository,
                       AgentRepository agentRepository,
                       RoleRepository roleRepository,
                       TeamRepository teamRepository,
                       Clock clock,
                       @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope) {
        this.msalClientProvider = msalClientProvider;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.roleRepository = roleRepository;
        this.teamRepository = teamRepository;
        this.clock = clock;
        this.scopes = parseScopes(defaultScope);
    }

    public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository,
                       AgentRepository agentRepository,
                       RoleRepository roleRepository,
                       TeamRepository teamRepository) {
        this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default");
    }

    public List<UserDTO> findAll() {
        return userRepository.findAllByOrderByDisplayNameAsc().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(UserMapper::toDto);
    }

    @Transactional
    public UserDTO create(UserDTO dto) {
        UserDTO validated = sanitize(dto);
        validate(validated);
        LocalDateTime createdAt = Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock));
        Boolean active = Optional.ofNullable(validated.getActive()).orElse(Boolean.TRUE);

        String passwordHash = buildPasswordHash(validated.getPassword());
        User toSave = new User(null,
                validated.getAzureId(),
                validated.getEmail(),
                validated.getDisplayName(),
                passwordHash,
                validated.getRoleId(),
                validated.getTeamId(),
                active,
                createdAt);
        User saved = Objects.requireNonNull(userRepository.save(toSave), "saved user must not be null");
        return UserMapper.toDto(saved);
    }

    @Transactional
    public Optional<UserDTO> update(Long id, UserDTO dto) {
        UserDTO validated = sanitize(dto);
        validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return userRepository.findById(requiredId)
                .map(existing -> merge(existing, validated))
                .map(userRepository::save)
                .map(UserMapper::toDto);
    }

    @Transactional
    public boolean delete(Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    userRepository.delete(existing);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public AuthResponse loginWithMicrosoft(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken());

        User savedUser = userRepository.findByAzureId(requiredRequest.azureId())
                .map(user -> Objects.requireNonNull(user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()),
                        "updated user must not be null"))
                .orElseGet(() -> registerAzureUser(requiredRequest));

        savedUser = Objects.requireNonNull(userRepository.save(savedUser), "saved user must not be null");

        Instant expiresAt = Instant.now(clock).plusSeconds(3600);
        return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser));
    }

    @Transactional
    public UserSummary register(RegisterRequest request) {
        RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE));
        Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM));

        User user = userRepository.findByAzureId(requiredRequest.azureId())
                .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()))
                .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId))
                .withRoleAndTeam(roleId, teamId);

        if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) {
            user = user.withPasswordHash(hashPassword(requiredRequest.password()));
        }

        User saved = Objects.requireNonNull(userRepository.save(user), "saved user must not be null");
        Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null");

        if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) {
            Agent agent = agentRepository.findByUserId(savedId)
                    .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole()))
                    .orElseGet(() -> Objects.requireNonNull(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"),
                            "agent must not be null"));
            agentRepository.save(Objects.requireNonNull(agent, "agent must not be null"));
        }

        return toSummary(saved);
    }

    private User registerAzureUser(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(DEFAULT_ROLE);
        Long teamId = resolveTeamId(DEFAULT_TEAM);
        return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId);
    }

    private Long resolveRoleId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null");
        return roleRepository.findByName(requiredName)
                .map(Role::getId)
                .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId());
    }

    private Long resolveTeamId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null");
        return teamRepository.findByName(requiredName)
                .map(Team::getId)
                .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId());
    }

    private UserSummary toSummary(User user) {
        User requiredUser = Objects.requireNonNull(user, "user must not be null");
        return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId());
    }

    private String acquireDelegatedToken(String userAccessToken) {
        try {
            ConfidentialClientApplication client = msalClientProvider.createClient();
            UserAssertion assertion = new UserAssertion(userAccessToken);
            OnBehalfOfParameters parameters = OnBehalfOfParameters
                    .builder(scopes, assertion)
                    .build();
            return client.acquireToken(parameters).get().accessToken();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e);
        } catch (MalformedURLException | ExecutionException | MsalException e) {
            throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e);
        }
    }

    private User merge(User existing, UserDTO dto) {
        String passwordHash = existing.getPasswordHash();
        if (StringUtils.hasText(dto.getPassword())) {
            passwordHash = hashPassword(dto.getPassword());
        }

        Boolean active = Optional.ofNullable(dto.getActive()).orElse(Boolean.TRUE);
        LocalDateTime createdAt = Optional.ofNullable(existing.getCreatedAt())
                .orElseGet(() -> Optional.ofNullable(dto.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock)));

        return new User(existing.getId(),
                Optional.ofNullable(dto.getAzureId()).orElse(existing.getAzureId()),
                dto.getEmail(),
                dto.getDisplayName(),
                passwordHash,
                dto.getRoleId(),
                dto.getTeamId(),
                active,
                createdAt);
    }

    private void validate(UserDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("L'email è obbligatoria");
        }
        if (!StringUtils.hasText(dto.getDisplayName())) {
            throw new IllegalArgumentException("Il nome visualizzato è obbligatorio");
        }
        if (dto.getRoleId() == null) {
            throw new IllegalArgumentException("Il ruolo è obbligatorio");
        }
        if (dto.getTeamId() == null) {
            throw new IllegalArgumentException("Il team è obbligatorio");
        }
    }

    private UserDTO sanitize(UserDTO dto) {
        UserDTO validatedDto = Objects.requireNonNull(dto, "user must not be null");
        return new UserDTO(
                validatedDto.getId(),
                normalize(validatedDto.getAzureId()),
                normalize(validatedDto.getEmail()),
                normalize(validatedDto.getDisplayName()),
                validatedDto.getPassword(),
                validatedDto.getRoleId(),
                validatedDto.getTeamId(),
                validatedDto.getActive(),
                validatedDto.getCreatedAt()
        );
    }

    private String buildPasswordHash(String password) {
        if (!StringUtils.hasText(password)) {
            return null;
        }
        return hashPassword(password);
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private Set<String> parseScopes(String scopeExpression) {
        var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+"))
                .filter(token -> !token.isBlank())
                .toList();
        if (tokens.isEmpty()) {
            return Set.of("https://graph.microsoft.com/.default");
        }
        return Set.copyOf(tokens);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo di hashing non disponibile", e);
        }
    }
}
