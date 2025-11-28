package com.example.common.dto;                                   // Package contenente i DTO condivisi fra client e server.

import java.math.BigDecimal;                    // Enum che rappresenta lo stato del contratto.
import java.time.LocalDate;                                      // Tipo preciso per valori monetari.
import java.util.Objects;                                       // Tipo per date senza fuso orario.

import com.example.common.enums.ContractStatus;                                         // Utility per equals(), hashCode() e confronti.

/**
 * DTO condiviso per rappresentare i contratti commerciali.
 * Contiene tutte le informazioni principali utili ai moduli client e server.
 */
public class ContractDTO {                                        // DTO mutabile che descrive un contratto commerciale.

    private Long id;                                              // Identificatore del contratto.
    private Long agentId;                                         // ID dell’agente responsabile del contratto.
    private String customerName;                                  // Nome del cliente associato al contratto.
    private String description;                                   // Descrizione testuale del contratto.
    private LocalDate startDate;                                  // Data di inizio validità del contratto.
    private LocalDate endDate;                                    // Data di termine del contratto.
    private BigDecimal totalValue;                                // Valore economico totale del contratto.
    private ContractStatus status;                                // Stato attuale del contratto (ACTIVE, CLOSED, ecc.).

    public ContractDTO() {                                        // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public ContractDTO(Long id,                                   // Costruttore completo per inizializzazione custom.
                       Long agentId,
                       String customerName,
                       String description,
                       LocalDate startDate,
                       LocalDate endDate,
                       BigDecimal totalValue,
                       ContractStatus status) {
        this.id = id;
        this.agentId = agentId;
        this.customerName = customerName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalValue = totalValue;
        this.status = status;
    }

    public Long getId() {                                         // Restituisce l’ID del contratto.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID del contratto.
        this.id = id;
    }

    public Long getAgentId() {                                    // Restituisce l’ID dell’agente assegnato.
        return agentId;
    }

    public void setAgentId(Long agentId) {                        // Imposta l’ID dell’agente assegnato.
        this.agentId = agentId;
    }

    public String getCustomerName() {                             // Restituisce il nome del cliente.
        return customerName;
    }

    public void setCustomerName(String customerName) {            // Imposta il nome del cliente.
        this.customerName = customerName;
    }

    public String getDescription() {                              // Restituisce la descrizione del contratto.
        return description;
    }

    public void setDescription(String description) {              // Imposta la descrizione del contratto.
        this.description = description;
    }

    public LocalDate getStartDate() {                             // Restituisce la data di inizio validità.
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {               // Imposta la data di inizio.
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {                               // Restituisce la data di fine validità.
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {                   // Imposta la data di fine contratto.
        this.endDate = endDate;
    }

    public BigDecimal getTotalValue() {                           // Restituisce il valore economico totale.
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {            // Imposta il valore economico totale.
        this.totalValue = totalValue;
    }

    public ContractStatus getStatus() {                           // Restituisce lo stato attuale del contratto.
        return status;
    }

    public void setStatus(ContractStatus status) {                // Imposta lo stato del contratto.
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due ContractDTO basandosi sull’ID.
        if (this == o) {                                          // Se stesso oggetto → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Se null o classi diverse → non uguali.
            return false;
        }
        ContractDTO that = (ContractDTO) o;                       // Cast dopo controllo di classe.
        return Objects.equals(id, that.id);                       // Confronto basato sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                    // Rappresentazione leggibile utile per log/debug.
        return "ContractDTO{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", customerName='" + customerName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalValue=" + totalValue +
                ", status=" + status +
                '}';
    }
}                                                                  // Fine della classe ContractDTO.
