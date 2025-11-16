package com.example.server.service;

import com.example.server.domain.Agent;
import com.example.server.domain.Commission;
import com.example.server.domain.Contract;
import com.example.server.domain.User;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.CommissionRepository;
import com.example.server.repository.ContractRepository;
import com.example.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class CommissionService {

    private static final BigDecimal MIN_TEAM_RATE = new BigDecimal("0.10");
    private static final BigDecimal MAX_TEAM_RATE = new BigDecimal("0.12");
    private static final BigDecimal SENIOR_RATE = new BigDecimal("0.03");
    private static final BigDecimal JUNIOR_RATE = new BigDecimal("0.02");
    private static final BigDecimal INTERN_RATE = new BigDecimal("0.015");
    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.01");
    private static final MathContext MATH_CONTEXT = new MathContext(8, RoundingMode.HALF_UP);

    private final CommissionRepository commissionRepository;
    private final ContractRepository contractRepository;
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public CommissionService(CommissionRepository commissionRepository,
                             ContractRepository contractRepository,
                             AgentRepository agentRepository,
                             UserRepository userRepository,
                             Clock clock) {
        this.commissionRepository = commissionRepository;
        this.contractRepository = contractRepository;
        this.agentRepository = agentRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public List<Commission> updateAfterPayment(Long contractId, BigDecimal invoiceAmount, BigDecimal amountPaid) {
        if (contractId == null) {
            return List.of();
        }

        Optional<Contract> contract = contractRepository.findById(contractId);
        if (contract.isEmpty()) {
            return List.of();
        }

        Long contractAgentId = contract.get().getAgentId();
        TeamCommissionRule rule = resolveRuleForAgent(contractAgentId);
        Map<Long, BigDecimal> totalAllocations = allocationForAmount(invoiceAmount, rule);
        Map<Long, BigDecimal> paidAllocations = allocationForAmount(amountPaid, rule);

        List<Commission> updated = new ArrayList<>();
        for (AgentCommissionShare share : orderedShares(rule)) {
            Long agentId = share.agentId();
            Commission base = commissionRepository
                    .findByAgentIdAndContractId(agentId, contractId)
                    .orElseGet(() -> Commission.create(agentId, contractId, BigDecimal.ZERO));

            BigDecimal totalCommission = base.getTotalCommission().add(totalAllocations.getOrDefault(agentId, BigDecimal.ZERO));
            BigDecimal paidCommission = base.getPaidCommission().add(paidAllocations.getOrDefault(agentId, BigDecimal.ZERO));
            BigDecimal pendingCommission = totalCommission.subtract(paidCommission);
            if (pendingCommission.signum() < 0) {
                pendingCommission = BigDecimal.ZERO;
            }

            Commission updatedCommission = base.update(totalCommission, paidCommission, pendingCommission, Instant.now(clock));
            updated.add(commissionRepository.save(updatedCommission));
        }
        return updated;
    }

    public BigDecimal computeCommission(BigDecimal amount) {
        return applyDefaultCommissionRate(amount);
    }

    public BigDecimal contractAgentCommission(Long contractId, BigDecimal amount) {
        if (contractId == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        Optional<Contract> contract = contractRepository.findById(contractId);
        if (contract.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        Long agentId = contract.get().getAgentId();
        TeamCommissionRule rule = resolveRuleForAgent(agentId);
        Map<Long, BigDecimal> allocations = allocationForAmount(amount, rule);
        return allocations.getOrDefault(agentId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAgentCommission(Long teamId, Long agentId, BigDecimal amount) {
        if (agentId == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        TeamCommissionRule rule = resolveRuleForTeam(teamId);
        Map<Long, BigDecimal> allocations = allocationForAmount(amount, rule);
        return allocations.getOrDefault(agentId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTeamCommission(Long teamId, BigDecimal amount) {
        TeamCommissionRule rule = resolveRuleForTeam(teamId);
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal rate = rule.teamCommissionRate();
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal applyDefaultCommissionRate(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.multiply(MIN_TEAM_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<Long, BigDecimal> allocationForAmount(BigDecimal amount, TeamCommissionRule rule) {
        if (amount == null || rule == null || rule.shares().isEmpty()) {
            return Map.of();
        }

        Map<Long, BigDecimal> allocations = new LinkedHashMap<>();
        List<AgentCommissionShare> ordered = orderedShares(rule);
        if (rule.distributionStrategy() == DistributionStrategy.BARRIER) {
            BigDecimal remainingRate = rule.teamCommissionRate();
            for (AgentCommissionShare share : ordered) {
                BigDecimal allocatedRate = share.percentage().min(remainingRate);
                if (allocatedRate.signum() < 0) {
                    allocatedRate = BigDecimal.ZERO;
                }
                allocations.put(share.agentId(), amount.multiply(allocatedRate));
                remainingRate = remainingRate.subtract(allocatedRate);
            }
        } else {
            BigDecimal totalShare = ordered.stream()
                    .map(AgentCommissionShare::percentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalShare.signum() == 0) {
                ordered.forEach(share -> allocations.put(share.agentId(), BigDecimal.ZERO));
                return allocations;
            }
            BigDecimal scaling = rule.teamCommissionRate().divide(totalShare, MATH_CONTEXT);
            for (AgentCommissionShare share : ordered) {
                BigDecimal allocatedRate = share.percentage().multiply(scaling);
                allocations.put(share.agentId(), amount.multiply(allocatedRate));
            }
        }
        return allocations;
    }

    private List<AgentCommissionShare> orderedShares(TeamCommissionRule rule) {
        if (rule == null) {
            return List.of();
        }
        return rule.shares().stream()
                .sorted(Comparator.comparingInt(AgentCommissionShare::ranking).thenComparing(AgentCommissionShare::agentId))
                .toList();
    }

    private TeamCommissionRule resolveRuleForAgent(Long agentId) {
        if (agentId == null) {
            return null;
        }
        Optional<Agent> agent = agentRepository.findById(agentId);
        if (agent.isEmpty()) {
            return TeamCommissionRule.singleAgent(agentId, MIN_TEAM_RATE);
        }
        Optional<User> user = userRepository.findById(agent.get().getUserId());
        if (user.isEmpty() || user.get().getTeamId() == null) {
            return TeamCommissionRule.singleAgent(agentId, MIN_TEAM_RATE);
        }
        TeamCommissionRule teamRule = resolveRuleForTeam(user.get().getTeamId());
        boolean containsAgent = teamRule.shares().stream()
                .anyMatch(share -> share.agentId().equals(agentId));
        if (!containsAgent) {
            List<AgentCommissionShare> enriched = new ArrayList<>(teamRule.shares());
            enriched.add(new AgentCommissionShare(agentId,
                    percentageForRole(agent.get().getTeamRole()),
                    rankingForRole(agent.get().getTeamRole())));
            return new TeamCommissionRule(teamRule.teamId(), teamRule.teamCommissionRate(),
                    teamRule.distributionStrategy(), List.copyOf(enriched));
        }
        return teamRule;
    }

    private TeamCommissionRule resolveRuleForTeam(Long teamId) {
        if (teamId == null) {
            return new TeamCommissionRule(null, MIN_TEAM_RATE, DistributionStrategy.PERCENTAGE, List.of());
        }
        List<User> teamMembers = userRepository.findByTeamId(teamId);
        List<AgentCommissionShare> shares = new ArrayList<>();
        for (User member : teamMembers) {
            agentRepository.findByUserId(member.getId()).ifPresent(agent ->
                    shares.add(new AgentCommissionShare(agent.getId(),
                            percentageForRole(agent.getTeamRole()),
                            rankingForRole(agent.getTeamRole()))));
        }
        BigDecimal totalRequested = shares.stream()
                .map(AgentCommissionShare::percentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal teamRate = normalizeTeamRate(totalRequested);
        DistributionStrategy strategy = totalRequested.compareTo(teamRate) > 0
                ? DistributionStrategy.BARRIER
                : DistributionStrategy.PERCENTAGE;
        return new TeamCommissionRule(teamId, teamRate, strategy, List.copyOf(shares));
    }

    private BigDecimal normalizeTeamRate(BigDecimal desired) {
        if (desired == null) {
            return MIN_TEAM_RATE;
        }
        BigDecimal capped = desired.min(MAX_TEAM_RATE);
        if (capped.compareTo(MIN_TEAM_RATE) < 0) {
            return MIN_TEAM_RATE;
        }
        return capped;
    }

    private BigDecimal percentageForRole(String role) {
        if (role == null) {
            return DEFAULT_RATE;
        }
        String normalized = role.toLowerCase(Locale.ITALY);
        if (normalized.contains("senior")) {
            return SENIOR_RATE;
        }
        if (normalized.contains("junior")) {
            return JUNIOR_RATE;
        }
        if (normalized.contains("stag")) {
            return INTERN_RATE;
        }
        return DEFAULT_RATE;
    }

    private int rankingForRole(String role) {
        if (role == null) {
            return 3;
        }
        String normalized = role.toLowerCase(Locale.ITALY);
        if (normalized.contains("senior")) {
            return 0;
        }
        if (normalized.contains("junior")) {
            return 1;
        }
        if (normalized.contains("stag")) {
            return 2;
        }
        return 3;
    }
}
