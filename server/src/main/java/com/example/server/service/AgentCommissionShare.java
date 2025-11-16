package com.example.server.service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Percentuale assegnata a un agente per la ripartizione della provvigione di team.
 */
public record AgentCommissionShare(Long agentId, BigDecimal percentage, int ranking) {

    public AgentCommissionShare {
        Objects.requireNonNull(agentId, "agentId must not be null");
        Objects.requireNonNull(percentage, "percentage must not be null");
    }
}
