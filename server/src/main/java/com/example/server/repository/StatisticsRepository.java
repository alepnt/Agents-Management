package com.example.server.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class StatisticsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StatisticsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Integer> findAvailableYears(String paidStatus) {
        String sql = """
                SELECT DISTINCT YEAR(i.payment_date) AS payment_year
                FROM invoices i
                WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL
                ORDER BY payment_year
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("paidStatus", paidStatus);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getInt("payment_year"));
    }

    public List<MonthlyAggregate> findMonthlyTotals(int year, String paidStatus) {
        String sql = """
                SELECT YEAR(i.payment_date) AS payment_year,
                       MONTH(i.payment_date) AS payment_month,
                       SUM(i.amount) AS total_amount
                FROM invoices i
                WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
                GROUP BY YEAR(i.payment_date), MONTH(i.payment_date)
                ORDER BY payment_month
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("paidStatus", paidStatus)
                .addValue("year", year);
        RowMapper<MonthlyAggregate> mapper = (rs, rowNum) -> new MonthlyAggregate(
                rs.getInt("payment_year"),
                rs.getInt("payment_month"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql, params, mapper);
    }

    public List<AgentAggregate> findAgentTotals(int year, String paidStatus) {
        String sql = """
                SELECT a.id AS agent_id,
                       u.display_name AS agent_name,
                       t.id AS team_id,
                       t.name AS team_name,
                       SUM(i.amount) AS total_amount
                FROM invoices i
                         JOIN contracts c ON i.contract_id = c.id
                         JOIN agents a ON c.agent_id = a.id
                         JOIN users u ON a.user_id = u.id
                         JOIN teams t ON u.team_id = t.id
                WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
                GROUP BY a.id, u.display_name, t.id, t.name
                ORDER BY total_amount DESC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("paidStatus", paidStatus);
        RowMapper<AgentAggregate> mapper = (rs, rowNum) -> new AgentAggregate(
                rs.getLong("agent_id"),
                rs.getString("agent_name"),
                rs.getLong("team_id"),
                rs.getString("team_name"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql, params, mapper);
    }

    public List<TeamAggregate> findTeamTotals(int year, String paidStatus) {
        String sql = """
                SELECT t.id AS team_id,
                       t.name AS team_name,
                       SUM(i.amount) AS total_amount
                FROM invoices i
                         JOIN contracts c ON i.contract_id = c.id
                         JOIN agents a ON c.agent_id = a.id
                         JOIN users u ON a.user_id = u.id
                         JOIN teams t ON u.team_id = t.id
                WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
                GROUP BY t.id, t.name
                ORDER BY total_amount DESC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("paidStatus", paidStatus);
        RowMapper<TeamAggregate> mapper = (rs, rowNum) -> new TeamAggregate(
                rs.getLong("team_id"),
                rs.getString("team_name"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql, params, mapper);
    }

    public record MonthlyAggregate(Integer paymentYear, Integer paymentMonth, BigDecimal totalAmount) { }

    public record AgentAggregate(Long agentId, String agentName, Long teamId, String teamName, BigDecimal totalAmount) { }

    public record TeamAggregate(Long teamId, String teamName, BigDecimal totalAmount) { }
}
