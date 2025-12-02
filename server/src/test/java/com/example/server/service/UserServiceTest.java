package com.example.server.service;

import com.example.server.domain.Agent;
import com.example.server.domain.Role;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.common.dto.RegistrationLookupDTO;
import com.example.server.dto.RegisterRequest;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.MsalClientProvider;
import org.springframework.dao.DataAccessResourceFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private MsalClientProvider msalClientProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TeamRepository teamRepository;

    private Clock clock;
    private UserService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2024-05-01T12:00:00Z"), ZoneOffset.UTC);
        service = new UserService(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository,
                clock, "https://graph.microsoft.com/.default", "");

        when(roleRepository.findByName("Agent")).thenReturn(Optional.of(new Role(1L, "Agent")));
        when(teamRepository.findByName("Vendite")).thenReturn(Optional.of(new Team(2L, "Vendite")));
        when(userRepository.findByAzureId(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> ((User) invocation.getArgument(0)).withId(10L));
        when(agentRepository.findByUserId(10L)).thenReturn(Optional.empty());
    }

    @Test
    void registerFailsWhenAgentCodeAlreadyExists() {
        when(agentRepository.findByAgentCode("AG-001"))
                .thenReturn(Optional.of(new Agent(5L, 20L, "AG-001", "Member")));

        RegisterRequest request = new RegisterRequest(
                "az-1",
                "user@example.com",
                "Nuovo Utente",
                "AG-001",
                "password-123",
                null,
                null);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        verify(agentRepository).findByAgentCode("AG-001");
        verify(agentRepository, never()).save(any());
    }

    @Test
    void registerFailsWhenEmailAlreadyExistsForAnotherUser() {
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(new User(15L, "az-existing", "user@example.com", "Existing User",
                        null, 1L, 1L, true, LocalDateTime.now(clock))));

        RegisterRequest request = new RegisterRequest(
                "az-new",
                "user@example.com",
                "Nuovo Utente",
                "AG-002",
                "password-123",
                null,
                null);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any());
        verify(agentRepository, never()).save(any());
    }

    @Test
    void registrationLookupsReturnFallbackOnDataAccessErrors() {
        when(userRepository.findAllByOrderByDisplayNameAsc())
                .thenThrow(new DataAccessResourceFailureException("database not reachable"));

        RegistrationLookupDTO lookup = service.registrationLookups();

        assertThat(lookup.getAzureIds()).isEmpty();
        assertThat(lookup.getAgentCodes()).isEmpty();
        assertThat(lookup.getRoleNames()).isEmpty();
        assertThat(lookup.getTeamNames()).isEmpty();
        assertThat(lookup.getNextAgentCode()).isEqualTo("AG-001");
    }
}

