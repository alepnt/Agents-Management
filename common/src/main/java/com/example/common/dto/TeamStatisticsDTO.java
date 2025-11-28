package com.example.common.dto; // Package che contiene i DTO condivisi tra client e server.

import java.util.List; // Usato per rappresentare liste di anni e aggregati.

/**
 * Risposta aggregata per le statistiche delle provvigioni per team.
 * Contiene anno corrente, lista anni disponibili e totali provvigioni dei team.
 */
public record TeamStatisticsDTO( // Record immutabile che rappresenta il risultato statistico complessivo.
        int year, // Anno di riferimento selezionato dall’utente.
        List<Integer> years, // Elenco completo degli anni disponibili per la consultazione.
        List<TeamCommissionDTO> teamTotals // Lista aggregata delle provvigioni totali per team.
) {
} // Fine del record TeamStatisticsDTO.
