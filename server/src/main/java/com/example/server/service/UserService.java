package com.example.server.service; // Questa riga gestisce: package com.example.server.service;.
// Riga vuota lasciata per separare meglio le sezioni del file.
import com.example.common.dto.UserDTO; // Questa riga gestisce: import com.example.common.dto.UserDTO;.
import com.example.server.domain.Agent; // Questa riga gestisce: import com.example.server.domain.Agent;.
import com.example.server.domain.Role; // Questa riga gestisce: import com.example.server.domain.Role;.
import com.example.server.domain.Team; // Questa riga gestisce: import com.example.server.domain.Team;.
import com.example.server.domain.User; // Questa riga gestisce: import com.example.server.domain.User;.
import com.example.server.dto.AuthResponse; // Questa riga gestisce: import com.example.server.dto.AuthResponse;.
import com.example.server.dto.LoginRequest; // Questa riga gestisce: import com.example.server.dto.LoginRequest;.
import com.example.server.dto.RegisterRequest; // Questa riga gestisce: import com.example.server.dto.RegisterRequest;.
import com.example.server.dto.UserSummary; // Questa riga gestisce: import com.example.server.dto.UserSummary;.
import com.example.common.dto.RegistrationLookupDTO; // Suggerimenti per la registrazione.
import com.example.server.repository.AgentRepository; // Questa riga gestisce: import com.example.server.repository.AgentRepository;.
import com.example.server.repository.RoleRepository; // Questa riga gestisce: import com.example.server.repository.RoleRepository;.
import com.example.server.repository.TeamRepository; // Questa riga gestisce: import com.example.server.repository.TeamRepository;.
import com.example.server.repository.UserRepository; // Questa riga gestisce: import com.example.server.repository.UserRepository;.
import com.example.server.service.mapper.UserMapper; // Questa riga gestisce: import com.example.server.service.mapper.UserMapper;.
import com.example.server.security.MsalClientProvider; // Questa riga gestisce: import com.example.server.security.MsalClientProvider;.
import com.microsoft.aad.msal4j.ConfidentialClientApplication; // Questa riga gestisce: import com.microsoft.aad.msal4j.ConfidentialClientApplication;.
import com.microsoft.aad.msal4j.MsalException; // Questa riga gestisce: import com.microsoft.aad.msal4j.MsalException;.
import com.microsoft.aad.msal4j.OnBehalfOfParameters; // Questa riga gestisce: import com.microsoft.aad.msal4j.OnBehalfOfParameters;.
import com.microsoft.aad.msal4j.UserAssertion; // Questa riga gestisce: import com.microsoft.aad.msal4j.UserAssertion;.
import org.springframework.beans.factory.annotation.Autowired; // Questa riga gestisce: import org.springframework.beans.factory.annotation.Autowired;.
import org.springframework.beans.factory.annotation.Value; // Questa riga gestisce: import org.springframework.beans.factory.annotation.Value;.
import org.springframework.stereotype.Service; // Questa riga gestisce: import org.springframework.stereotype.Service;.
import org.springframework.transaction.annotation.Transactional; // Questa riga gestisce: import org.springframework.transaction.annotation.Transactional;.
import org.springframework.util.StringUtils; // Questa riga gestisce: import org.springframework.util.StringUtils;.
// Riga vuota lasciata per separare meglio le sezioni del file.
import java.net.MalformedURLException; // Questa riga gestisce: import java.net.MalformedURLException;.
import java.nio.charset.StandardCharsets; // Questa riga gestisce: import java.nio.charset.StandardCharsets;.
import java.security.MessageDigest; // Questa riga gestisce: import java.security.MessageDigest;.
import java.security.NoSuchAlgorithmException; // Questa riga gestisce: import java.security.NoSuchAlgorithmException;.
import java.time.Clock; // Questa riga gestisce: import java.time.Clock;.
import java.time.Instant; // Questa riga gestisce: import java.time.Instant;.
import java.time.LocalDateTime; // Questa riga gestisce: import java.time.LocalDateTime;.
import java.util.List; // Questa riga gestisce: import java.util.List;.
import java.util.Objects; // Questa riga gestisce: import java.util.Objects;.
import java.util.Optional; // Questa riga gestisce: import java.util.Optional;.
import java.util.Set; // Questa riga gestisce: import java.util.Set;.
import java.util.concurrent.ExecutionException; // Questa riga gestisce: import java.util.concurrent.ExecutionException;.
// Riga vuota lasciata per separare meglio le sezioni del file.
@Service // Questa riga gestisce: @Service.
public class UserService { // Questa riga gestisce: public class UserService {.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private static final String DEFAULT_ROLE = "Agent"; // Questa riga gestisce: private static final String DEFAULT_ROLE = "Agent";.
    private static final String DEFAULT_TEAM = "Vendite"; // Questa riga gestisce: private static final String DEFAULT_TEAM = "Vendite";.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private final MsalClientProvider msalClientProvider; // Questa riga gestisce: private final MsalClientProvider msalClientProvider;.
    private final UserRepository userRepository; // Questa riga gestisce: private final UserRepository userRepository;.
    private final AgentRepository agentRepository; // Questa riga gestisce: private final AgentRepository agentRepository;.
    private final RoleRepository roleRepository; // Questa riga gestisce: private final RoleRepository roleRepository;.
    private final TeamRepository teamRepository; // Questa riga gestisce: private final TeamRepository teamRepository;.
    private final Clock clock; // Questa riga gestisce: private final Clock clock;.
    private final Set<String> scopes; // Questa riga gestisce: private final Set<String> scopes;.
    private final String devBypassSecret; // Token di sviluppo per saltare MSAL.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Autowired // Questa riga gestisce: @Autowired.
    public UserService(MsalClientProvider msalClientProvider, // Questa riga gestisce: public UserService(MsalClientProvider msalClientProvider,.
                       UserRepository userRepository, // Questa riga gestisce: UserRepository userRepository,.
                       AgentRepository agentRepository, // Questa riga gestisce: AgentRepository agentRepository,.
                       RoleRepository roleRepository, // Questa riga gestisce: RoleRepository roleRepository,.
                       TeamRepository teamRepository, // Questa riga gestisce: TeamRepository teamRepository,.
                       Clock clock, // Questa riga gestisce: Clock clock,.
                       @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope, // Questa riga gestisce: @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope) {.
                       @Value("${security.azure.dev-bypass-secret:}") String devBypassSecret) { // Segreto usato in sviluppo per saltare MSAL.
        this.msalClientProvider = msalClientProvider; // Questa riga gestisce: this.msalClientProvider = msalClientProvider;.
        this.userRepository = userRepository; // Questa riga gestisce: this.userRepository = userRepository;.
        this.agentRepository = agentRepository; // Questa riga gestisce: this.agentRepository = agentRepository;.
        this.roleRepository = roleRepository; // Questa riga gestisce: this.roleRepository = roleRepository;.
        this.teamRepository = teamRepository; // Questa riga gestisce: this.teamRepository = teamRepository;.
        this.clock = clock; // Questa riga gestisce: this.clock = clock;.
        this.scopes = parseScopes(defaultScope); // Questa riga gestisce: this.scopes = parseScopes(defaultScope);.
        this.devBypassSecret = devBypassSecret; // Conserva il segreto opzionale di bypass.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    public UserService(MsalClientProvider msalClientProvider, // Questa riga gestisce: public UserService(MsalClientProvider msalClientProvider,.
                       UserRepository userRepository, // Questa riga gestisce: UserRepository userRepository,.
                       AgentRepository agentRepository, // Questa riga gestisce: AgentRepository agentRepository,.
                       RoleRepository roleRepository, // Questa riga gestisce: RoleRepository roleRepository,.
                       TeamRepository teamRepository) { // Questa riga gestisce: TeamRepository teamRepository) {.
        this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default", ""); // Questa riga gestisce: this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default", "");.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    public List<UserDTO> findAll() { // Questa riga gestisce: public List<UserDTO> findAll() {.
        return userRepository.findAllByOrderByDisplayNameAsc().stream() // Questa riga gestisce: return userRepository.findAllByOrderByDisplayNameAsc().stream().
                .map(UserMapper::toDto) // Questa riga gestisce: .map(UserMapper::toDto).
                .toList(); // Questa riga gestisce: .toList();.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    public Optional<UserDTO> findById(Long id) { // Questa riga gestisce: public Optional<UserDTO> findById(Long id) {.
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Questa riga gestisce: return userRepository.findById(Objects.requireNonNull(id, "id must not be null")).
                .map(UserMapper::toDto); // Questa riga gestisce: .map(UserMapper::toDto);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Transactional // Questa riga gestisce: @Transactional.
    public UserDTO create(UserDTO dto) { // Questa riga gestisce: public UserDTO create(UserDTO dto) {.
        UserDTO validated = sanitize(dto); // Questa riga gestisce: UserDTO validated = sanitize(dto);.
        validate(validated); // Questa riga gestisce: validate(validated);.
        LocalDateTime createdAt = Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock)); // Questa riga gestisce: LocalDateTime createdAt = Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock));.
        Boolean active = Optional.ofNullable(validated.getActive()).orElse(Boolean.TRUE); // Questa riga gestisce: Boolean active = Optional.ofNullable(validated.getActive()).orElse(Boolean.TRUE);.
// Riga vuota lasciata per separare meglio le sezioni del file.
        String passwordHash = buildPasswordHash(validated.getPassword()); // Questa riga gestisce: String passwordHash = buildPasswordHash(validated.getPassword());.
        User toSave = new User(null, // Questa riga gestisce: User toSave = new User(null,.
                validated.getAzureId(), // Questa riga gestisce: validated.getAzureId(),.
                validated.getEmail(), // Questa riga gestisce: validated.getEmail(),.
                validated.getDisplayName(), // Questa riga gestisce: validated.getDisplayName(),.
                passwordHash, // Questa riga gestisce: passwordHash,.
                validated.getRoleId(), // Questa riga gestisce: validated.getRoleId(),.
                validated.getTeamId(), // Questa riga gestisce: validated.getTeamId(),.
                active, // Questa riga gestisce: active,.
                createdAt); // Questa riga gestisce: createdAt);.
        User saved = Objects.requireNonNull(userRepository.save(toSave), "saved user must not be null"); // Questa riga gestisce: User saved = Objects.requireNonNull(userRepository.save(toSave), "saved user must not be null");.
        return UserMapper.toDto(saved); // Questa riga gestisce: return UserMapper.toDto(saved);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Transactional // Questa riga gestisce: @Transactional.
    public Optional<UserDTO> update(Long id, UserDTO dto) { // Questa riga gestisce: public Optional<UserDTO> update(Long id, UserDTO dto) {.
        UserDTO validated = sanitize(dto); // Questa riga gestisce: UserDTO validated = sanitize(dto);.
        validate(validated); // Questa riga gestisce: validate(validated);.
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Questa riga gestisce: Long requiredId = Objects.requireNonNull(id, "id must not be null");.
        return userRepository.findById(requiredId) // Questa riga gestisce: return userRepository.findById(requiredId).
                .map(existing -> merge(existing, validated)) // Questa riga gestisce: .map(existing -> merge(existing, validated)).
                .map(userRepository::save) // Questa riga gestisce: .map(userRepository::save).
                .map(UserMapper::toDto); // Questa riga gestisce: .map(UserMapper::toDto);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Transactional // Questa riga gestisce: @Transactional.
    public boolean delete(Long id) { // Questa riga gestisce: public boolean delete(Long id) {.
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Questa riga gestisce: return userRepository.findById(Objects.requireNonNull(id, "id must not be null")).
                .map(existing -> { // Questa riga gestisce: .map(existing -> {.
                    User nonNullExisting = Objects.requireNonNull(existing, "user must not be null"); // Questa riga gestisce: User nonNullExisting = Objects.requireNonNull(existing, "user must not be null");.
                    userRepository.delete(nonNullExisting); // Questa riga gestisce: userRepository.delete(nonNullExisting);.
                    return true; // Questa riga gestisce: return true;.
                }) // Questa riga gestisce: }).
                .orElse(false); // Questa riga gestisce: .orElse(false);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Transactional // Questa riga gestisce: @Transactional.
    public AuthResponse loginWithMicrosoft(LoginRequest request) { // Questa riga gestisce: public AuthResponse loginWithMicrosoft(LoginRequest request) {.
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Questa riga gestisce: LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");.
        String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken()); // Questa riga gestisce: String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken());.
// Riga vuota lasciata per separare meglio le sezioni del file.
        User savedUser = userRepository.findByAzureId(requiredRequest.azureId()) // Questa riga gestisce: User savedUser = userRepository.findByAzureId(requiredRequest.azureId()).
                .map(user -> Objects.requireNonNull(user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()), // Questa riga gestisce: .map(user -> Objects.requireNonNull(user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()),.
                        "updated user must not be null")) // Questa riga gestisce: "updated user must not be null")).
                .orElseGet(() -> registerAzureUser(requiredRequest)); // Questa riga gestisce: .orElseGet(() -> registerAzureUser(requiredRequest));.
// Riga vuota lasciata per separare meglio le sezioni del file.
        savedUser = userRepository.save(savedUser); // Questa riga gestisce: savedUser = userRepository.save(savedUser);.
// Riga vuota lasciata per separare meglio le sezioni del file.
        Instant expiresAt = Instant.now(clock).plusSeconds(3600); // Questa riga gestisce: Instant expiresAt = Instant.now(clock).plusSeconds(3600);.
        return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser)); // Questa riga gestisce: return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser));.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    @Transactional // Questa riga gestisce: @Transactional.
    public UserSummary register(RegisterRequest request) { // Questa riga gestisce: public UserSummary register(RegisterRequest request) {.
        RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Questa riga gestisce: RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");.
        Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE)); // Questa riga gestisce: Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE));.
        Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM)); // Questa riga gestisce: Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM));.
