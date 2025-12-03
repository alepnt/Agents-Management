package com.example.server.domain; // Pacchetto che contiene le entità di dominio

import com.example.common.enums.InvoiceStatus; // Enum che rappresenta lo stato della fattura
import org.springframework.data.annotation.Id; // Indica il campo identificativo
import org.springframework.data.annotation.LastModifiedDate; // Gestisce l'aggiornamento automatico della data di modifica
import org.springframework.data.relational.core.mapping.Column; // Mappa un campo su una colonna del database
import org.springframework.data.relational.core.mapping.Table; // Mappa la classe su una tabella del database

import java.math.BigDecimal; // Gestisce importi monetari con precisione
import java.time.Instant; // Rappresenta un timestamp istantaneo
import java.time.LocalDate; // Rappresenta date senza orario
import java.util.Objects; // Utility per equals e hashCode

@Table("invoices") // Collega la classe alla tabella "invoices"
public class Invoice { // Entità che rappresenta una fattura

    @Id // Identificatore univoco della fattura
    @Column("id") // Colonna primaria in minuscolo
    private Long id; // Campo per l'ID

    @Column("contract_id") // Colonna che memorizza il contratto collegato
    private Long contractId; // ID del contratto associato

    @Column("invoice_number") // Colonna per il numero della fattura
    private String number; // Numero progressivo della fattura

    @Column("customer_id") // Colonna per l'ID del cliente
    private Long customerId; // Identificativo del cliente

    @Column("customer_name") // Colonna per il nome del cliente
    private String customerName; // Nome del cliente al momento dell'emissione

    private BigDecimal amount; // Importo totale della fattura

    @Column("issue_date") // Colonna per la data di emissione
    private LocalDate issueDate; // Data in cui è stata emessa la fattura

    @Column("due_date") // Colonna per la data di scadenza
    private LocalDate dueDate; // Data entro cui deve avvenire il pagamento

    private InvoiceStatus status; // Stato attuale della fattura

    @Column("payment_date") // Colonna per la data di pagamento
    private LocalDate paymentDate; // Data in cui è stato registrato il pagamento

    private String notes; // Note aggiuntive sulla fattura

    @Column("created_at") // Colonna per la data di creazione
    private Instant createdAt; // Timestamp di creazione del record

    @LastModifiedDate // Gestisce l'aggiornamento automatico della data di modifica
    @Column("updated_at") // Colonna per l'ultima modifica
    private Instant updatedAt; // Timestamp dell'ultima modifica

    public Invoice(Long id, // Costruttore completo con ID
                   Long contractId, // ID del contratto collegato
                   String number, // Numero della fattura
                   Long customerId, // ID del cliente
                   String customerName, // Nome del cliente
                   BigDecimal amount, // Importo totale
                   LocalDate issueDate, // Data di emissione
                   LocalDate dueDate, // Data di scadenza
                   InvoiceStatus status, // Stato della fattura
                   LocalDate paymentDate, // Data di pagamento
                   String notes, // Note sulla fattura
                   Instant createdAt, // Timestamp di creazione
                   Instant updatedAt) { // Timestamp di ultima modifica
        this.id = id; // Imposta l'ID
        this.contractId = contractId; // Imposta il contratto associato
        this.number = number; // Imposta il numero della fattura
        this.customerId = customerId; // Imposta l'ID del cliente
        this.customerName = customerName; // Imposta il nome del cliente
        this.amount = amount; // Imposta l'importo
        this.issueDate = issueDate; // Imposta la data di emissione
        this.dueDate = dueDate; // Imposta la data di scadenza
        this.status = status; // Imposta lo stato
        this.paymentDate = paymentDate; // Imposta la data di pagamento
        this.notes = notes; // Imposta le note
        this.createdAt = createdAt; // Imposta il timestamp di creazione
        this.updatedAt = updatedAt; // Imposta il timestamp di modifica
    }

