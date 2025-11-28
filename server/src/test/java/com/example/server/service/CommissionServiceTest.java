package com.example.server.service;

import com.example.server.domain.Agent;
import com.example.server.domain.Contract;
import com.example.server.domain.User;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.CommissionRepository;
import com.example.server.repository.ContractRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommissionServiceTest {

    @Mock
    private CommissionRepository commissionRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private UserRepository userRepository;

    private CommissionService commissionService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-06-01T10:15:30Z"), ZoneOffset.UTC);
        commissionService = new CommissionService(commissionRepository, contractRepository, agentRepository, userRepository, fixedClock);
    }

    @Test
    void applyDefaultCommissionRateShouldHandleNullAndMultiplyAmount() {
        assertThat(commissionService.applyDefaultCommissionRate(null)).isEqualTo(new BigDecimal("0.00"));

        BigDecimal result = commissionService.applyDefaultCommissionRate(new BigDecimal("200"));

        assertThat(result).isEqualTo(new BigDecimal("20.00"));
    }

    @Test
    void calculateTeamCommissionUsesNormalizedRate() {
        long teamId = 5L;
        when(userRepository.findByTeamId(teamId)).thenReturn(List.of(
                new User(1L, null, null, null, null, null, teamId, true, null),
                new User(2L, null, null, null, null, null, teamId, true, null)
        ));
        when(agentRepository.findByUserId(1L)).thenReturn(Optional.of(new Agent(101L, 1L, "A1", "Senior")));
        when(agentRepository.findByUserId(2L)).thenReturn(Optional.of(new Agent(102L, 2L, "A2", "Junior")));

        BigDecimal commission = commissionService.calculateTeamCommission(teamId, new BigDecimal("50"));

        assertThat(commission).isEqualTo(new BigDecimal("5.00"));
    }

    @Test
    void calculateAgentCommissionDistributesWithPercentageStrategy() {
        long teamId = 8L;
        when(userRepository.findByTeamId(teamId)).thenReturn(List.of(
                new User(11L, null, null, null, null, null, teamId, true, null),
                new User(12L, null, null, null, null, null, teamId, true, null)
        ));
        when(agentRepository.findByUserId(11L)).thenReturn(Optional.of(new Agent(201L, 11L, "A201", "Senior")));
        when(agentRepository.findByUserId(12L)).thenReturn(Optional.of(new Agent(202L, 12L, "A202", "Junior")));

        BigDecimal seniorCommission = commissionService.calculateAgentCommission(teamId, 201L, new BigDecimal("100"));
        BigDecimal juniorCommission = commissionService.calculateAgentCommission(teamId, 202L, new BigDecimal("100"));

        assertThat(seniorCommission).isEqualTo(new BigDecimal("6.00"));
        assertThat(juniorCommission).isEqualTo(new BigDecimal("4.00"));
    }

    @Test
    void calculateAgentCommissionStopsAtTeamRateForBarrierStrategy() {
        long teamId = 15L;
        when(userRepository.findByTeamId(teamId)).thenReturn(List.of(
                new User(21L, null, null, null, null, null, teamId, true, null),
                new User(22L, null, null, null, null, null, teamId, true, null),
                new User(23L, null, null, null, null, null, teamId, true, null),
                new User(24L, null, null, null, null, null, teamId, true, null),
                new User(25L, null, null, null, null, null, teamId, true, null)
        ));
        when(agentRepository.findByUserId(21L)).thenReturn(Optional.of(new Agent(301L, 21L, "A301", "Senior")));
        when(agentRepository.findByUserId(22L)).thenReturn(Optional.of(new Agent(302L, 22L, "A302", "Senior")));
        when(agentRepository.findByUserId(23L)).thenReturn(Optional.of(new Agent(303L, 23L, "A303", "Senior")));
        when(agentRepository.findByUserId(24L)).thenReturn(Optional.of(new Agent(304L, 24L, "A304", "Senior")));
        when(agentRepository.findByUserId(25L)).thenReturn(Optional.of(new Agent(305L, 25L, "A305", "Senior")));

        BigDecimal first = commissionService.calculateAgentCommission(teamId, 301L, new BigDecimal("100"));
        BigDecimal second = commissionService.calculateAgentCommission(teamId, 302L, new BigDecimal("100"));
        BigDecimal third = commissionService.calculateAgentCommission(teamId, 303L, new BigDecimal("100"));
        BigDecimal fourth = commissionService.calculateAgentCommission(teamId, 304L, new BigDecimal("100"));
        BigDecimal exhausted = commissionService.calculateAgentCommission(teamId, 305L, new BigDecimal("100"));

        assertThat(List.of(first, second, third, fourth, exhausted))
                .containsExactly(
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("0.00")
                );
    }

    @Test
    void contractAgentCommissionFallsBackWhenContractMissing() {
        assertThat(commissionService.contractAgentCommission(99L, new BigDecimal("123")))
                .isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void contractAgentCommissionUsesSingleAgentRuleWhenAgentUnknown() {
        Contract contract = Contract.create(500L, "cust", "desc", null, null, null, null);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(agentRepository.findById(anyLong())).thenReturn(Optional.empty());

        BigDecimal commission = commissionService.contractAgentCommission(1L, new BigDecimal("200"));

        assertThat(commission).isEqualTo(new BigDecimal("20.00"));
    }
}
