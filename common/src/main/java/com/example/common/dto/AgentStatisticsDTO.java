package com.example.common.dto;                                   // Package che contiene i DTO condivisi fra client e server.

import java.util.List;                                            // Utilizzato per rappresentare collezioni di dati aggregati.

/**
 * Risposta aggregata per le statistiche delle provvigioni per agente.
 * Contiene anno selezionato, anni disponibili, aggregati mensili e totali per agente.
 */
public record AgentStatisticsDTO(                                 // Record immutabile che rappresenta il risultato statistico completo.
        int year,                                                  // Anno di riferimento per le statistiche richieste.
        List<Integer> years,                                       // Elenco degli anni disponibili per la consultazione.
        List<MonthlyCommissionDTO> monthlyTotals,                  // Lista delle provvigioni aggregate per ogni mese dell’anno.
        List<AgentCommissionDTO> agentTotals                       // Lista delle provvigioni aggregate per singolo agente.
) {
}                                                                  // Fine del record AgentStatisticsDTO.
