package com.example.server.service.mapper; // Defines the package for user mapping utilities

import com.example.common.dto.UserDTO; // Imports the DTO representation of a User
import com.example.server.domain.User; // Imports the entity representation of a User

public final class UserMapper { // Utility class to convert between User entity and DTO

    private UserMapper() { // Private constructor to prevent instantiation
    }

    public static UserDTO toDto(User user) { // Converts a User entity to its DTO form
        if (user == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new UserDTO( // Builds the DTO using entity values
                user.getId(), // Maps the user identifier
                user.getAzureId(), // Maps the Azure AD identifier
                user.getEmail(), // Maps the user email
                user.getDisplayName(), // Maps the display name
                null, // Leaves the password unset for security
                user.getRoleId(), // Maps the associated role identifier
                user.getTeamId(), // Maps the associated team identifier
                user.getActive(), // Maps the active status flag
                user.getCreatedAt() // Maps the creation timestamp
        );
    }

    public static User fromDto(UserDTO dto) { // Converts a UserDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new User( // Builds the entity using DTO values
                dto.getId(), // Sets the user identifier
                dto.getAzureId(), // Sets the Azure AD identifier
                dto.getEmail(), // Sets the user email
                dto.getDisplayName(), // Sets the display name
                null, // Leaves the password unset for security
                dto.getRoleId(), // Sets the associated role identifier
                dto.getTeamId(), // Sets the associated team identifier
                dto.getActive(), // Sets the active status flag
                dto.getCreatedAt() // Sets the creation timestamp
        );
    }
}
