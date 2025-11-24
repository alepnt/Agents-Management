package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import com.example.common.dto.UserDTO; // Commento automatico: import com.example.common.dto.UserDTO;
import com.example.server.domain.Agent; // Commento automatico: import com.example.server.domain.Agent;
import com.example.server.domain.Role; // Commento automatico: import com.example.server.domain.Role;
import com.example.server.domain.Team; // Commento automatico: import com.example.server.domain.Team;
import com.example.server.domain.User; // Commento automatico: import com.example.server.domain.User;
import com.example.server.dto.AuthResponse; // Commento automatico: import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequest; // Commento automatico: import com.example.server.dto.LoginRequest;
import com.example.server.dto.RegisterRequest; // Commento automatico: import com.example.server.dto.RegisterRequest;
import com.example.server.dto.UserSummary; // Commento automatico: import com.example.server.dto.UserSummary;
import com.example.server.repository.AgentRepository; // Commento automatico: import com.example.server.repository.AgentRepository;
import com.example.server.repository.RoleRepository; // Commento automatico: import com.example.server.repository.RoleRepository;
import com.example.server.repository.TeamRepository; // Commento automatico: import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository; // Commento automatico: import com.example.server.repository.UserRepository;
import com.example.server.service.mapper.UserMapper; // Commento automatico: import com.example.server.service.mapper.UserMapper;
import com.example.server.security.MsalClientProvider; // Commento automatico: import com.example.server.security.MsalClientProvider;
import com.microsoft.aad.msal4j.ConfidentialClientApplication; // Commento automatico: import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.MsalException; // Commento automatico: import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.OnBehalfOfParameters; // Commento automatico: import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion; // Commento automatico: import com.microsoft.aad.msal4j.UserAssertion;
import org.springframework.beans.factory.annotation.Autowired; // Commento automatico: import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Commento automatico: import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service; // Commento automatico: import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Commento automatico: import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Commento automatico: import org.springframework.util.StringUtils;
// Spazio commentato per leggibilità
import java.net.MalformedURLException; // Commento automatico: import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets; // Commento automatico: import java.nio.charset.StandardCharsets;
import java.security.MessageDigest; // Commento automatico: import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; // Commento automatico: import java.security.NoSuchAlgorithmException;
import java.time.Clock; // Commento automatico: import java.time.Clock;
import java.time.Instant; // Commento automatico: import java.time.Instant;
import java.time.LocalDateTime; // Commento automatico: import java.time.LocalDateTime;
import java.util.List; // Commento automatico: import java.util.List;
import java.util.Objects; // Commento automatico: import java.util.Objects;
import java.util.Optional; // Commento automatico: import java.util.Optional;
import java.util.Set; // Commento automatico: import java.util.Set;
import java.util.concurrent.ExecutionException; // Commento automatico: import java.util.concurrent.ExecutionException;
// Spazio commentato per leggibilità
@Service // Commento automatico: @Service
public class UserService { // Commento automatico: public class UserService {
// Spazio commentato per leggibilità
    private static final String DEFAULT_ROLE = "Agent"; // Commento automatico: private static final String DEFAULT_ROLE = "Agent";
    private static final String DEFAULT_TEAM = "Vendite"; // Commento automatico: private static final String DEFAULT_TEAM = "Vendite";
// Spazio commentato per leggibilità
    private final MsalClientProvider msalClientProvider; // Commento automatico: private final MsalClientProvider msalClientProvider;
    private final UserRepository userRepository; // Commento automatico: private final UserRepository userRepository;
    private final AgentRepository agentRepository; // Commento automatico: private final AgentRepository agentRepository;
    private final RoleRepository roleRepository; // Commento automatico: private final RoleRepository roleRepository;
    private final TeamRepository teamRepository; // Commento automatico: private final TeamRepository teamRepository;
    private final Clock clock; // Commento automatico: private final Clock clock;
    private final Set<String> scopes; // Commento automatico: private final Set<String> scopes;
// Spazio commentato per leggibilità
    @Autowired // Commento automatico: @Autowired
    public UserService(MsalClientProvider msalClientProvider, // Commento automatico: public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository, // Commento automatico: UserRepository userRepository,
                       AgentRepository agentRepository, // Commento automatico: AgentRepository agentRepository,
                       RoleRepository roleRepository, // Commento automatico: RoleRepository roleRepository,
                       TeamRepository teamRepository, // Commento automatico: TeamRepository teamRepository,
                       Clock clock, // Commento automatico: Clock clock,
                       @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope) { // Commento automatico: @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope) {
        this.msalClientProvider = msalClientProvider; // Commento automatico: this.msalClientProvider = msalClientProvider;
        this.userRepository = userRepository; // Commento automatico: this.userRepository = userRepository;
        this.agentRepository = agentRepository; // Commento automatico: this.agentRepository = agentRepository;
        this.roleRepository = roleRepository; // Commento automatico: this.roleRepository = roleRepository;
        this.teamRepository = teamRepository; // Commento automatico: this.teamRepository = teamRepository;
        this.clock = clock; // Commento automatico: this.clock = clock;
        this.scopes = parseScopes(defaultScope); // Commento automatico: this.scopes = parseScopes(defaultScope);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public UserService(MsalClientProvider msalClientProvider, // Commento automatico: public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository, // Commento automatico: UserRepository userRepository,
                       AgentRepository agentRepository, // Commento automatico: AgentRepository agentRepository,
                       RoleRepository roleRepository, // Commento automatico: RoleRepository roleRepository,
                       TeamRepository teamRepository) { // Commento automatico: TeamRepository teamRepository) {
        this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default"); // Commento automatico: this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default");
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public List<UserDTO> findAll() { // Commento automatico: public List<UserDTO> findAll() {
        return userRepository.findAllByOrderByDisplayNameAsc().stream() // Commento automatico: return userRepository.findAllByOrderByDisplayNameAsc().stream()
                .map(UserMapper::toDto) // Commento automatico: .map(UserMapper::toDto)
                .toList(); // Commento automatico: .toList();
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public Optional<UserDTO> findById(Long id) { // Commento automatico: public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return userRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(UserMapper::toDto); // Commento automatico: .map(UserMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public UserDTO create(UserDTO dto) { // Commento automatico: public UserDTO create(UserDTO dto) {
        UserDTO validated = sanitize(dto); // Commento automatico: UserDTO validated = sanitize(dto);
        validate(validated); // Commento automatico: validate(validated);
        LocalDateTime createdAt = Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock)); // Commento automatico: LocalDateTime createdAt = Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock));
        Boolean active = Optional.ofNullable(validated.getActive()).orElse(Boolean.TRUE); // Commento automatico: Boolean active = Optional.ofNullable(validated.getActive()).orElse(Boolean.TRUE);
// Spazio commentato per leggibilità
        String passwordHash = buildPasswordHash(validated.getPassword()); // Commento automatico: String passwordHash = buildPasswordHash(validated.getPassword());
        User toSave = new User(null, // Commento automatico: User toSave = new User(null,
                validated.getAzureId(), // Commento automatico: validated.getAzureId(),
                validated.getEmail(), // Commento automatico: validated.getEmail(),
                validated.getDisplayName(), // Commento automatico: validated.getDisplayName(),
                passwordHash, // Commento automatico: passwordHash,
                validated.getRoleId(), // Commento automatico: validated.getRoleId(),
                validated.getTeamId(), // Commento automatico: validated.getTeamId(),
                active, // Commento automatico: active,
                createdAt); // Commento automatico: createdAt);
        User saved = Objects.requireNonNull(userRepository.save(toSave), "saved user must not be null"); // Commento automatico: User saved = Objects.requireNonNull(userRepository.save(toSave), "saved user must not be null");
        return UserMapper.toDto(saved); // Commento automatico: return UserMapper.toDto(saved);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public Optional<UserDTO> update(Long id, UserDTO dto) { // Commento automatico: public Optional<UserDTO> update(Long id, UserDTO dto) {
        UserDTO validated = sanitize(dto); // Commento automatico: UserDTO validated = sanitize(dto);
        validate(validated); // Commento automatico: validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Commento automatico: Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return userRepository.findById(requiredId) // Commento automatico: return userRepository.findById(requiredId)
                .map(existing -> merge(existing, validated)) // Commento automatico: .map(existing -> merge(existing, validated))
                .map(userRepository::save) // Commento automatico: .map(userRepository::save)
                .map(UserMapper::toDto); // Commento automatico: .map(UserMapper::toDto);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public boolean delete(Long id) { // Commento automatico: public boolean delete(Long id) {
        return userRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Commento automatico: return userRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> { // Commento automatico: .map(existing -> {
                    User nonNullExisting = Objects.requireNonNull(existing, "user must not be null"); // Commento automatico: User nonNullExisting = Objects.requireNonNull(existing, "user must not be null");
                    userRepository.delete(nonNullExisting); // Commento automatico: userRepository.delete(nonNullExisting);
                    return true; // Commento automatico: return true;
                }) // Commento automatico: })
                .orElse(false); // Commento automatico: .orElse(false);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public AuthResponse loginWithMicrosoft(LoginRequest request) { // Commento automatico: public AuthResponse loginWithMicrosoft(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Commento automatico: LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken()); // Commento automatico: String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken());
