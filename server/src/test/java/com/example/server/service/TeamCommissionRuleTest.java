package com.example.server.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamCommissionRuleTest {

    @Test
    void singleAgentFactoryCopiesValues() {
        BigDecimal rate = new BigDecimal("0.15");
        TeamCommissionRule rule = TeamCommissionRule.singleAgent(5L, rate);

        assertNull(rule.teamId());
        assertEquals(rate, rule.teamCommissionRate());
        assertEquals(DistributionStrategy.PERCENTAGE, rule.distributionStrategy());
        assertEquals(1, rule.shares().size());

        AgentCommissionShare share = rule.shares().get(0);
        assertEquals(5L, share.agentId());
        assertEquals(rate, share.percentage());
        assertEquals(0, share.ranking());
    }

    @Test
    void commissionRuleRequiresNonNullArguments() {
        assertThrows(NullPointerException.class, () -> new TeamCommissionRule(1L, null,
                DistributionStrategy.PERCENTAGE, List.of()));
        assertThrows(NullPointerException.class, () -> new TeamCommissionRule(1L, BigDecimal.ONE,
                null, List.of()));
        assertThrows(NullPointerException.class, () -> new TeamCommissionRule(1L, BigDecimal.ONE,
                DistributionStrategy.BARRIER, null));
    }

    @Test
    void agentCommissionShareValidatesInputs() {
        NullPointerException agentException = assertThrows(NullPointerException.class,
                () -> new AgentCommissionShare(null, BigDecimal.ONE, 1));
        assertEquals("agentId must not be null", agentException.getMessage());

        NullPointerException percentageException = assertThrows(NullPointerException.class,
                () -> new AgentCommissionShare(2L, null, 1));
        assertEquals("percentage must not be null", percentageException.getMessage());
    }
}
