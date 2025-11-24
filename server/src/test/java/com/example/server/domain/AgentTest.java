package com.example.server.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentTest {

    @Test
    void forUserShouldInitializeAgentWithoutId() {
        Agent agent = Agent.forUser(5L, "AG-123", "SUPPORT");

        assertThat(agent.getId()).isNull();
        assertThat(agent.getUserId()).isEqualTo(5L);
        assertThat(agent.getAgentCode()).isEqualTo("AG-123");
        assertThat(agent.getTeamRole()).isEqualTo("SUPPORT");
    }

    @Test
    void equalityShouldBeBasedOnId() {
        Agent first = new Agent(1L, 10L, "AG-001", "LEAD");
        Agent second = new Agent(1L, 11L, "AG-002", "MEMBER");
        Agent third = new Agent(2L, 10L, "AG-001", "LEAD");

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(third);
    }
}
