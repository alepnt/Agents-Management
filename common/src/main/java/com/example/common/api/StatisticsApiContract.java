package com.example.common.api;                               // Package che contiene i contratti API condivisi fra client e server.

import com.example.common.dto.AgentStatisticsDTO;             // DTO contenente le statistiche aggregare per singolo agente.
import com.example.common.dto.TeamStatisticsDTO;              // DTO contenente le statistiche aggregate per team.

import java.time.LocalDate;                                   // Utilizzato per filtrare per periodo.

/**
 * Contratto per gli endpoint di statistica sulle provvigioni.
 * Fornisce aggregazioni annuali a livello di agente e di team.
 */
public interface StatisticsApiContract {                      // Interfaccia che definisce le operazioni di consultazione statistiche.

    AgentStatisticsDTO agentStatistics(Integer year,           // Restituisce le statistiche aggregate per agente.
                                       LocalDate from,         // Data di inizio filtro (inclusa), opzionale.
                                       LocalDate to,           // Data di fine filtro (inclusa), opzionale.
                                       Long roleId);           // Ruolo agente su cui filtrare, opzionale.

    TeamStatisticsDTO teamStatistics(Integer year,             // Restituisce le statistiche aggregate per team.
                                     LocalDate from,           // Data di inizio filtro (inclusa), opzionale.
                                     LocalDate to,             // Data di fine filtro (inclusa), opzionale.
                                     Long roleId);             // Ruolo agente su cui filtrare, opzionale.
}                                                              // Fine dell’interfaccia StatisticsApiContract.
