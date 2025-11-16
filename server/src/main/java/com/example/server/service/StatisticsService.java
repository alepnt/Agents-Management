package com.example.server.service;

import com.example.common.dto.AgentCommissionDTO;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.MonthlyCommissionDTO;
import com.example.common.dto.TeamCommissionDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.enums.InvoiceStatus;
import com.example.server.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticsService {

    private static final String PAID_STATUS = InvoiceStatus.PAID.name();

    private final StatisticsRepository statisticsRepository;
    private final CommissionService commissionService;
    private final Map<Integer, AgentStatisticsDTO> agentCache = new ConcurrentHashMap<>();
    private final Map<Integer, TeamStatisticsDTO> teamCache = new ConcurrentHashMap<>();

    public StatisticsService(StatisticsRepository statisticsRepository,
                            CommissionService commissionService) {
        this.statisticsRepository = statisticsRepository;
        this.commissionService = commissionService;
    }

    public AgentStatisticsDTO agentStatistics(Integer requestedYear) {
        List<Integer> availableYears = statisticsRepository.findAvailableYears(PAID_STATUS);
        if (availableYears.isEmpty()) {
            int currentYear = Year.now().getValue();
            return new AgentStatisticsDTO(currentYear, List.of(currentYear), List.of(), List.of());
        }

        int targetYear = resolveYear(requestedYear, availableYears);
        AgentStatisticsDTO cached = agentCache.get(targetYear);
        if (cached != null && cached.years().equals(availableYears)) {
            return cached;
        }

        AgentStatisticsDTO computed = buildAgentStatistics(targetYear, availableYears);
        agentCache.put(targetYear, computed);
        return computed;
    }

    public TeamStatisticsDTO teamStatistics(Integer requestedYear) {
        List<Integer> availableYears = statisticsRepository.findAvailableYears(PAID_STATUS);
        if (availableYears.isEmpty()) {
            int currentYear = Year.now().getValue();
            return new TeamStatisticsDTO(currentYear, List.of(currentYear), List.of());
        }

        int targetYear = resolveYear(requestedYear, availableYears);
        TeamStatisticsDTO cached = teamCache.get(targetYear);
        if (cached != null && cached.years().equals(availableYears)) {
            return cached;
        }

        TeamStatisticsDTO computed = buildTeamStatistics(targetYear, availableYears);
        teamCache.put(targetYear, computed);
        return computed;
    }

    public void clearCache() {
        agentCache.clear();
        teamCache.clear();
    }

    private int resolveYear(Integer requestedYear, List<Integer> availableYears) {
        if (requestedYear != null && availableYears.contains(requestedYear)) {
            return requestedYear;
        }
        return availableYears.get(availableYears.size() - 1);
    }

    private AgentStatisticsDTO buildAgentStatistics(int targetYear, List<Integer> availableYears) {
        List<MonthlyCommissionDTO> monthlyTotals = statisticsRepository
                .findMonthlyTotals(targetYear, PAID_STATUS).stream()
                .sorted(Comparator.comparing(StatisticsRepository.MonthlyAggregate::getMonth))
                .map(aggregate -> new MonthlyCommissionDTO(
                        aggregate.getYear(),
                        aggregate.getMonth(),
                        commissionService.applyDefaultCommissionRate(aggregate.getTotalAmount())))
                .toList();

        List<AgentCommissionDTO> agentTotals = statisticsRepository
                .findAgentTotals(targetYear, PAID_STATUS).stream()
                .map(aggregate -> new AgentCommissionDTO(
                        aggregate.getAgentId(),
                        aggregate.getAgentName(),
                        aggregate.getTeamName(),
                        commissionService.calculateAgentCommission(
                                aggregate.getTeamId(),
                                aggregate.getAgentId(),
                                aggregate.getTotalAmount())))
                .toList();

        return new AgentStatisticsDTO(targetYear, List.copyOf(availableYears), monthlyTotals, agentTotals);
    }

    private TeamStatisticsDTO buildTeamStatistics(int targetYear, List<Integer> availableYears) {
        List<TeamCommissionDTO> teamTotals = statisticsRepository
                .findTeamTotals(targetYear, PAID_STATUS).stream()
                .map(aggregate -> new TeamCommissionDTO(
                        aggregate.getTeamId(),
                        aggregate.getTeamName(),
                        commissionService.calculateTeamCommission(aggregate.getTeamId(), aggregate.getTotalAmount())))
                .toList();

        return new TeamStatisticsDTO(targetYear, List.copyOf(availableYears), teamTotals);
    }

}
