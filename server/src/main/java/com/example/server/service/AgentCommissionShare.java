package com.example.server.service; // Colloca il record nel package dei servizi del server.

import java.math.BigDecimal; // Importa il tipo decimale usato per rappresentare percentuali con precisione.
import java.util.Objects; // Importa le utility per verificare che i parametri non siano nulli.

/**
 * Percentuale assegnata a un agente per la ripartizione della provvigione di team.
 */
public record AgentCommissionShare(Long agentId, BigDecimal percentage, int ranking) { // Record immutabile che descrive la quota di provvigione assegnata a un singolo agente.

    public AgentCommissionShare { // Costruttore compatto che applica le regole di validazione sugli argomenti.
        Objects.requireNonNull(agentId, "agentId must not be null"); // Impone che l'identificativo dell'agente sia sempre valorizzato.
        Objects.requireNonNull(percentage, "percentage must not be null"); // Impone che la percentuale assegnata non sia mai assente.
    }
}
