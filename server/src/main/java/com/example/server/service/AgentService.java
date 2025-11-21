package com.example.server.service;

import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent;
import com.example.server.repository.AgentRepository;
import com.example.server.service.mapper.AgentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<AgentDTO> findAll() {
        return agentRepository.findAllByOrderByAgentCodeAsc().stream()
                .map(AgentMapper::toDto)
                .toList();
    }

    public Optional<AgentDTO> findById(Long id) {
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(AgentMapper::toDto);
    }

    @Transactional
    public AgentDTO create(AgentDTO dto) {
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null");
        validate(validatedDto);
        Agent agent = Objects.requireNonNull(AgentMapper.fromDto(validatedDto), "mapped agent must not be null");
        Agent toSave = Agent.forUser(agent.getUserId(), normalize(agent.getAgentCode()), normalize(agent.getTeamRole()));
        Agent saved = agentRepository.save(toSave);
        return AgentMapper.toDto(saved);
    }

    @Transactional
    public Optional<AgentDTO> update(Long id, AgentDTO dto) {
        AgentDTO validatedDto = Objects.requireNonNull(dto, "agent must not be null");
        validate(validatedDto);
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> new Agent(existing.getId(),
                        validatedDto.getUserId(),
                        normalize(validatedDto.getAgentCode()),
                        normalize(validatedDto.getTeamRole())))
                .map(agentRepository::save)
                .map(AgentMapper::toDto);
    }

    @Transactional
    public boolean delete(Long id) {
        return agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(existing -> {
                    agentRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    public Agent require(Long id) {
        return Objects.requireNonNull(agentRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Agente non trovato")), "agent must not be null");
    }

    private void validate(AgentDTO dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("L'utente associato è obbligatorio");
        }
        if (!StringUtils.hasText(dto.getAgentCode())) {
            throw new IllegalArgumentException("Il codice agente è obbligatorio");
        }
        if (!StringUtils.hasText(dto.getTeamRole())) {
            throw new IllegalArgumentException("Il ruolo nel team è obbligatorio");
        }
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
