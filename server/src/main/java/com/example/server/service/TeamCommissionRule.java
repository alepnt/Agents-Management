package com.example.server.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Regola di calcolo della provvigione per un team e ripartizione tra gli agenti.
 */
public record TeamCommissionRule(Long teamId,
                                 BigDecimal teamCommissionRate,
                                 DistributionStrategy distributionStrategy,
                                 List<AgentCommissionShare> shares) {

    public TeamCommissionRule {
        Objects.requireNonNull(teamCommissionRate, "teamCommissionRate must not be null");
        Objects.requireNonNull(distributionStrategy, "distributionStrategy must not be null");
        Objects.requireNonNull(shares, "shares must not be null");
    }

    public static TeamCommissionRule singleAgent(Long agentId, BigDecimal commissionRate) {
        return new TeamCommissionRule(null, commissionRate, DistributionStrategy.PERCENTAGE,
                List.of(new AgentCommissionShare(agentId, commissionRate, 0)));
    }
}
