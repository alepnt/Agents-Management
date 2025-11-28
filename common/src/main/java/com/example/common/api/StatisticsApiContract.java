package com.example.common.api;                               // Package che contiene i contratti API condivisi fra client e server.

import com.example.common.dto.AgentStatisticsDTO;             // DTO contenente le statistiche aggregare per singolo agente.
import com.example.common.dto.TeamStatisticsDTO;              // DTO contenente le statistiche aggregate per team.

/**
 * Contratto per gli endpoint di statistica sulle provvigioni.
 * Fornisce aggregazioni annuali a livello di agente e di team.
 */
public interface StatisticsApiContract {                      // Interfaccia che definisce le operazioni di consultazione statistiche.

    AgentStatisticsDTO agentStatistics(Integer year);          // Restituisce le statistiche annuali aggregate per agente.

    TeamStatisticsDTO teamStatistics(Integer year);            // Restituisce le statistiche annuali aggregate per team.
}                                                              // Fine dell’interfaccia StatisticsApiContract.
