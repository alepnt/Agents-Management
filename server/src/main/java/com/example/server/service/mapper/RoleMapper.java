package com.example.server.service.mapper;

import com.example.common.dto.RoleDTO;
import com.example.server.domain.Role;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDTO(
                role.getId(),
                role.getName()
        );
    }

    public static Role fromDto(RoleDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Role(
                dto.getId(),
                dto.getName()
        );
    }
}
