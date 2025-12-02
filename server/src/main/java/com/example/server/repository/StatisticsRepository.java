package com.example.server.repository;                                 // Package che contiene i repository personalizzati del backend.

import org.springframework.jdbc.core.RowMapper;                        // Interfaccia per mappare le righe del ResultSet in oggetti Java.
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource; // Classe per fornire parametri nominati nelle query SQL.
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // Template JDBC che supporta parametri nominati.
import org.springframework.stereotype.Repository;                      // Indica che la classe è un componente di accesso ai dati.

import java.math.BigDecimal;                                           // Rappresenta valori numerici con precisione arbitraria.
import java.time.LocalDate;                                            // Gestisce le date filtro.
import java.util.List;                                                 // Supporta la gestione di liste di risultati.

@Repository                                                             // Rende la classe un bean Spring di tipo repository.
public class StatisticsRepository {                                    // Repository custom per aggregazioni statistiche.

    private final NamedParameterJdbcTemplate jdbcTemplate;             // Template centralizzato per eseguire query SQL con parametri nominati.

    public StatisticsRepository(NamedParameterJdbcTemplate jdbcTemplate) { // Costruttore con iniezione del template JDBC.
        this.jdbcTemplate = jdbcTemplate;                              // Assegna il template al campo interno.
    }

