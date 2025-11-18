package com.example.server.service;

import com.example.common.dto.TeamDTO;
import com.example.server.domain.Team;
import com.example.server.repository.TeamRepository;
import com.example.server.service.mapper.TeamMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamDTO> findAll() {
        return StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                .map(TeamMapper::toDto)
                .toList();
    }

    public Optional<TeamDTO> findById(Long id) {
        return teamRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(TeamMapper::toDto);
    }

    @Transactional
    public TeamDTO create(TeamDTO dto) {
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null");
        validate(validated);
        String normalizedName = normalize(validated.getName());
        ensureUniqueName(normalizedName, null);
        Team toSave = new Team(null, normalizedName);
        Team saved = teamRepository.save(toSave);
        return TeamMapper.toDto(saved);
    }

    @Transactional
    public Optional<TeamDTO> update(Long id, TeamDTO dto) {
        TeamDTO validated = Objects.requireNonNull(dto, "team must not be null");
        validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        String normalizedName = normalize(validated.getName());
        return teamRepository.findById(requiredId)
                .map(existing -> {
                    ensureUniqueName(normalizedName, requiredId);
                    Team toSave = new Team(existing.getId(), normalizedName);
                    return TeamMapper.toDto(teamRepository.save(toSave));
                });
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return teamRepository.findById(requiredId)
                .map(existing -> {
                    teamRepository.delete(existing);
                    return true;
                })
                .orElse(false);
    }

    private void validate(TeamDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio");
        }
    }

    private void ensureUniqueName(String name, Long currentId) {
        teamRepository.findByName(name)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Esiste già un team con questo nome");
                });
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
