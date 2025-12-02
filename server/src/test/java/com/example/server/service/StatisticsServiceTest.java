package com.example.server.service;

import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.MonthlyCommissionDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.server.repository.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private CommissionService commissionService;

    private StatisticsService service;

    @BeforeEach
    void setUp() {
        service = new StatisticsService(statisticsRepository, commissionService);
    }

    @Test
    void shouldReturnFallbackWhenNoPaidInvoices() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of());

        AgentStatisticsDTO agentStats = service.agentStatistics(null, null, null, null);
        TeamStatisticsDTO teamStats = service.teamStatistics(2020, null, null, null);

        int currentYear = Year.now().getValue();
        assertThat(agentStats.year()).isEqualTo(currentYear);
        assertThat(agentStats.years()).containsExactly(currentYear);
        assertThat(agentStats.monthlyTotals()).isEmpty();
        assertThat(teamStats.year()).isEqualTo(currentYear);
        assertThat(teamStats.teamTotals()).isEmpty();
    }

    @Test
    void shouldCacheAgentStatisticsWhenYearsUnchanged() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of(2022, 2023));
        when(statisticsRepository.findMonthlyTotals(any(), any(), eq("PAID"), isNull())).thenReturn(List.of(
                new StatisticsRepository.MonthlyAggregate(2023, 5, new BigDecimal("10")),
                new StatisticsRepository.MonthlyAggregate(2023, 4, new BigDecimal("5"))
        ));
        when(statisticsRepository.findAgentTotals(any(), any(), eq("PAID"), isNull())).thenReturn(List.of(
                new StatisticsRepository.AgentAggregate(1L, "Mario", 7L, "Team", new BigDecimal("100"))
        ));
        when(commissionService.applyDefaultCommissionRate(any())).thenReturn(BigDecimal.ONE);
        when(commissionService.calculateAgentCommission(eq(7L), eq(1L), any())).thenReturn(BigDecimal.TEN);

        AgentStatisticsDTO first = service.agentStatistics(null, null, null, null);
        AgentStatisticsDTO second = service.agentStatistics(null, null, null, null);

        assertThat(first.monthlyTotals())
                .extracting(MonthlyCommissionDTO::month)
                .containsExactly(4, 5);
        assertThat(first.agentTotals().getFirst().commission()).isEqualTo(BigDecimal.TEN);
        assertThat(second).isSameAs(first);
        verify(statisticsRepository, times(1)).findMonthlyTotals(any(), any(), eq("PAID"), isNull());
        verify(statisticsRepository, times(1)).findAgentTotals(any(), any(), eq("PAID"), isNull());
    }

    @Test
    void shouldBuildTeamStatisticsForRequestedYearWhenAvailable() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of(2021, 2022));
        when(statisticsRepository.findTeamTotals(any(), any(), eq("PAID"), isNull())).thenReturn(List.of(
                new StatisticsRepository.TeamAggregate(3L, "North", new BigDecimal("50"))
        ));
        when(commissionService.calculateTeamCommission(3L, new BigDecimal("50"))).thenReturn(new BigDecimal("5"));

        TeamStatisticsDTO stats = service.teamStatistics(2021, null, null, null);

        assertThat(stats.year()).isEqualTo(2021);
        assertThat(stats.teamTotals()).hasSize(1);
        assertThat(stats.teamTotals().getFirst().commission()).isEqualTo(new BigDecimal("5"));
    }

    @Test
    void shouldReturnMonthlyTotalsSortedByYearAndMonth() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of(2022, 2023));
        when(statisticsRepository.findMonthlyTotals(any(), any(), eq("PAID"), isNull())).thenReturn(List.of(
                new StatisticsRepository.MonthlyAggregate(2023, 1, new BigDecimal("20")),
                new StatisticsRepository.MonthlyAggregate(2022, 12, new BigDecimal("10")),
                new StatisticsRepository.MonthlyAggregate(2022, 6, new BigDecimal("5"))
        ));
        when(statisticsRepository.findAgentTotals(any(), any(), eq("PAID"), isNull())).thenReturn(List.of());
        when(commissionService.applyDefaultCommissionRate(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AgentStatisticsDTO stats = service.agentStatistics(null,
                LocalDate.of(2022, 6, 1),
                LocalDate.of(2023, 2, 28),
                null);

        assertThat(stats.year()).isEqualTo(2022);
        assertThat(stats.monthlyTotals())
                .extracting(MonthlyCommissionDTO::year, MonthlyCommissionDTO::month)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(2022, 6),
                        org.assertj.core.groups.Tuple.tuple(2022, 12),
                        org.assertj.core.groups.Tuple.tuple(2023, 1)
                );
    }

}
