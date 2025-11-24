package com.example.server.service.mapper;

import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentMapperTest {

    @Test
    void shouldConvertEntityToDto() {
        Agent agent = new Agent(1L, 2L, "AG-01", "LEAD");

        AgentDTO dto = AgentMapper.toDto(agent);

        assertThat(dto)
                .extracting(AgentDTO::getId, AgentDTO::getUserId, AgentDTO::getAgentCode, AgentDTO::getTeamRole)
                .containsExactly(1L, 2L, "AG-01", "LEAD");
    }

    @Test
    void shouldReturnNullDtoWhenEntityIsNull() {
        assertThat(AgentMapper.toDto(null)).isNull();
    }

    @Test
    void shouldConvertDtoToEntity() {
        AgentDTO dto = new AgentDTO(3L, 4L, "AG-99", "MEMBER");

        Agent entity = AgentMapper.fromDto(dto);

        assertThat(entity)
                .extracting(Agent::getId, Agent::getUserId, Agent::getAgentCode, Agent::getTeamRole)
                .containsExactly(3L, 4L, "AG-99", "MEMBER");
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        assertThat(AgentMapper.fromDto(null)).isNull();
    }
}
