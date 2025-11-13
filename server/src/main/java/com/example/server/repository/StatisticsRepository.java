package com.example.server.repository;

import com.example.server.domain.Invoice;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StatisticsRepository extends org.springframework.data.repository.Repository<Invoice, Long> {

    @Query("""
            SELECT DISTINCT YEAR(i.payment_date) AS year
            FROM invoices i
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL
            ORDER BY year
            """)
    List<Integer> findAvailableYears(@Param("paidStatus") String paidStatus);

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

    @Query("""
            SELECT a.id AS agentId,
                   u.display_name AS agentName,
                   t.name AS teamName,
                   SUM(i.amount) AS totalAmount
            FROM invoices i
                     JOIN contracts c ON i.contract_id = c.id
                     JOIN agents a ON c.agent_id = a.id
                     JOIN users u ON a.user_id = u.id
                     JOIN teams t ON u.team_id = t.id
            WHERE i.status = :paidStatus AND i.payment_date IS NOT NULL AND YEAR(i.payment_date) = :year
            GROUP BY a.id, u.display_name, t.name
            ORDER BY totalAmount DESC
            """)
    List<AgentAggregate> findAgentTotals(@Param("year") int year,
                                         @Param("paidStatus") String paidStatus);

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

    interface MonthlyAggregate {
        Integer getYear();

        Integer getMonth();

        BigDecimal getTotalAmount();
    }

    interface AgentAggregate {
        Long getAgentId();

        String getAgentName();

        String getTeamName();

        BigDecimal getTotalAmount();
    }

    interface TeamAggregate {
        Long getTeamId();

        String getTeamName();

        BigDecimal getTotalAmount();
    }
}
