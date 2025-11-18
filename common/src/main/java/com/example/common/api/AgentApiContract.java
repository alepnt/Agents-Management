package com.example.common.api;

import com.example.common.dto.AgentDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione degli agenti.
 */
public interface AgentApiContract {

    List<AgentDTO> listAgents();

    Optional<AgentDTO> findById(Long id);

    AgentDTO create(AgentDTO agent);

    AgentDTO update(Long id, AgentDTO agent);

    void delete(Long id);
}