// Riga vuota lasciata per separare meglio le sezioni del file.
        User user = userRepository.findByAzureId(requiredRequest.azureId()) // Questa riga gestisce: User user = userRepository.findByAzureId(requiredRequest.azureId()).
                .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email())) // Questa riga gestisce: .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email())).
                .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId)) // Questa riga gestisce: .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId)).
                .withRoleAndTeam(roleId, teamId); // Questa riga gestisce: .withRoleAndTeam(roleId, teamId);.
// Riga vuota lasciata per separare meglio le sezioni del file.
        if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) { // Questa riga gestisce: if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) {.
            user = user.withPasswordHash(hashPassword(requiredRequest.password())); // Questa riga gestisce: user = user.withPasswordHash(hashPassword(requiredRequest.password()));.
        } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
        User saved = userRepository.save(user); // Questa riga gestisce: User saved = userRepository.save(user);.
        Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null"); // Questa riga gestisce: Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null");.
// Riga vuota lasciata per separare meglio le sezioni del file.
        if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) { // Questa riga gestisce: if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) {.
            Agent agent = agentRepository.findByUserId(savedId) // Questa riga gestisce: Agent agent = agentRepository.findByUserId(savedId).
                    .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole())) // Questa riga gestisce: .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole())).
                    .orElseGet(() -> Objects.requireNonNull(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"), // Questa riga gestisce: .orElseGet(() -> Objects.requireNonNull(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"),.
                            "agent must not be null")); // Questa riga gestisce: "agent must not be null"));.
            agentRepository.save(Objects.requireNonNull(agent, "agent must not be null")); // Questa riga gestisce: agentRepository.save(Objects.requireNonNull(agent, "agent must not be null"));.
        } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
        return toSummary(saved); // Questa riga gestisce: return toSummary(saved);.
    } // Questa riga gestisce: }.

    public RegistrationLookupDTO registrationLookups() {
        List<String> azureIds = userRepository.findAllByOrderByDisplayNameAsc().stream()
                .map(User::getAzureId)
                .filter(Objects::nonNull)
                .filter(StringUtils::hasText)
                .toList();

        List<String> agentCodes = agentRepository.findAllByOrderByAgentCodeAsc().stream()
                .map(Agent::getAgentCode)
                .filter(StringUtils::hasText)
                .toList();

        List<String> roles = roleRepository.findAllByOrderByNameAsc().stream()
                .map(Role::getName)
                .filter(StringUtils::hasText)
                .toList();

        List<String> teams = teamRepository.findAllByOrderByNameAsc().stream()
                .map(Team::getName)
                .filter(StringUtils::hasText)
                .toList();

        return new RegistrationLookupDTO(azureIds, agentCodes, teams, roles, suggestNextAgentCode());
    }

    private String suggestNextAgentCode() {
        return agentRepository.findTopByAgentCodeNotNullOrderByAgentCodeDesc()
                .map(Agent::getAgentCode)
                .map(this::incrementAgentCode)
                .orElse("AG001");
    }

    private String incrementAgentCode(String current) {
        String trimmed = current != null ? current.trim() : "";
        if (!trimmed.matches("^[A-Za-z]{2}\\d{3,}$")) {
            return "AG001";
        }
        String prefix = trimmed.substring(0, 2).toUpperCase();
        String numericPart = trimmed.substring(2);
        int value = Integer.parseInt(numericPart) + 1;
        String next = String.format("%03d", value);
        return prefix + next;
    }
