package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import java.math.BigDecimal; // Commento automatico: import java.math.BigDecimal;
import java.util.Objects; // Commento automatico: import java.util.Objects;
// Spazio commentato per leggibilità
/** // Commento automatico: /**
 * Percentuale assegnata a un agente per la ripartizione della provvigione di team. // Commento automatico: * Percentuale assegnata a un agente per la ripartizione della provvigione di team.
 */ // Commento automatico: */
public record AgentCommissionShare(Long agentId, BigDecimal percentage, int ranking) { // Commento automatico: public record AgentCommissionShare(Long agentId, BigDecimal percentage, int ranking) {
// Spazio commentato per leggibilità
    public AgentCommissionShare { // Commento automatico: public AgentCommissionShare {
        Objects.requireNonNull(agentId, "agentId must not be null"); // Commento automatico: Objects.requireNonNull(agentId, "agentId must not be null");
        Objects.requireNonNull(percentage, "percentage must not be null"); // Commento automatico: Objects.requireNonNull(percentage, "percentage must not be null");
    } // Commento automatico: }
} // Commento automatico: }
