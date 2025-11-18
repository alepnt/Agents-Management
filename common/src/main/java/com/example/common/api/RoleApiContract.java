package com.example.common.api;

import com.example.common.dto.RoleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione dei ruoli.
 */
public interface RoleApiContract {

    List<RoleDTO> listRoles();

    Optional<RoleDTO> findById(Long id);

    RoleDTO create(RoleDTO role);

    RoleDTO update(Long id, RoleDTO role);

    void delete(Long id);
}
