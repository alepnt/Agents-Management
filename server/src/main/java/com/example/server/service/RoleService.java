package com.example.server.service;

import com.example.common.dto.RoleDTO;
import com.example.server.domain.Role;
import com.example.server.repository.RoleRepository;
import com.example.server.service.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> findAll() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false)
                .map(RoleMapper::toDto)
                .toList();
    }

    public Optional<RoleDTO> findById(Long id) {
        return roleRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(RoleMapper::toDto);
    }

    @Transactional
    public RoleDTO create(RoleDTO dto) {
        RoleDTO validated = Objects.requireNonNull(dto, "role must not be null");
        validate(validated);
        String normalizedName = normalize(validated.getName());
        ensureUniqueName(normalizedName, null);
        Role toSave = new Role(null, normalizedName);
        Role saved = roleRepository.save(toSave);
        return RoleMapper.toDto(saved);
    }

    @Transactional
    public Optional<RoleDTO> update(Long id, RoleDTO dto) {
        RoleDTO validated = Objects.requireNonNull(dto, "role must not be null");
        validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        String normalizedName = normalize(validated.getName());
        return roleRepository.findById(requiredId)
                .map(existing -> {
                    ensureUniqueName(normalizedName, requiredId);
                    Role toSave = new Role(existing.getId(), normalizedName);
                    return RoleMapper.toDto(roleRepository.save(toSave));
                });
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return roleRepository.findById(requiredId)
                .map(existing -> {
                    Role nonNullExisting = Objects.requireNonNull(existing, "role must not be null");
                    roleRepository.delete(nonNullExisting);
                    return true;
                })
                .orElse(false);
    }

    private void validate(RoleDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Il nome del ruolo è obbligatorio");
        }
    }

    private void ensureUniqueName(String name, Long currentId) {
        roleRepository.findByName(name)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Esiste già un ruolo con questo nome");
                });
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
