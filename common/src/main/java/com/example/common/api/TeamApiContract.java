package com.example.common.api;

import com.example.common.dto.TeamDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione dei team.
 */
public interface TeamApiContract {

    List<TeamDTO> listTeams();

    Optional<TeamDTO> findById(Long id);

    TeamDTO create(TeamDTO team);

    TeamDTO update(Long id, TeamDTO team);

    void delete(Long id);
}
