package com.example.common.dto;

import java.math.BigDecimal;

/**
 * Aggregato delle provvigioni per team.
 */
public record TeamCommissionDTO(Long teamId,
                                String teamName,
                                BigDecimal commission) {
}