    public List<Integer> findAvailableYears(String paidStatus) {       // Restituisce gli anni disponibili in base allo stato di pagamento.
        // Query SQL multi-linea per estrarre anni distinti dai pagamenti.
        String sql = """
                SELECT DISTINCT EXTRACT(YEAR FROM i."payment_date") AS payment_year
                FROM "invoices" i
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL
                ORDER BY payment_year
                """;
        MapSqlParameterSource params = new MapSqlParameterSource("paidStatus", paidStatus); // Parametro nominato per lo stato.
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getInt("payment_year"));  // Esegue la query e mappa l'anno come intero.
    }

    public List<MonthlyAggregate> findMonthlyTotals(LocalDate fromDate, LocalDate toDate, String paidStatus, Long roleId) { // Aggrega i totali mensili in un intervallo.
        StringBuilder sql = new StringBuilder("""
                SELECT EXTRACT(YEAR FROM i."payment_date") AS payment_year,
                       EXTRACT(MONTH FROM i."payment_date") AS payment_month,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                         JOIN "contracts" c ON i."contract_id" = c."id"
                         JOIN "agents" a ON c."agent_id" = a."id"
                         JOIN "users" u ON a."user_id" = u."id"
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL
                """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("paidStatus", paidStatus)
                .addValue("fromDate", fromDate)
                .addValue("toDate", toDate)
                .addValue("roleId", roleId);

        if (fromDate != null) {
            sql.append(" AND i.\"payment_date\" >= :fromDate");
        }
        if (toDate != null) {
            sql.append(" AND i.\"payment_date\" <= :toDate");
        }
        if (roleId != null) {
            sql.append(" AND u.\"role_id\" = :roleId");
        }

        sql.append(" GROUP BY EXTRACT(YEAR FROM i.\"payment_date\"), EXTRACT(MONTH FROM i.\"payment_date\")")
                .append(" ORDER BY payment_year, payment_month");

        RowMapper<MonthlyAggregate> mapper = (rs, rowNum) -> new MonthlyAggregate(
                rs.getInt("payment_year"),
                rs.getInt("payment_month"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql.toString(), params, mapper);
    }

    public List<AgentAggregate> findAgentTotals(LocalDate fromDate, LocalDate toDate, String paidStatus, Long roleId) { // Aggrega i totali per agente e team.
        StringBuilder sql = new StringBuilder("""
                SELECT a."id" AS agent_id,
                       u."display_name" AS agent_name,
                       t."id" AS team_id,
                       t."name" AS team_name,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                         JOIN "contracts" c ON i."contract_id" = c."id"
                         JOIN "agents" a ON c."agent_id" = a."id"
                         JOIN "users" u ON a."user_id" = u."id"
                         JOIN "teams" t ON u."team_id" = t."id"
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL
                """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("paidStatus", paidStatus)
                .addValue("fromDate", fromDate)
                .addValue("toDate", toDate)
                .addValue("roleId", roleId);

        if (fromDate != null) {
            sql.append(" AND i.\"payment_date\" >= :fromDate");
        }
        if (toDate != null) {
            sql.append(" AND i.\"payment_date\" <= :toDate");
        }
        if (roleId != null) {
            sql.append(" AND u.\"role_id\" = :roleId");
        }

        sql.append(" GROUP BY a.\"id\", u.\"display_name\", t.\"id\", t.\"name\"")
                .append(" ORDER BY total_amount DESC");

        RowMapper<AgentAggregate> mapper = (rs, rowNum) -> new AgentAggregate(
                rs.getLong("agent_id"),
                rs.getString("agent_name"),
                rs.getLong("team_id"),
                rs.getString("team_name"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql.toString(), params, mapper);
    }

    public List<TeamAggregate> findTeamTotals(LocalDate fromDate, LocalDate toDate, String paidStatus, Long roleId) { // Aggrega i totali dei team.
        StringBuilder sql = new StringBuilder("""
                SELECT t."id" AS team_id,
                       t."name" AS team_name,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                         JOIN "contracts" c ON i."contract_id" = c."id"
                         JOIN "agents" a ON c."agent_id" = a."id"
                         JOIN "users" u ON a."user_id" = u."id"
                         JOIN "teams" t ON u."team_id" = t."id"
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL
                """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("paidStatus", paidStatus)
                .addValue("fromDate", fromDate)
                .addValue("toDate", toDate)
                .addValue("roleId", roleId);

        if (fromDate != null) {
            sql.append(" AND i.\"payment_date\" >= :fromDate");
        }
        if (toDate != null) {
            sql.append(" AND i.\"payment_date\" <= :toDate");
        }
        if (roleId != null) {
            sql.append(" AND u.\"role_id\" = :roleId");
        }

        sql.append(" GROUP BY t.\"id\", t.\"name\"")
                .append(" ORDER BY total_amount DESC");

        RowMapper<TeamAggregate> mapper = (rs, rowNum) -> new TeamAggregate(
                rs.getLong("team_id"),
                rs.getString("team_name"),
                rs.getBigDecimal("total_amount")
        );
        return jdbcTemplate.query(sql.toString(), params, mapper);
    }

    // Record che rappresenta l'aggregazione mensile.
    public record MonthlyAggregate(Integer paymentYear, Integer paymentMonth, BigDecimal totalAmount) {
        public Integer getYear() {                                    // Getter standardizzato dell'anno.
            return paymentYear;
        }
        public Integer getMonth() {                                   // Getter standardizzato del mese.
            return paymentMonth;
        }
        public BigDecimal getTotalAmount() {                          // Getter del totale.
            return totalAmount;
        }
    }

    // Record che rappresenta l'aggregazione per agente.
    public record AgentAggregate(Long agentId, String agentName, Long teamId, String teamName, BigDecimal totalAmount) {
        public Long getAgentId() {                                    // Getter ID agente.
            return agentId;
        }
        public String getAgentName() {                                // Getter nome agente.
            return agentName;
        }
        public Long getTeamId() {                                     // Getter ID team.
            return teamId;
        }
        public String getTeamName() {                                 // Getter nome team.
            return teamName;
        }
        public BigDecimal getTotalAmount() {                          // Getter totale importi.
            return totalAmount;
        }
    }

    // Record che rappresenta l'aggregazione per team.
    public record TeamAggregate(Long teamId, String teamName, BigDecimal totalAmount) {
        public Long getTeamId() {                                     // Getter ID team.
            return teamId;
        }
        public String getTeamName() {                                 // Getter nome team.
            return teamName;
        }
        public BigDecimal getTotalAmount() {                          // Getter totale importi.
            return totalAmount;
        }
    }
} // Fine classe StatisticsRepository.
