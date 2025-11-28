package com.example.server.repository;

import com.example.server.domain.Agent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Sql(scripts = "/data.sql")
class AgentRepositoryTest {

    @Autowired
    private AgentRepository agentRepository;

    @Test
    @DisplayName("findAllByOrderByAgentCodeAsc returns sorted agents")
    void findAllSorted() {
        List<Agent> agents = agentRepository.findAllByOrderByAgentCodeAsc();

        assertThat(agents)
                .extracting(Agent::getAgentCode)
                .containsExactly("A-001", "A-002");
    }

    @Test
    @DisplayName("findByUserId returns present and missing results")
    void findByUserId() {
        Optional<Agent> existing = agentRepository.findByUserId(1L);
        Optional<Agent> missing = agentRepository.findByUserId(999L);

        assertThat(existing).isPresent();
        assertThat(existing.get().getAgentCode()).isEqualTo("A-001");
        assertThat(missing).isEmpty();
    }
}
