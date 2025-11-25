package com.example.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJdbcTest
@ActiveProfiles("test")
@Import({StatisticsRepository.class, StatisticsRepositoryTest.StatisticsRepositoryConfig.class})
@Sql(scripts = "/data.sql")
class StatisticsRepositoryTest {

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Test
    void findAvailableYearsShouldReturnDistinctPaidYearsOrdered() {
        List<Integer> years = statisticsRepository.findAvailableYears("PAID");

        assertThat(years).containsExactly(2022, 2024);
    }

    @Test
    void findMonthlyTotalsShouldGroupResultsByMonthForTargetYear() {
        List<StatisticsRepository.MonthlyAggregate> totals = statisticsRepository.findMonthlyTotals(2024, "PAID");

        assertThat(totals)
                .hasSize(3)
                .extracting(StatisticsRepository.MonthlyAggregate::paymentYear,
                        StatisticsRepository.MonthlyAggregate::paymentMonth,
                        StatisticsRepository.MonthlyAggregate::totalAmount)
                .containsExactly(
                        tuple(2024, 1, new BigDecimal("100.00")),
                        tuple(2024, 2, new BigDecimal("250.00")),
                        tuple(2024, 3, new BigDecimal("300.00"))
                );
    }

    @Test
    void findAgentTotalsShouldReturnAggregatedTotalsOrderedByAmount() {
        List<StatisticsRepository.AgentAggregate> totals = statisticsRepository.findAgentTotals(2024, "PAID");

        assertThat(totals)
                .extracting(StatisticsRepository.AgentAggregate::agentId,
                        StatisticsRepository.AgentAggregate::agentName,
                        StatisticsRepository.AgentAggregate::teamId,
                        StatisticsRepository.AgentAggregate::teamName,
                        StatisticsRepository.AgentAggregate::totalAmount)
                .containsExactly(
                        tuple(1L, "Alice Agent", 1L, "Sales", new BigDecimal("350.00")),
                        tuple(2L, "Bob Agent", 2L, "Support", new BigDecimal("300.00"))
                );
    }

    @Test
    void findTeamTotalsShouldAggregateInvoicesByTeam() {
        List<StatisticsRepository.TeamAggregate> totals = statisticsRepository.findTeamTotals(2024, "PAID");

        assertThat(totals)
                .extracting(StatisticsRepository.TeamAggregate::teamId,
                        StatisticsRepository.TeamAggregate::teamName,
                        StatisticsRepository.TeamAggregate::totalAmount)
                .containsExactly(
                        tuple(1L, "Sales", new BigDecimal("350.00")),
                        tuple(2L, "Support", new BigDecimal("300.00"))
                );
    }

    @TestConfiguration
    static class StatisticsRepositoryConfig {
        @Bean
        StatisticsRepository statisticsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new StatisticsRepository(jdbcTemplate);
        }
    }
}
