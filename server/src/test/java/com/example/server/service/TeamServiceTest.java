package com.example.server.service;

import com.example.common.dto.TeamDTO;
import com.example.server.domain.Team;
import com.example.server.repository.TeamRepository;
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
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    private TeamService service;

    @BeforeEach
    void setUp() {
        service = new TeamService(teamRepository);
    }

    @Test
    void shouldMapAllTeamsToDtos() {
        when(teamRepository.findAll()).thenReturn(List.of(
                new Team(1L, "Sales"),
                new Team(2L, "Marketing")
        ));

        List<TeamDTO> result = service.findAll();

        assertThat(result)
                .extracting(TeamDTO::getId, TeamDTO::getName)
                .containsExactly(
                        org.assertj.core.api.Assertions.tuple(1L, "Sales"),
                        org.assertj.core.api.Assertions.tuple(2L, "Marketing")
                );
    }

    @Test
    void shouldFindByIdWhenPresent() {
        Team team = new Team(5L, "Support");
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));

        Optional<TeamDTO> result = service.findById(5L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Support");
    }

    @Test
    void shouldValidateIdOnFind() {
        assertThatThrownBy(() -> service.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("id must not be null");
    }

    @Test
    void shouldCreateTeamNormalizingAndCheckingUniqueness() {
        TeamDTO request = new TeamDTO(null, "  New Team  ");
        when(teamRepository.findByName("New Team"))
                .thenReturn(Optional.empty());
        when(teamRepository.save(any())).thenAnswer(invocation -> {
            Team toSave = invocation.getArgument(0);
            return new Team(99L, toSave.getName());
        });

        TeamDTO created = service.create(request);

        ArgumentCaptor<Team> saved = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("New Team");
        assertThat(created.getId()).isEqualTo(99L);
        assertThat(created.getName()).isEqualTo("New Team");
    }

    @Test
    void shouldRejectBlankTeamName() {
        TeamDTO request = new TeamDTO(null, " ");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il nome del team è obbligatorio");
    }

    @Test
    void shouldRejectDuplicateTeamNameOnCreate() {
        TeamDTO request = new TeamDTO(null, "Existing");
        when(teamRepository.findByName("Existing"))
                .thenReturn(Optional.of(new Team(1L, "Existing")));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Esiste già un team con questo nome");
    }

    @Test
    void shouldUpdateExistingTeam() {
        Team existing = new Team(7L, "Old");
        when(teamRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(teamRepository.findByName("Updated")).thenReturn(Optional.empty());
        when(teamRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<TeamDTO> updated = service.update(7L, new TeamDTO(null, "Updated"));

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated");
    }

    @Test
    void shouldNotUpdateWhenTeamNotFound() {
        when(teamRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<TeamDTO> updated = service.update(3L, new TeamDTO(null, "Whatever"));

        assertThat(updated).isEmpty();
        verify(teamRepository, never()).save(any());
    }

    @Test
    void shouldDetectDuplicateOnUpdateWhenDifferentTeam() {
        Team existing = new Team(4L, "Current");
        when(teamRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(teamRepository.findByName("Existing"))
                .thenReturn(Optional.of(new Team(9L, "Existing")));

        assertThatThrownBy(() -> service.update(4L, new TeamDTO(null, "Existing")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Esiste già un team con questo nome");
    }

    @Test
    void shouldDeleteWhenPresent() {
        Team existing = new Team(11L, "ToRemove");
        when(teamRepository.findById(11L)).thenReturn(Optional.of(existing));

        boolean deleted = service.delete(11L);

        assertThat(deleted).isTrue();
        verify(teamRepository).delete(existing);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingTeam() {
        when(teamRepository.findById(12L)).thenReturn(Optional.empty());

        boolean deleted = service.delete(12L);

        assertThat(deleted).isFalse();
        verify(teamRepository, never()).delete(any());
    }
}
