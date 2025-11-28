package com.example.common.dto; // Package che ospita i DTO condivisi tra client e server.

import java.math.BigDecimal; // Tipo numerico preciso per importi monetari.

/**
 * Aggregato delle provvigioni per team.
 * Utilizzato nelle statistiche per rappresentare il totale maturato da ciascun
 * team.
 */
public record TeamCommissionDTO( // Record immutabile che rappresenta il totale provvigioni per team.
        Long teamId, // Identificatore univoco del team.
        String teamName, // Nome del team.
        BigDecimal commission // Totale delle provvigioni maturate dal team.
) {
} // Fine del record TeamCommissionDTO.