// Riga vuota lasciata per separare meglio le sezioni del file.
    private User registerAzureUser(LoginRequest request) { // Questa riga gestisce: private User registerAzureUser(LoginRequest request) {.
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Questa riga gestisce: LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");.
        Long roleId = resolveRoleId(DEFAULT_ROLE); // Questa riga gestisce: Long roleId = resolveRoleId(DEFAULT_ROLE);.
        Long teamId = resolveTeamId(DEFAULT_TEAM); // Questa riga gestisce: Long teamId = resolveTeamId(DEFAULT_TEAM);.
        return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId); // Questa riga gestisce: return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private Long resolveRoleId(String name) { // Questa riga gestisce: private Long resolveRoleId(String name) {.
        String requiredName = Objects.requireNonNull(name, "name must not be null"); // Questa riga gestisce: String requiredName = Objects.requireNonNull(name, "name must not be null");.
        return roleRepository.findByName(requiredName) // Questa riga gestisce: return roleRepository.findByName(requiredName).
                .map(Role::getId) // Questa riga gestisce: .map(Role::getId).
                .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId()); // Questa riga gestisce: .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId());.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private Long resolveTeamId(String name) { // Questa riga gestisce: private Long resolveTeamId(String name) {.
        String requiredName = Objects.requireNonNull(name, "name must not be null"); // Questa riga gestisce: String requiredName = Objects.requireNonNull(name, "name must not be null");.
        return teamRepository.findByName(requiredName) // Questa riga gestisce: return teamRepository.findByName(requiredName).
                .map(Team::getId) // Questa riga gestisce: .map(Team::getId).
                .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId()); // Questa riga gestisce: .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId());.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private UserSummary toSummary(User user) { // Questa riga gestisce: private UserSummary toSummary(User user) {.
        User requiredUser = Objects.requireNonNull(user, "user must not be null"); // Questa riga gestisce: User requiredUser = Objects.requireNonNull(user, "user must not be null");.
        return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId()); // Questa riga gestisce: return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId());.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private String acquireDelegatedToken(String userAccessToken) { // Questa riga gestisce: private String acquireDelegatedToken(String userAccessToken) {.
        if (StringUtils.hasText(devBypassSecret) && devBypassSecret.equals(userAccessToken)) { // Questa riga gestisce: bypass MSAL in dev.
            return "dev-bypass-token"; // Questa riga gestisce: token fittizio per sviluppo.
        } // Questa riga gestisce: }.
        try { // Questa riga gestisce: try {.
            ConfidentialClientApplication client = msalClientProvider.createClient(); // Questa riga gestisce: ConfidentialClientApplication client = msalClientProvider.createClient();.
            UserAssertion assertion = new UserAssertion(userAccessToken); // Questa riga gestisce: UserAssertion assertion = new UserAssertion(userAccessToken);.
            OnBehalfOfParameters parameters = OnBehalfOfParameters // Questa riga gestisce: OnBehalfOfParameters parameters = OnBehalfOfParameters.
                    .builder(scopes, assertion) // Questa riga gestisce: .builder(scopes, assertion).
                    .build(); // Questa riga gestisce: .build();.
            return client.acquireToken(parameters).get().accessToken(); // Questa riga gestisce: return client.acquireToken(parameters).get().accessToken();.
        } catch (InterruptedException e) { // Questa riga gestisce: } catch (InterruptedException e) {.
            Thread.currentThread().interrupt(); // Questa riga gestisce: Thread.currentThread().interrupt();.
            throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e); // Questa riga gestisce: throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e);.
        } catch (MalformedURLException | ExecutionException | MsalException e) { // Questa riga gestisce: } catch (MalformedURLException | ExecutionException | MsalException e) {.
            throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e); // Questa riga gestisce: throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e);.
        } // Questa riga gestisce: }.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private User merge(User existing, UserDTO dto) { // Questa riga gestisce: private User merge(User existing, UserDTO dto) {.
        String passwordHash = existing.getPasswordHash(); // Questa riga gestisce: String passwordHash = existing.getPasswordHash();.
        if (StringUtils.hasText(dto.getPassword())) { // Questa riga gestisce: if (StringUtils.hasText(dto.getPassword())) {.
            passwordHash = hashPassword(dto.getPassword()); // Questa riga gestisce: passwordHash = hashPassword(dto.getPassword());.
        } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
        Boolean active = Optional.ofNullable(dto.getActive()).orElse(Boolean.TRUE); // Questa riga gestisce: Boolean active = Optional.ofNullable(dto.getActive()).orElse(Boolean.TRUE);.
        LocalDateTime createdAt = Optional.ofNullable(existing.getCreatedAt()) // Questa riga gestisce: LocalDateTime createdAt = Optional.ofNullable(existing.getCreatedAt()).
                .orElseGet(() -> Optional.ofNullable(dto.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock))); // Questa riga gestisce: .orElseGet(() -> Optional.ofNullable(dto.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock)));.
