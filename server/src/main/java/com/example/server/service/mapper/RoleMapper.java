package com.example.server.service.mapper; // Defines the package for role mapping utilities

import com.example.common.dto.RoleDTO; // Imports the DTO representation of a Role
import com.example.server.domain.Role; // Imports the entity representation of a Role

public final class RoleMapper { // Utility class to convert between Role entity and DTO

    private RoleMapper() { // Private constructor to prevent instantiation
    }

    public static RoleDTO toDto(Role role) { // Converts a Role entity to its DTO form
        if (role == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new RoleDTO( // Builds the DTO using entity values
                role.getId(), // Maps the role identifier
                role.getName() // Maps the role name
        );
    }

    public static Role fromDto(RoleDTO dto) { // Converts a RoleDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Role( // Builds the entity using DTO values
                dto.getId(), // Sets the role identifier
                dto.getName() // Sets the role name
        );
    }
}