// Spazio commentato per leggibilità
        User savedUser = userRepository.findByAzureId(requiredRequest.azureId()) // Commento automatico: User savedUser = userRepository.findByAzureId(requiredRequest.azureId())
                .map(user -> Objects.requireNonNull(user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()), // Commento automatico: .map(user -> Objects.requireNonNull(user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()),
                        "updated user must not be null")) // Commento automatico: "updated user must not be null"))
                .orElseGet(() -> registerAzureUser(requiredRequest)); // Commento automatico: .orElseGet(() -> registerAzureUser(requiredRequest));
// Spazio commentato per leggibilità
        savedUser = userRepository.save(savedUser); // Commento automatico: savedUser = userRepository.save(savedUser);
// Spazio commentato per leggibilità
        Instant expiresAt = Instant.now(clock).plusSeconds(3600); // Commento automatico: Instant expiresAt = Instant.now(clock).plusSeconds(3600);
        return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser)); // Commento automatico: return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser));
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @Transactional // Commento automatico: @Transactional
    public UserSummary register(RegisterRequest request) { // Commento automatico: public UserSummary register(RegisterRequest request) {
        RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Commento automatico: RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE)); // Commento automatico: Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE));
        Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM)); // Commento automatico: Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM));
// Spazio commentato per leggibilità
        User user = userRepository.findByAzureId(requiredRequest.azureId()) // Commento automatico: User user = userRepository.findByAzureId(requiredRequest.azureId())
                .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email())) // Commento automatico: .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()))
                .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId)) // Commento automatico: .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId))
                .withRoleAndTeam(roleId, teamId); // Commento automatico: .withRoleAndTeam(roleId, teamId);
