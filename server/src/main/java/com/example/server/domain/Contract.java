package com.example.server.domain; // Definisce il package della classe

import com.example.common.enums.ContractStatus; // Importa l'enum che rappresenta lo stato del contratto
import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.math.BigDecimal; // Importa il tipo per valori monetari
import java.time.LocalDate; // Importa il tipo per date
import java.util.Objects; // Importa utilità per confronti e hash

@Table("contracts") // Associa la classe alla tabella "contracts"
public class Contract { // Definisce l'entità Contract

    @Id // Identifica il campo come chiave primaria
    @Column("id") // Colonna primaria in minuscolo
    private Long id; // Identificativo univoco del contratto

    @Column("agent_id") // Mappa il campo alla colonna agent_id
    private Long agentId; // Riferimento all'agente responsabile

    @Column("customer_name") // Mappa il campo alla colonna customer_name
    private String customerName; // Nome del cliente

    private String description; // Descrizione del contratto

    @Column("start_date") // Mappa il campo alla colonna start_date
    private LocalDate startDate; // Data di inizio del contratto

    @Column("end_date") // Mappa il campo alla colonna end_date
    private LocalDate endDate; // Data di fine del contratto

    @Column("total_value") // Mappa il campo alla colonna total_value
    private BigDecimal totalValue; // Valore economico del contratto

    private ContractStatus status; // Stato attuale del contratto

    public Contract(Long id, // Costruttore completo
                    Long agentId, // Identificativo dell'agente
                    String customerName, // Nome del cliente
                    String description, // Descrizione del contratto
                    LocalDate startDate, // Data di inizio
                    LocalDate endDate, // Data di fine
                    BigDecimal totalValue, // Valore totale
                    ContractStatus status) { // Stato del contratto
        this.id = id; // Imposta l'id
        this.agentId = agentId; // Imposta l'id dell'agente
        this.customerName = customerName; // Imposta il nome del cliente
        this.description = description; // Imposta la descrizione
        this.startDate = startDate; // Imposta la data di inizio
        this.endDate = endDate; // Imposta la data di fine
        this.totalValue = totalValue; // Imposta il valore totale
        this.status = status; // Imposta lo stato
    }

    public static Contract create(Long agentId, // Factory method per creare un nuovo contratto
                                  String customerName, // Nome del cliente
                                  String description, // Descrizione del contratto
                                  LocalDate startDate, // Data di inizio
                                  LocalDate endDate, // Data di fine
                                  BigDecimal totalValue, // Valore totale
                                  ContractStatus status) { // Stato del contratto
        return new Contract(null, agentId, customerName, description, startDate, endDate, totalValue, status); // Crea un contratto senza id
    }

    public Contract updateFrom(Contract source) { // Restituisce una copia aggiornata con i dati forniti
        return new Contract(id, // Mantiene lo stesso id
                source.agentId, // Aggiorna l'id agente
                source.customerName, // Aggiorna il nome cliente
                source.description, // Aggiorna la descrizione
                source.startDate, // Aggiorna la data di inizio
                source.endDate, // Aggiorna la data di fine
                source.totalValue, // Aggiorna il valore totale
                source.status); // Aggiorna lo stato
    }

    public Long getId() { // Restituisce l'id del contratto
        return id; // Ritorna il valore di id
    }

    public Long getAgentId() { // Restituisce l'id dell'agente
        return agentId; // Ritorna il valore di agentId
    }

    public String getCustomerName() { // Restituisce il nome del cliente
        return customerName; // Ritorna il valore di customerName
    }

    public String getDescription() { // Restituisce la descrizione del contratto
        return description; // Ritorna il valore di description
    }

    public LocalDate getStartDate() { // Restituisce la data di inizio
        return startDate; // Ritorna il valore di startDate
    }

    public LocalDate getEndDate() { // Restituisce la data di fine
        return endDate; // Ritorna il valore di endDate
    }

    public BigDecimal getTotalValue() { // Restituisce il valore totale
        return totalValue; // Ritorna il valore di totalValue
    }

    public ContractStatus getStatus() { // Restituisce lo stato del contratto
        return status; // Ritorna il valore di status
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public boolean equals(Object o) { // Confronta due contratti per uguaglianza
        if (this == o) return true; // Se i riferimenti coincidono, sono uguali
        if (!(o instanceof Contract contract)) return false; // Se non è un Contract, non sono uguali
        return Objects.equals(id, contract.id); // Confronta gli id per stabilire l'uguaglianza
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public int hashCode() { // Calcola l'hash del contratto
        return Objects.hash(id); // Usa l'id per calcolare l'hash
    }
} // Chiude la definizione della classe
