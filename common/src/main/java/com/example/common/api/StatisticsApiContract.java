package com.example.common.api;

import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.TeamStatisticsDTO;

/**
 * Contratto per gli endpoint di statistica sulle provvigioni.
 */
public interface StatisticsApiContract {

    AgentStatisticsDTO agentStatistics(Integer year);

    TeamStatisticsDTO teamStatistics(Integer year);
}
