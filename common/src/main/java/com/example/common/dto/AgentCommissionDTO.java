package com.example.common.dto;                               // Package che contiene i DTO condivisi tra client e server.

import java.math.BigDecimal;                                  // Tipo numerico preciso per rappresentare importi monetari.

/**
 * Aggregato delle provvigioni per singolo agente.
 * Contiene valori già calcolati e pronti per la visualizzazione o esportazione.
 */
public record AgentCommissionDTO(                             // Record immutabile che rappresenta le provvigioni per agente.
        Long agentId,                                          // Identificativo univoco dell'agente.
        String agentName,                                      // Nome completo dell'agente.
        String teamName,                                       // Nome del team di appartenenza.
        BigDecimal commission                                   // Valore totale delle provvigioni maturate.
) {
}                                                              // Fine del record AgentCommissionDTO.
