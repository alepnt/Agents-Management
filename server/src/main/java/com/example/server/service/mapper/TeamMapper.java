package com.example.server.service.mapper; // Defines the package for team mapping utilities

import com.example.common.dto.TeamDTO; // Imports the DTO representation of a Team
import com.example.server.domain.Team; // Imports the entity representation of a Team

public final class TeamMapper { // Utility class to convert between Team entity and DTO

    private TeamMapper() { // Private constructor to prevent instantiation
    }

    public static TeamDTO toDto(Team team) { // Converts a Team entity to its DTO form
        if (team == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new TeamDTO( // Builds the DTO using entity values
                team.getId(), // Maps the team identifier
                team.getName() // Maps the team name
        );
    }

    public static Team fromDto(TeamDTO dto) { // Converts a TeamDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Team( // Builds the entity using DTO values
                dto.getId(), // Sets the team identifier
                dto.getName() // Sets the team name
        );
    }
}
