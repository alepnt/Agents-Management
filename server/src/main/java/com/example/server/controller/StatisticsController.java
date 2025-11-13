package com.example.server.controller;

import com.example.common.api.StatisticsApiContract;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.server.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController implements StatisticsApiContract {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    @GetMapping("/agent")
    public AgentStatisticsDTO agentStatistics(@RequestParam(value = "year", required = false) Integer year) {
        return statisticsService.agentStatistics(year);
    }

    @Override
    @GetMapping("/team")
    public TeamStatisticsDTO teamStatistics(@RequestParam(value = "year", required = false) Integer year) {
        return statisticsService.teamStatistics(year);
    }
}
