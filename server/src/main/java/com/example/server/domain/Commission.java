package com.example.server.domain; // Definisce il package della classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.math.BigDecimal; // Importa il tipo per valori monetari
import java.time.Instant; // Importa il tipo per timestamp
import java.util.Objects; // Importa utilità per confronti e hash

@Table("commissions") // Associa la classe alla tabella "commissions"
public class Commission { // Definisce l'entità Commission

    @Id // Identifica il campo come chiave primaria
    @Column("id") // Colonna primaria in minuscolo
    private Long id; // Identificativo univoco della commissione

    @Column("agent_id") // Mappa il campo alla colonna agent_id
    private Long agentId; // Riferimento all'agente coinvolto

    @Column("contract_id") // Mappa il campo alla colonna contract_id
    private Long contractId; // Riferimento al contratto associato

    @Column("total_commission") // Mappa il campo alla colonna total_commission
    private BigDecimal totalCommission; // Totale delle commissioni maturate

    @Column("paid_commission") // Mappa il campo alla colonna paid_commission
    private BigDecimal paidCommission; // Commissioni già pagate

    @Column("pending_commission") // Mappa il campo alla colonna pending_commission
    private BigDecimal pendingCommission; // Commissioni ancora da pagare

    @Column("last_updated") // Mappa il campo alla colonna last_updated
    private Instant lastUpdated; // Data e ora dell'ultimo aggiornamento

    public Commission(Long id, // Costruttore completo
                      Long agentId, // Identificativo dell'agente
                      Long contractId, // Identificativo del contratto
                      BigDecimal totalCommission, // Totale maturato
                      BigDecimal paidCommission, // Totale pagato
                      BigDecimal pendingCommission, // Totale pendente
                      Instant lastUpdated) { // Timestamp dell'ultimo aggiornamento
        this.id = id; // Imposta l'id
        this.agentId = agentId; // Imposta l'id dell'agente
        this.contractId = contractId; // Imposta l'id del contratto
        this.totalCommission = totalCommission; // Imposta il totale commissioni
        this.paidCommission = paidCommission; // Imposta le commissioni pagate
        this.pendingCommission = pendingCommission; // Imposta le commissioni pendenti
        this.lastUpdated = lastUpdated; // Imposta la data di aggiornamento
    }

    public static Commission create(Long agentId, Long contractId, BigDecimal totalCommission) { // Factory method per una nuova commissione
        return new Commission(null, agentId, contractId, totalCommission, BigDecimal.ZERO, totalCommission, Instant.now()); // Crea una nuova istanza con importi iniziali
    }

    public Commission update(BigDecimal totalCommission, BigDecimal paidCommission, BigDecimal pendingCommission, Instant lastUpdated) { // Restituisce una copia aggiornata
        return new Commission(id, agentId, contractId, totalCommission, paidCommission, pendingCommission, lastUpdated); // Crea una nuova istanza con i valori aggiornati
    }

    public Long getId() { // Restituisce l'id della commissione
        return id; // Ritorna il valore di id
    }

    public Long getAgentId() { // Restituisce l'id dell'agente
        return agentId; // Ritorna il valore di agentId
    }

    public Long getContractId() { // Restituisce l'id del contratto
        return contractId; // Ritorna il valore di contractId
    }

    public BigDecimal getTotalCommission() { // Restituisce il totale commissioni
        return totalCommission; // Ritorna il valore di totalCommission
    }

    public BigDecimal getPaidCommission() { // Restituisce le commissioni pagate
        return paidCommission; // Ritorna il valore di paidCommission
    }

    public BigDecimal getPendingCommission() { // Restituisce le commissioni pendenti
        return pendingCommission; // Ritorna il valore di pendingCommission
    }

    public Instant getLastUpdated() { // Restituisce la data di ultimo aggiornamento
        return lastUpdated; // Ritorna il valore di lastUpdated
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public boolean equals(Object o) { // Confronta due commissioni per uguaglianza
        if (this == o) return true; // Se i riferimenti coincidono, sono uguali
        if (!(o instanceof Commission that)) return false; // Se non è una Commission, non sono uguali
        return Objects.equals(id, that.id); // Confronta gli id per stabilire l'uguaglianza
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public int hashCode() { // Calcola l'hash della commissione
        return Objects.hash(id); // Usa l'id per calcolare l'hash
    }
} // Chiude la definizione della classe