// Riga vuota lasciata per separare meglio le sezioni del file.
        return new User(existing.getId(), // Questa riga gestisce: return new User(existing.getId(),.
                Optional.ofNullable(dto.getAzureId()).orElse(existing.getAzureId()), // Questa riga gestisce: Optional.ofNullable(dto.getAzureId()).orElse(existing.getAzureId()),.
                dto.getEmail(), // Questa riga gestisce: dto.getEmail(),.
                dto.getDisplayName(), // Questa riga gestisce: dto.getDisplayName(),.
                passwordHash, // Questa riga gestisce: passwordHash,.
                dto.getRoleId(), // Questa riga gestisce: dto.getRoleId(),.
                dto.getTeamId(), // Questa riga gestisce: dto.getTeamId(),.
                active, // Questa riga gestisce: active,.
                createdAt); // Questa riga gestisce: createdAt);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private void validate(UserDTO dto) { // Questa riga gestisce: private void validate(UserDTO dto) {.
        if (!StringUtils.hasText(dto.getEmail())) { // Questa riga gestisce: if (!StringUtils.hasText(dto.getEmail())) {.
            throw new IllegalArgumentException("L'email è obbligatoria"); // Questa riga gestisce: throw new IllegalArgumentException("L'email è obbligatoria");.
        } // Questa riga gestisce: }.
        if (!StringUtils.hasText(dto.getDisplayName())) { // Questa riga gestisce: if (!StringUtils.hasText(dto.getDisplayName())) {.
            throw new IllegalArgumentException("Il nome visualizzato è obbligatorio"); // Questa riga gestisce: throw new IllegalArgumentException("Il nome visualizzato è obbligatorio");.
        } // Questa riga gestisce: }.
        if (dto.getRoleId() == null) { // Questa riga gestisce: if (dto.getRoleId() == null) {.
            throw new IllegalArgumentException("Il ruolo è obbligatorio"); // Questa riga gestisce: throw new IllegalArgumentException("Il ruolo è obbligatorio");.
        } // Questa riga gestisce: }.
        if (dto.getTeamId() == null) { // Questa riga gestisce: if (dto.getTeamId() == null) {.
            throw new IllegalArgumentException("Il team è obbligatorio"); // Questa riga gestisce: throw new IllegalArgumentException("Il team è obbligatorio");.
        } // Questa riga gestisce: }.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private UserDTO sanitize(UserDTO dto) { // Questa riga gestisce: private UserDTO sanitize(UserDTO dto) {.
        UserDTO validatedDto = Objects.requireNonNull(dto, "user must not be null"); // Questa riga gestisce: UserDTO validatedDto = Objects.requireNonNull(dto, "user must not be null");.
        return new UserDTO( // Questa riga gestisce: return new UserDTO(.
                validatedDto.getId(), // Questa riga gestisce: validatedDto.getId(),.
                normalize(validatedDto.getAzureId()), // Questa riga gestisce: normalize(validatedDto.getAzureId()),.
                normalize(validatedDto.getEmail()), // Questa riga gestisce: normalize(validatedDto.getEmail()),.
                normalize(validatedDto.getDisplayName()), // Questa riga gestisce: normalize(validatedDto.getDisplayName()),.
                validatedDto.getPassword(), // Questa riga gestisce: validatedDto.getPassword(),.
                validatedDto.getRoleId(), // Questa riga gestisce: validatedDto.getRoleId(),.
                validatedDto.getTeamId(), // Questa riga gestisce: validatedDto.getTeamId(),.
                validatedDto.getActive(), // Questa riga gestisce: validatedDto.getActive(),.
                validatedDto.getCreatedAt() // Questa riga gestisce: validatedDto.getCreatedAt().
        ); // Questa riga gestisce: );.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private String buildPasswordHash(String password) { // Questa riga gestisce: private String buildPasswordHash(String password) {.
        if (!StringUtils.hasText(password)) { // Questa riga gestisce: if (!StringUtils.hasText(password)) {.
            return null; // Questa riga gestisce: return null;.
        } // Questa riga gestisce: }.
        return hashPassword(password); // Questa riga gestisce: return hashPassword(password);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private String normalize(String value) { // Questa riga gestisce: private String normalize(String value) {.
        return value != null ? value.trim() : null; // Questa riga gestisce: return value != null ? value.trim() : null;.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private Set<String> parseScopes(String scopeExpression) { // Questa riga gestisce: private Set<String> parseScopes(String scopeExpression) {.
        var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+")) // Questa riga gestisce: var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+")).
                .filter(token -> !token.isBlank()) // Questa riga gestisce: .filter(token -> !token.isBlank()).
                .toList(); // Questa riga gestisce: .toList();.
        if (tokens.isEmpty()) { // Questa riga gestisce: if (tokens.isEmpty()) {.
            return Set.of("https://graph.microsoft.com/.default"); // Questa riga gestisce: return Set.of("https://graph.microsoft.com/.default");.
        } // Questa riga gestisce: }.
        return Set.copyOf(tokens); // Questa riga gestisce: return Set.copyOf(tokens);.
    } // Questa riga gestisce: }.
// Riga vuota lasciata per separare meglio le sezioni del file.
    private String hashPassword(String rawPassword) { // Questa riga gestisce: private String hashPassword(String rawPassword) {.
        try { // Questa riga gestisce: try {.
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Questa riga gestisce: MessageDigest digest = MessageDigest.getInstance("SHA-256");.
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8)); // Questa riga gestisce: byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));.
            return java.util.HexFormat.of().formatHex(hashed); // Questa riga gestisce: return java.util.HexFormat.of().formatHex(hashed);.
        } catch (NoSuchAlgorithmException e) { // Questa riga gestisce: } catch (NoSuchAlgorithmException e) {.
            throw new IllegalStateException("Algoritmo di hashing non disponibile", e); // Questa riga gestisce: throw new IllegalStateException("Algoritmo di hashing non disponibile", e);.
        } // Questa riga gestisce: }.
    } // Questa riga gestisce: }.
} // Questa riga gestisce: }.
