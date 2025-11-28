package com.example.common.dto;                                   // Package che contiene i DTO condivisi per la comunicazione client–server.

import java.math.BigDecimal;                    // Enum che rappresenta lo stato della fattura (OPEN, PAID, ecc.).
import java.time.LocalDate;                                      // Tipo numerico ad alta precisione per importi monetari.
import java.util.ArrayList;                                       // Tipo per date senza riferimento al fuso.
import java.util.List;                                       // Implementazione mutabile usata per copie difensive.
import java.util.Objects;                                            // Interfaccia per liste di DTO.

import com.example.common.enums.InvoiceStatus;                                         // Utility per equals(), hashCode(), confronti null-safe.

/**
 * DTO condiviso per rappresentare le fatture.
 * Comprende dati economici, anagrafici e lo stato corrente.
 */
public class InvoiceDTO {                                         // DTO mutabile che modella una fattura commerciale.

    private Long id;                                              // Identificatore univoco della fattura.
    private String number;                                        // Numero della fattura.
    private Long contractId;                                      // ID del contratto associato alla fattura.
    private Long customerId;                                      // ID del cliente fatturato.
    private String customerName;                                  // Nome del cliente (ridondanza utile per query rapide).
    private BigDecimal amount;                                    // Importo totale della fattura (IVA inclusa o esclusa a seconda del dominio).
    private LocalDate issueDate;                                  // Data di emissione della fattura.
    private LocalDate dueDate;                                    // Data di scadenza per il pagamento.
    private InvoiceStatus status;                                 // Stato corrente della fattura (es. PAID, OVERDUE).
    private LocalDate paymentDate;                                // Data in cui la fattura è stata pagata (se applicabile).
    private String notes;                                         // Eventuali note o informazioni aggiuntive.
    private List<InvoiceLineDTO> lines = new ArrayList<>();       // Lista delle righe della fattura.

    public InvoiceDTO() {                                         // Costruttore vuoto richiesto da framework di serializzazione.
    }

    public InvoiceDTO(Long id,
                      String number,
                      Long contractId,
                      Long customerId,
                      String customerName,
                      BigDecimal amount,
                      LocalDate issueDate,
                      LocalDate dueDate,
                      InvoiceStatus status,
                      LocalDate paymentDate,
                      String notes,
                      List<InvoiceLineDTO> lines) {               // Costruttore completo per inizializzazione diretta del DTO.
        this.id = id;
        this.number = number;
        this.contractId = contractId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.paymentDate = paymentDate;
        this.notes = notes;

        if (lines != null) {                                      // Copia difensiva delle righe.
            this.lines = new ArrayList<>(lines);
        }
    }

    public Long getId() {                                         // Restituisce l'ID fattura.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l'ID fattura.
        this.id = id;
    }

    public String getNumber() {                                   // Restituisce il numero fattura.
        return number;
    }

    public void setNumber(String number) {                        // Imposta il numero fattura.
        this.number = number;
    }

    public Long getContractId() {                                 // Restituisce l'ID del contratto associato.
        return contractId;
    }

    public void setContractId(Long contractId) {                  // Imposta l'ID del contratto associato.
        this.contractId = contractId;
    }

    public Long getCustomerId() {                                 // Restituisce l'ID del cliente.
        return customerId;
    }

    public void setCustomerId(Long customerId) {                  // Imposta l'ID del cliente.
        this.customerId = customerId;
    }

    public String getCustomerName() {                             // Restituisce il nome del cliente.
        return customerName;
    }

    public void setCustomerName(String customerName) {            // Imposta il nome del cliente.
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {                               // Restituisce l'importo totale della fattura.
        return amount;
    }

    public void setAmount(BigDecimal amount) {                    // Imposta l'importo totale della fattura.
        this.amount = amount;
    }

    public LocalDate getIssueDate() {                             // Restituisce la data di emissione.
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {               // Imposta la data di emissione della fattura.
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {                               // Restituisce la data di scadenza.
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {                   // Imposta la data di scadenza.
        this.dueDate = dueDate;
    }

    public InvoiceStatus getStatus() {                            // Restituisce lo stato della fattura.
        return status;
    }

    public void setStatus(InvoiceStatus status) {                 // Imposta lo stato della fattura.
        this.status = status;
    }

    public LocalDate getPaymentDate() {                           // Restituisce la data di pagamento, se esiste.
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {           // Imposta la data di pagamento della fattura.
        this.paymentDate = paymentDate;
    }

    public String getNotes() {                                    // Restituisce eventuali note aggiuntive.
        return notes;
    }

    public void setNotes(String notes) {                          // Imposta le note aggiuntive.
        this.notes = notes;
    }

    public List<InvoiceLineDTO> getLines() {                      // Restituisce la lista delle righe della fattura.
        return lines;
    }

    public void setLines(List<InvoiceLineDTO> lines) {            // Imposta le righe, applicando copia difensiva.
        this.lines = lines != null ? new ArrayList<>(lines)
                                   : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due InvoiceDTO basandosi sull'ID.
        if (this == o) {                                          // Se è la stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Tipi diversi → non uguali.
            return false;
        }
        InvoiceDTO that = (InvoiceDTO) o;                         // Cast sicuro.
        return Objects.equals(id, that.id);                       // Confronto basato sull'ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                    // Rappresentazione leggibile per debug/logging.
        return "InvoiceDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", contractId=" + contractId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", amount=" + amount +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", notes='" + notes + '\'' +
                ", lines=" + lines +
                '}';
    }
}                                                                  // Fine della classe InvoiceDTO.
