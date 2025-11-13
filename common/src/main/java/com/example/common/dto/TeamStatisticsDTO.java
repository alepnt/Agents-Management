package com.example.common.dto;

import java.util.List;

/**
 * Risposta aggregata per le statistiche delle provvigioni per team.
 */
public record TeamStatisticsDTO(int year,
                                List<Integer> years,
                                List<TeamCommissionDTO> teamTotals) {
}