    public static Invoice create(Long contractId, // Metodo factory per creare una fattura senza ID
                                 String number, // Numero della nuova fattura
                                 Long customerId, // ID del cliente
                                 String customerName, // Nome del cliente
                                 BigDecimal amount, // Importo da fatturare
                                 LocalDate issueDate, // Data di emissione
                                 LocalDate dueDate, // Data di scadenza
                                 InvoiceStatus status, // Stato iniziale
                                 String notes) { // Note opzionali
        return new Invoice(null, contractId, number, customerId, customerName, amount, issueDate, dueDate, status, null, notes, null, null); // Crea un'istanza senza ID e dati temporali
    }

    public Invoice withId(Long id) { // Restituisce una copia della fattura con un nuovo ID
        return new Invoice(id, contractId, number, customerId, customerName, amount, issueDate, dueDate, status, paymentDate, notes, createdAt, updatedAt); // Clona l'oggetto impostando l'ID
    }

    public Invoice updateFrom(Invoice source) { // Crea una nuova fattura copiando i dati da un'altra
        return new Invoice(id, // Mantiene l'ID corrente
                source.contractId, // Aggiorna il contratto
                source.number, // Aggiorna il numero
                source.customerId, // Aggiorna l'ID cliente
                source.customerName, // Aggiorna il nome cliente
                source.amount, // Aggiorna l'importo
                source.issueDate, // Aggiorna la data di emissione
                source.dueDate, // Aggiorna la data di scadenza
                source.status, // Aggiorna lo stato
                source.paymentDate, // Aggiorna la data di pagamento
                source.notes, // Aggiorna le note
                createdAt, // Mantiene la data di creazione
                updatedAt); // Mantiene la data di modifica
    }

    public Invoice registerPayment(LocalDate paymentDate, InvoiceStatus newStatus) { // Restituisce una copia con pagamento registrato
        return new Invoice(id, contractId, number, customerId, customerName, amount, issueDate, dueDate, newStatus, paymentDate, notes, createdAt, updatedAt); // Aggiorna stato e data di pagamento
    }

    public Long getId() { // Ritorna l'ID della fattura
        return id; // Restituisce l'identificativo
    }

    public Long getContractId() { // Ritorna l'ID del contratto associato
        return contractId; // Restituisce l'identificativo del contratto
    }

    public String getNumber() { // Ritorna il numero della fattura
        return number; // Restituisce il valore numerico
    }

    public Long getCustomerId() { // Ritorna l'ID del cliente
        return customerId; // Restituisce l'identificativo del cliente
    }

    public String getCustomerName() { // Ritorna il nome del cliente
        return customerName; // Restituisce il nome memorizzato
    }

    public BigDecimal getAmount() { // Ritorna l'importo totale
        return amount; // Restituisce l'importo
    }

    public LocalDate getIssueDate() { // Ritorna la data di emissione
        return issueDate; // Restituisce la data memorizzata
    }

    public LocalDate getDueDate() { // Ritorna la data di scadenza
        return dueDate; // Restituisce la data memorizzata
    }

    public InvoiceStatus getStatus() { // Ritorna lo stato della fattura
        return status; // Restituisce lo stato corrente
    }

    public LocalDate getPaymentDate() { // Ritorna la data di pagamento
        return paymentDate; // Restituisce la data registrata
    }

    public String getNotes() { // Ritorna le note della fattura
        return notes; // Restituisce il testo delle note
    }

    public Instant getCreatedAt() { // Ritorna il timestamp di creazione
        return createdAt; // Restituisce la data di creazione
    }

    public Instant getUpdatedAt() { // Ritorna il timestamp di modifica
        return updatedAt; // Restituisce l'ultima data di aggiornamento
    }

    @Override // Ridefinisce equals per la classe
    public boolean equals(Object o) { // Confronta l'oggetto con un altro
        if (this == o) return true; // Se sono lo stesso riferimento, sono uguali
        if (!(o instanceof Invoice invoice)) return false; // Se l'altro non è una fattura, non sono uguali
        return Objects.equals(id, invoice.id); // Due fatture sono uguali se hanno lo stesso ID
    }

    @Override // Ridefinisce hashCode in coerenza con equals
    public int hashCode() { // Calcola l'hash della fattura
        return Objects.hash(id); // Usa l'ID come base per l'hash
    }
}
