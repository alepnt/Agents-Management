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
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        AgentStatisticsDTO agentStats = service.agentStatistics(null);
        TeamStatisticsDTO teamStats = service.teamStatistics(2020);

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
        when(statisticsRepository.findMonthlyTotals(2023, "PAID")).thenReturn(List.of(
                new MonthlyAggregateStub(2023, 5, new BigDecimal("10")),
                new MonthlyAggregateStub(2023, 4, new BigDecimal("5"))
        ));
        when(statisticsRepository.findAgentTotals(2023, "PAID")).thenReturn(List.of(
                new AgentAggregateStub(1L, "Mario", 7L, "Team", new BigDecimal("100"))
        ));
        when(commissionService.applyDefaultCommissionRate(any())).thenReturn(BigDecimal.ONE);
        when(commissionService.calculateAgentCommission(eq(7L), eq(1L), any())).thenReturn(BigDecimal.TEN);

        AgentStatisticsDTO first = service.agentStatistics(null);
        AgentStatisticsDTO second = service.agentStatistics(null);

        assertThat(first.monthlyTotals())
                .extracting(MonthlyCommissionDTO::month)
                .containsExactly(4, 5);
        assertThat(first.agentTotals().getFirst().commission()).isEqualTo(BigDecimal.TEN);
        assertThat(second).isSameAs(first);
        verify(statisticsRepository, times(1)).findMonthlyTotals(2023, "PAID");
        verify(statisticsRepository, times(1)).findAgentTotals(2023, "PAID");
    }

    @Test
    void shouldBuildTeamStatisticsForRequestedYearWhenAvailable() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of(2021, 2022));
        when(statisticsRepository.findTeamTotals(2021, "PAID")).thenReturn(List.of(
                new TeamAggregateStub(3L, "North", new BigDecimal("50"))
        ));
        when(commissionService.calculateTeamCommission(3L, new BigDecimal("50"))).thenReturn(new BigDecimal("5"));

        TeamStatisticsDTO stats = service.teamStatistics(2021);

        assertThat(stats.year()).isEqualTo(2021);
        assertThat(stats.teamTotals()).hasSize(1);
        assertThat(stats.teamTotals().getFirst().commission()).isEqualTo(new BigDecimal("5"));
    }

    private record MonthlyAggregateStub(Integer year, Integer month, BigDecimal totalAmount)
            implements StatisticsRepository.MonthlyAggregate {
        @Override
        public Integer getYear() {
            return year;
        }

        @Override
        public Integer getMonth() {
            return month;
        }

        @Override
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }

    private record AgentAggregateStub(Long agentId, String agentName, Long teamId, String teamName, BigDecimal totalAmount)
            implements StatisticsRepository.AgentAggregate {
        @Override
        public Long getAgentId() {
            return agentId;
        }

        @Override
        public String getAgentName() {
            return agentName;
        }

        @Override
        public Long getTeamId() {
            return teamId;
        }

        @Override
        public String getTeamName() {
            return teamName;
        }

        @Override
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }

    private record TeamAggregateStub(Long teamId, String teamName, BigDecimal totalAmount)
            implements StatisticsRepository.TeamAggregate {
        @Override
        public Long getTeamId() {
            return teamId;
        }

        @Override
        public String getTeamName() {
            return teamName;
        }

        @Override
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}
