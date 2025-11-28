package com.example.server.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgentCommissionShareTest {

    @Test
    @DisplayName("Compact constructor stores provided values")
    void shouldExposeAssignedFields() {
        AgentCommissionShare share = new AgentCommissionShare(5L, new BigDecimal("12.5"), 2);

        assertEquals(5L, share.agentId());
        assertEquals(new BigDecimal("12.5"), share.percentage());
        assertEquals(2, share.ranking());
    }

    @Test
    @DisplayName("Null agentId is rejected")
    void shouldRejectNullAgentId() {
        assertThrows(NullPointerException.class, () -> new AgentCommissionShare(null, BigDecimal.ONE, 1));
    }

    @Test
    @DisplayName("Null percentage is rejected")
    void shouldRejectNullPercentage() {
        assertThrows(NullPointerException.class, () -> new AgentCommissionShare(1L, null, 1));
    }
}
