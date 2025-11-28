package com.example.common.dto;                                   // Package che contiene i DTO condivisi fra client e server.

import java.math.BigDecimal;                                      // Tipo numerico ad alta precisione, ideale per importi monetari.
import java.time.Instant;                                         // Timestamp UTC usato per auditing.
import java.util.Objects;                                         // Utility per equals() e hashCode().

/**
 * DTO con i dati aggregati delle commissioni.
 * Utilizzato per rappresentare lo stato corrente delle provvigioni di un agente su un contratto.
 */
public class CommissionDTO {                                      // DTO mutabile che modella le commissioni consolidate.

    private Long id;                                              // Identificatore della riga commissione.
    private Long agentId;                                         // ID dell’agente a cui le commissioni si riferiscono.
    private Long contractId;                                      // ID del contratto di riferimento.
    private BigDecimal totalCommission;                           // Totale delle provvigioni maturate.
    private BigDecimal paidCommission;                            // Parte delle provvigioni già liquidata.
    private BigDecimal pendingCommission;                         // Parte delle provvigioni ancora da liquidare.
    private Instant lastUpdated;                                  // Timestamp dell’ultimo aggiornamento delle commissioni.

    public CommissionDTO() {                                      // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public CommissionDTO(Long id,                                 // Costruttore completo che permette inizializzazione completa.
                         Long agentId,
                         Long contractId,
                         BigDecimal totalCommission,
                         BigDecimal paidCommission,
                         BigDecimal pendingCommission,
                         Instant lastUpdated) {
        this.id = id;
        this.agentId = agentId;
        this.contractId = contractId;
        this.totalCommission = totalCommission;
        this.paidCommission = paidCommission;
        this.pendingCommission = pendingCommission;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {                                         // Restituisce l’ID della commissione.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID della commissione.
        this.id = id;
    }

    public Long getAgentId() {                                    // Restituisce l’ID dell’agente.
        return agentId;
    }

    public void setAgentId(Long agentId) {                        // Imposta l’ID dell’agente.
        this.agentId = agentId;
    }

    public Long getContractId() {                                 // Restituisce l’ID del contratto associato.
        return contractId;
    }

    public void setContractId(Long contractId) {                  // Imposta l’ID del contratto associato.
        this.contractId = contractId;
    }

    public BigDecimal getTotalCommission() {                      // Restituisce il totale delle provvigioni maturate.
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {  // Imposta il totale delle provvigioni maturate.
        this.totalCommission = totalCommission;
    }

    public BigDecimal getPaidCommission() {                       // Restituisce l’importo delle provvigioni già pagate.
        return paidCommission;
    }

    public void setPaidCommission(BigDecimal paidCommission) {    // Imposta l’importo delle provvigioni già pagate.
        this.paidCommission = paidCommission;
    }

    public BigDecimal getPendingCommission() {                    // Restituisce l’importo delle provvigioni non ancora pagate.
        return pendingCommission;
    }

    public void setPendingCommission(BigDecimal pendingCommission) { 
        // Imposta le provvigioni ancora in sospeso.
        this.pendingCommission = pendingCommission;
    }

    public Instant getLastUpdated() {                             // Restituisce il timestamp dell’ultimo aggiornamento.
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {             // Aggiorna il timestamp dell’ultima modifica.
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due CommissionDTO in base all’ID.
        if (this == o) {                                          // Se è la stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Se null o classi diverse → non uguali.
            return false;
        }
        CommissionDTO that = (CommissionDTO) o;                   // Cast sicuro.
        return Objects.equals(id, that.id);                       // Confronto basato esclusivamente sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe CommissionDTO.
