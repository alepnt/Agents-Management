package com.example.common.dto;                                   // Package contenente i DTO condivisi del sistema.

import java.math.BigDecimal;                                      // Tipo numerico preciso per importi monetari.

/**
 * Rappresenta il totale delle provvigioni calcolate per un mese specifico.
 * Utilizzato nelle statistiche dei singoli agenti e dei team.
 */
public record MonthlyCommissionDTO(                               // Record immutabile che modella un aggregato mensile.
        int year,                                                  // Anno di riferimento (es. 2025).
        int month,                                                 // Mese di riferimento (1–12).
        BigDecimal commission                                       // Totale provvigioni maturate in quel mese.
) {
}                                                                  // Fine del record MonthlyCommissionDTO.
