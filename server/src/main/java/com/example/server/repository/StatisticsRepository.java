// Declare the package that contains repository interfaces.
package com.example.server.repository;

// Import the Invoice entity to type the repository.
import com.example.server.domain.Invoice;
// Import Query annotation for custom SQL queries.
import org.springframework.data.jdbc.repository.query.Query;
// Import Param to bind method parameters to query parameters.
import org.springframework.data.repository.query.Param;
// Import Repository annotation to register the interface with Spring.
import org.springframework.stereotype.Repository;

// Import BigDecimal for precise monetary amounts.
import java.math.BigDecimal;
// Import List to handle collections of query results.
import java.util.List;

// Mark this interface as a Spring Data repository bean.
@Repository
// Declare a repository focused on statistics over Invoice entities.
public interface StatisticsRepository extends org.springframework.data.repository.Repository<Invoice, Long> {

    // Query for all distinct payment years of paid invoices ordered ascending.
    @Query("""
            SELECT DISTINCT YEAR(i.payment_date) AS year
            FROM invoices i
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL
            ORDER BY year
            """)
    List<Integer> findAvailableYears(@Param("paidStatus") String paidStatus);

    // Query for monthly totals for a given year of paid invoices.
    @Query("""
            SELECT YEAR(i.payment_date) AS year,
                   MONTH(i.payment_date) AS month,
                   SUM(i.amount) AS totalAmount
            FROM invoices i
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
            GROUP BY YEAR(i.payment_date), MONTH(i.payment_date)
            ORDER BY MONTH(i.payment_date)
            """)
    List<MonthlyAggregate> findMonthlyTotals(@Param("year") int year,
                                             @Param("paidStatus") String paidStatus);

    // Query for total amounts grouped by agent and team for a given year.
    @Query("""
            SELECT a.id AS agentId,
                   u.display_name AS agentName,
                   t.id AS teamId,
                   t.name AS teamName,
                   SUM(i.amount) AS totalAmount
            FROM invoices i
                     JOIN contracts c ON i.contract_id = c.id
                     JOIN agents a ON c.agent_id = a.id
                     JOIN users u ON a.user_id = u.id
                     JOIN teams t ON u.team_id = t.id
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
            GROUP BY a.id, u.display_name, t.id, t.name
            ORDER BY totalAmount DESC
            """)
    List<AgentAggregate> findAgentTotals(@Param("year") int year,
                                         @Param("paidStatus") String paidStatus);

    // Query for total amounts grouped by team for a given year.
    @Query("""
            SELECT t.id AS teamId,
                   t.name AS teamName,
                   SUM(i.amount) AS totalAmount
            FROM invoices i
                     JOIN contracts c ON i.contract_id = c.id
                     JOIN agents a ON c.agent_id = a.id
                     JOIN users u ON a.user_id = u.id
                     JOIN teams t ON u.team_id = t.id
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
            GROUP BY t.id, t.name
            ORDER BY totalAmount DESC
            """)
    List<TeamAggregate> findTeamTotals(@Param("year") int year,
                                       @Param("paidStatus") String paidStatus);

    // Projection for monthly statistics results.
    interface MonthlyAggregate {
        // Year of the aggregated invoices.
        Integer getYear();

        // Month of the aggregated invoices.
        Integer getMonth();

        // Sum of invoice amounts for the month.
        BigDecimal getTotalAmount();
    }

    // Projection for per-agent statistics results.
    interface AgentAggregate {
        // Unique identifier of the agent.
        Long getAgentId();

        // Display name of the agent.
        String getAgentName();

        // Unique identifier of the associated team.
        Long getTeamId();

        // Name of the associated team.
        String getTeamName();

        // Sum of invoice amounts attributed to the agent.
        BigDecimal getTotalAmount();
    }

    // Projection for per-team statistics results.
    interface TeamAggregate {
        // Unique identifier of the team.
        Long getTeamId();

        // Name of the team.
        String getTeamName();

        // Sum of invoice amounts attributed to the team.
        BigDecimal getTotalAmount();
    }
}
