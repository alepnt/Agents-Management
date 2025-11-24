package com.example.server.service.mapper;

import com.example.common.dto.AgentDTO;
import com.example.server.domain.Agent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.stream.Stream;

class AgentMapperTest {

    @ParameterizedTest
    @MethodSource("entityToDtoArguments")
    void shouldConvertEntityToDto(Long id, Long userId, String agentCode, String teamRole) {
        Agent agent = new Agent(id, userId, agentCode, teamRole);

        AgentDTO dto = AgentMapper.toDto(agent);

        assertThat(dto)
                .extracting(AgentDTO::getId, AgentDTO::getUserId, AgentDTO::getAgentCode, AgentDTO::getTeamRole)
                .containsExactly(id, userId, agentCode, teamRole);
    }

    @Test
    void shouldReturnNullDtoWhenEntityIsNull() {
        assertThat(AgentMapper.toDto(null)).isNull();
    }

    @ParameterizedTest
    @MethodSource("dtoToEntityArguments")
    void shouldConvertDtoToEntity(Long id, Long userId, String agentCode, String teamRole) {
        AgentDTO dto = new AgentDTO(id, userId, agentCode, teamRole);

        Agent entity = AgentMapper.fromDto(dto);

        assertThat(entity)
                .extracting(Agent::getId, Agent::getUserId, Agent::getAgentCode, Agent::getTeamRole)
                .containsExactly(id, userId, agentCode, teamRole);
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        assertThat(AgentMapper.fromDto(null)).isNull();
    }

    private static Stream<Arguments> entityToDtoArguments() {
        return Stream.of(
                Arguments.of(1L, 2L, "AG-01", "LEAD"),
                Arguments.of(null, 5L, null, "MEMBER"),
                Arguments.of(7L, null, "AG-77", null)
        );
    }

    private static Stream<Arguments> dtoToEntityArguments() {
        return Stream.of(
                Arguments.of(3L, 4L, "AG-99", "MEMBER"),
                Arguments.of(null, null, null, null)
        );
    }
}