// Spazio commentato per leggibilità
        if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) { // Commento automatico: if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) {
            user = user.withPasswordHash(hashPassword(requiredRequest.password())); // Commento automatico: user = user.withPasswordHash(hashPassword(requiredRequest.password()));
        } // Commento automatico: }
// Spazio commentato per leggibilità
        User saved = userRepository.save(user); // Commento automatico: User saved = userRepository.save(user);
        Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null"); // Commento automatico: Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null");
// Spazio commentato per leggibilità
        if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) { // Commento automatico: if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) {
            Agent agent = agentRepository.findByUserId(savedId) // Commento automatico: Agent agent = agentRepository.findByUserId(savedId)
                    .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole())) // Commento automatico: .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole()))
                    .orElseGet(() -> Objects.requireNonNull(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"), // Commento automatico: .orElseGet(() -> Objects.requireNonNull(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"),
                            "agent must not be null")); // Commento automatico: "agent must not be null"));
            agentRepository.save(Objects.requireNonNull(agent, "agent must not be null")); // Commento automatico: agentRepository.save(Objects.requireNonNull(agent, "agent must not be null"));
        } // Commento automatico: }
// Spazio commentato per leggibilità
        return toSummary(saved); // Commento automatico: return toSummary(saved);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private User registerAzureUser(LoginRequest request) { // Commento automatico: private User registerAzureUser(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Commento automatico: LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(DEFAULT_ROLE); // Commento automatico: Long roleId = resolveRoleId(DEFAULT_ROLE);
        Long teamId = resolveTeamId(DEFAULT_TEAM); // Commento automatico: Long teamId = resolveTeamId(DEFAULT_TEAM);
        return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId); // Commento automatico: return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private Long resolveRoleId(String name) { // Commento automatico: private Long resolveRoleId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null"); // Commento automatico: String requiredName = Objects.requireNonNull(name, "name must not be null");
        return roleRepository.findByName(requiredName) // Commento automatico: return roleRepository.findByName(requiredName)
                .map(Role::getId) // Commento automatico: .map(Role::getId)
                .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId()); // Commento automatico: .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId());
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private Long resolveTeamId(String name) { // Commento automatico: private Long resolveTeamId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null"); // Commento automatico: String requiredName = Objects.requireNonNull(name, "name must not be null");
        return teamRepository.findByName(requiredName) // Commento automatico: return teamRepository.findByName(requiredName)
                .map(Team::getId) // Commento automatico: .map(Team::getId)
                .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId()); // Commento automatico: .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId());
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private UserSummary toSummary(User user) { // Commento automatico: private UserSummary toSummary(User user) {
        User requiredUser = Objects.requireNonNull(user, "user must not be null"); // Commento automatico: User requiredUser = Objects.requireNonNull(user, "user must not be null");
        return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId()); // Commento automatico: return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId());
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String acquireDelegatedToken(String userAccessToken) { // Commento automatico: private String acquireDelegatedToken(String userAccessToken) {
        try { // Commento automatico: try {
            ConfidentialClientApplication client = msalClientProvider.createClient(); // Commento automatico: ConfidentialClientApplication client = msalClientProvider.createClient();
            UserAssertion assertion = new UserAssertion(userAccessToken); // Commento automatico: UserAssertion assertion = new UserAssertion(userAccessToken);
            OnBehalfOfParameters parameters = OnBehalfOfParameters // Commento automatico: OnBehalfOfParameters parameters = OnBehalfOfParameters
                    .builder(scopes, assertion) // Commento automatico: .builder(scopes, assertion)
                    .build(); // Commento automatico: .build();
            return client.acquireToken(parameters).get().accessToken(); // Commento automatico: return client.acquireToken(parameters).get().accessToken();
        } catch (InterruptedException e) { // Commento automatico: } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Commento automatico: Thread.currentThread().interrupt();
            throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e); // Commento automatico: throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e);
        } catch (MalformedURLException | ExecutionException | MsalException e) { // Commento automatico: } catch (MalformedURLException | ExecutionException | MsalException e) {
            throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e); // Commento automatico: throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e);
        } // Commento automatico: }
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private User merge(User existing, UserDTO dto) { // Commento automatico: private User merge(User existing, UserDTO dto) {
        String passwordHash = existing.getPasswordHash(); // Commento automatico: String passwordHash = existing.getPasswordHash();
        if (StringUtils.hasText(dto.getPassword())) { // Commento automatico: if (StringUtils.hasText(dto.getPassword())) {
            passwordHash = hashPassword(dto.getPassword()); // Commento automatico: passwordHash = hashPassword(dto.getPassword());
        } // Commento automatico: }
// Spazio commentato per leggibilità
        Boolean active = Optional.ofNullable(dto.getActive()).orElse(Boolean.TRUE); // Commento automatico: Boolean active = Optional.ofNullable(dto.getActive()).orElse(Boolean.TRUE);
        LocalDateTime createdAt = Optional.ofNullable(existing.getCreatedAt()) // Commento automatico: LocalDateTime createdAt = Optional.ofNullable(existing.getCreatedAt())
                .orElseGet(() -> Optional.ofNullable(dto.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock))); // Commento automatico: .orElseGet(() -> Optional.ofNullable(dto.getCreatedAt()).orElseGet(() -> LocalDateTime.now(clock)));
