package com.example.server.service; // Definisce il package in cui si trova il record

import java.math.BigDecimal; // Importa la classe per gestire valori decimali di precisione
import java.util.Objects; // Importa utilità per i controlli di nullità

/**
 * Percentuale assegnata a un agente per la ripartizione della provvigione di team.
 */
public record AgentCommissionShare(Long agentId, BigDecimal percentage, int ranking) { // Record immutabile che rappresenta la quota provvigionale di un agente

    public AgentCommissionShare { // Costruttore compatto per validare i parametri del record
        Objects.requireNonNull(agentId, "agentId must not be null"); // Verifica che l'identificativo dell'agente non sia nullo
        Objects.requireNonNull(percentage, "percentage must not be null"); // Verifica che la percentuale assegnata non sia nulla
    }
}
