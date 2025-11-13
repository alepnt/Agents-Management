package com.example.common.dto;

import java.util.List;

/**
 * Risposta aggregata per le statistiche delle provvigioni per agente.
 */
public record AgentStatisticsDTO(int year,
                                 List<Integer> years,
                                 List<MonthlyCommissionDTO> monthlyTotals,
                                 List<AgentCommissionDTO> agentTotals) {
}
