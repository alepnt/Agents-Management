package com.example.server.service;

import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent;
import com.example.server.repository.AgentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    private AgentService service;

    @BeforeEach
    void setUp() {
        service = new AgentService(agentRepository);
    }

    @Test
    void shouldMapAllAgentsOrderedToDtos() {
        when(agentRepository.findAllByOrderByAgentCodeAsc()).thenReturn(List.of(
                new Agent(1L, 99L, "A002", "Lead"),
                new Agent(2L, 100L, "B003", "Associate")
        ));

        List<AgentDTO> result = service.findAll();

        assertThat(result)
                .extracting(AgentDTO::getId, AgentDTO::getAgentCode, AgentDTO::getTeamRole)
                .containsExactly(
                        org.assertj.core.api.Assertions.tuple(1L, "A002", "Lead"),
                        org.assertj.core.api.Assertions.tuple(2L, "B003", "Associate")
                );
    }

    @Test
    void shouldFindByIdWhenPresent() {
        Agent agent = new Agent(5L, 77L, "C004", "Coordinator");
        when(agentRepository.findById(5L)).thenReturn(Optional.of(agent));

        Optional<AgentDTO> result = service.findById(5L);

        assertThat(result).isPresent();
        assertThat(result.get().getAgentCode()).isEqualTo("C004");
    }

    @Test
    void shouldValidateIdOnFind() {
        assertThatThrownBy(() -> service.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    void shouldCreateAgentNormalizingValues() {
        AgentDTO request = new AgentDTO(null, 9L, "  AG01  ", "  Manager  ");
        when(agentRepository.save(any())).thenAnswer(invocation -> {
            Agent toSave = invocation.getArgument(0);
            return new Agent(10L, toSave.getUserId(), toSave.getAgentCode(), toSave.getTeamRole());
        });

        AgentDTO created = service.create(request);

        ArgumentCaptor<Agent> saved = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(saved.capture());
        assertThat(saved.getValue().getAgentCode()).isEqualTo("AG01");
        assertThat(saved.getValue().getTeamRole()).isEqualTo("Manager");
        assertThat(created.getId()).isEqualTo(10L);
    }

    @Test
    void shouldRejectNullAgentOnCreate() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("agent must not be null");
    }

    @Test
    void shouldRejectMissingUserOnCreate() {
        AgentDTO request = new AgentDTO(null, null, "AG01", "Manager");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'utente associato è obbligatorio");
    }

    @Test
    void shouldRejectBlankAgentCodeOnCreate() {
        AgentDTO request = new AgentDTO(null, 9L, "  ", "Manager");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il codice agente è obbligatorio");
    }

    @Test
    void shouldRejectBlankTeamRoleOnCreate() {
        AgentDTO request = new AgentDTO(null, 9L, "AG01", " ");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il ruolo nel team è obbligatorio");
    }

    @Test
    void shouldUpdateExistingAgentNormalizingValues() {
        Agent existing = new Agent(4L, 7L, "OLD", "OldRole");
        when(agentRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(agentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<AgentDTO> updated = service.update(4L, new AgentDTO(null, 7L, "  NEW  ", "  Lead  "));

        assertThat(updated).isPresent();
        assertThat(updated.get().getAgentCode()).isEqualTo("NEW");
        assertThat(updated.get().getTeamRole()).isEqualTo("Lead");
    }

    @Test
    void shouldNotUpdateWhenAgentNotFound() {
        when(agentRepository.findById(12L)).thenReturn(Optional.empty());

        Optional<AgentDTO> updated = service.update(12L, new AgentDTO(null, 8L, "CODE", "Role"));

        assertThat(updated).isEmpty();
        verify(agentRepository, never()).save(any());
    }

    @Test
    void shouldValidateIdOnUpdate() {
        assertThatThrownBy(() -> service.update(null, new AgentDTO()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    void shouldDeleteWhenPresent() {
        Agent existing = new Agent(15L, 3L, "AG15", "Support");
        when(agentRepository.findById(15L)).thenReturn(Optional.of(existing));

        boolean deleted = service.delete(15L);

        assertThat(deleted).isTrue();
        verify(agentRepository).deleteById(15L);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingAgent() {
        when(agentRepository.findById(16L)).thenReturn(Optional.empty());

        boolean deleted = service.delete(16L);

        assertThat(deleted).isFalse();
        verify(agentRepository, never()).deleteById(any());
    }
}
