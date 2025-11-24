package com.example.server.service.mapper; // Defines the package for mapper utilities

import com.example.common.dto.AgentDTO; // Imports the DTO representation of an Agent
import com.example.server.domain.Agent; // Imports the entity representation of an Agent

public final class AgentMapper { // Utility class to convert between Agent entity and DTO

    private AgentMapper() { // Private constructor to prevent instantiation
    }

    public static AgentDTO toDto(Agent agent) { // Converts an Agent entity to its DTO form
        if (agent == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException by short-circuiting
        }
        return new AgentDTO( // Builds the DTO using entity values
                agent.getId(), // Maps the agent identifier
                agent.getUserId(), // Maps the associated user identifier
                agent.getAgentCode(), // Maps the agent code
                agent.getTeamRole() // Maps the team role
        );
    }

    public static Agent fromDto(AgentDTO dto) { // Converts an AgentDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Agent( // Builds the entity using DTO values
                dto.getId(), // Sets the agent identifier
                dto.getUserId(), // Sets the associated user identifier
                dto.getAgentCode(), // Sets the agent code
                dto.getTeamRole() // Sets the team role
        );
    }
}
