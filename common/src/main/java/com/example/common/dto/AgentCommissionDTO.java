package com.example.common.dto;

import java.math.BigDecimal;

/**
 * Aggregato delle provvigioni per singolo agente.
 */
public record AgentCommissionDTO(Long agentId,
                                 String agentName,
                                 String teamName,
                                 BigDecimal commission) {
}
