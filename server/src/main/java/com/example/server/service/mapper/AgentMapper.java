package com.example.server.service.mapper;

import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent;

public final class AgentMapper {

    private AgentMapper() {
    }

    public static AgentDTO toDto(Agent agent) {
        if (agent == null) {
            return null;
        }
        return new AgentDTO(
                agent.getId(),
                agent.getUserId(),
                agent.getAgentCode(),
                agent.getTeamRole()
        );
    }

    public static Agent fromDto(AgentDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Agent(
                dto.getId(),
                dto.getUserId(),
                dto.getAgentCode(),
                dto.getTeamRole()
        );
    }
}
