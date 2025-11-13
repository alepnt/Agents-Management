package com.example.common.dto;

import java.math.BigDecimal;

/**
 * Rappresenta il totale delle provvigioni calcolate per un mese specifico.
 */
public record MonthlyCommissionDTO(int year, int month, BigDecimal commission) {
}