// Spazio commentato per leggibilità
        return new User(existing.getId(), // Commento automatico: return new User(existing.getId(),
                Optional.ofNullable(dto.getAzureId()).orElse(existing.getAzureId()), // Commento automatico: Optional.ofNullable(dto.getAzureId()).orElse(existing.getAzureId()),
                dto.getEmail(), // Commento automatico: dto.getEmail(),
                dto.getDisplayName(), // Commento automatico: dto.getDisplayName(),
                passwordHash, // Commento automatico: passwordHash,
                dto.getRoleId(), // Commento automatico: dto.getRoleId(),
                dto.getTeamId(), // Commento automatico: dto.getTeamId(),
                active, // Commento automatico: active,
                createdAt); // Commento automatico: createdAt);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private void validate(UserDTO dto) { // Commento automatico: private void validate(UserDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) { // Commento automatico: if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("L'email è obbligatoria"); // Commento automatico: throw new IllegalArgumentException("L'email è obbligatoria");
        } // Commento automatico: }
        if (!StringUtils.hasText(dto.getDisplayName())) { // Commento automatico: if (!StringUtils.hasText(dto.getDisplayName())) {
            throw new IllegalArgumentException("Il nome visualizzato è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il nome visualizzato è obbligatorio");
        } // Commento automatico: }
        if (dto.getRoleId() == null) { // Commento automatico: if (dto.getRoleId() == null) {
            throw new IllegalArgumentException("Il ruolo è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il ruolo è obbligatorio");
        } // Commento automatico: }
        if (dto.getTeamId() == null) { // Commento automatico: if (dto.getTeamId() == null) {
            throw new IllegalArgumentException("Il team è obbligatorio"); // Commento automatico: throw new IllegalArgumentException("Il team è obbligatorio");
        } // Commento automatico: }
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private UserDTO sanitize(UserDTO dto) { // Commento automatico: private UserDTO sanitize(UserDTO dto) {
        UserDTO validatedDto = Objects.requireNonNull(dto, "user must not be null"); // Commento automatico: UserDTO validatedDto = Objects.requireNonNull(dto, "user must not be null");
        return new UserDTO( // Commento automatico: return new UserDTO(
                validatedDto.getId(), // Commento automatico: validatedDto.getId(),
                normalize(validatedDto.getAzureId()), // Commento automatico: normalize(validatedDto.getAzureId()),
                normalize(validatedDto.getEmail()), // Commento automatico: normalize(validatedDto.getEmail()),
                normalize(validatedDto.getDisplayName()), // Commento automatico: normalize(validatedDto.getDisplayName()),
                validatedDto.getPassword(), // Commento automatico: validatedDto.getPassword(),
                validatedDto.getRoleId(), // Commento automatico: validatedDto.getRoleId(),
                validatedDto.getTeamId(), // Commento automatico: validatedDto.getTeamId(),
                validatedDto.getActive(), // Commento automatico: validatedDto.getActive(),
                validatedDto.getCreatedAt() // Commento automatico: validatedDto.getCreatedAt()
        ); // Commento automatico: );
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String buildPasswordHash(String password) { // Commento automatico: private String buildPasswordHash(String password) {
        if (!StringUtils.hasText(password)) { // Commento automatico: if (!StringUtils.hasText(password)) {
            return null; // Commento automatico: return null;
        } // Commento automatico: }
        return hashPassword(password); // Commento automatico: return hashPassword(password);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String normalize(String value) { // Commento automatico: private String normalize(String value) {
        return value != null ? value.trim() : null; // Commento automatico: return value != null ? value.trim() : null;
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private Set<String> parseScopes(String scopeExpression) { // Commento automatico: private Set<String> parseScopes(String scopeExpression) {
        var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+")) // Commento automatico: var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+"))
                .filter(token -> !token.isBlank()) // Commento automatico: .filter(token -> !token.isBlank())
                .toList(); // Commento automatico: .toList();
        if (tokens.isEmpty()) { // Commento automatico: if (tokens.isEmpty()) {
            return Set.of("https://graph.microsoft.com/.default"); // Commento automatico: return Set.of("https://graph.microsoft.com/.default");
        } // Commento automatico: }
        return Set.copyOf(tokens); // Commento automatico: return Set.copyOf(tokens);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    private String hashPassword(String rawPassword) { // Commento automatico: private String hashPassword(String rawPassword) {
        try { // Commento automatico: try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Commento automatico: MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8)); // Commento automatico: byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hashed); // Commento automatico: return java.util.HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) { // Commento automatico: } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo di hashing non disponibile", e); // Commento automatico: throw new IllegalStateException("Algoritmo di hashing non disponibile", e);
        } // Commento automatico: }
    } // Commento automatico: }
} // Commento automatico: }
