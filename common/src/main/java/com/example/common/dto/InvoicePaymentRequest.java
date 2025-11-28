package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.math.BigDecimal;                                      // Tipo preciso per rappresentare importi monetari.
import java.time.LocalDate;                                       // Tipo per rappresentare date senza timezone.
import java.util.Objects;                                         // Utility per equals() e hashCode().

/**
 * Payload per registrare un pagamento di fattura.
 * Contiene i dati minimi necessari per aggiornare lo stato di pagamento.
 */
public class InvoicePaymentRequest {                              // DTO mutabile per la richiesta di registrazione pagamento.

    private Long id;                                              // Identificatore della richiesta (opzionale, può essere null).
    private LocalDate paymentDate;                                // Data in cui è avvenuto il pagamento.
    private BigDecimal amountPaid;                                // Importo corrisposto dal cliente.

    public InvoicePaymentRequest() {                              // Costruttore vuoto richiesto da framework di serializzazione.
    }

    public InvoicePaymentRequest(LocalDate paymentDate,           
                                 BigDecimal amountPaid) {         // Costruttore completo per creazione rapida da client.
        this.paymentDate = paymentDate;
        this.amountPaid = amountPaid;
    }

    public Long getId() {                                         // Restituisce l’ID della richiesta (se gestito).
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID della richiesta.
        this.id = id;
    }

    public LocalDate getPaymentDate() {                           // Restituisce la data del pagamento.
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {           // Imposta la data del pagamento.
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmountPaid() {                           // Restituisce l’importo pagato.
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {            // Imposta l’importo pagato.
        this.amountPaid = amountPaid;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due InvoicePaymentRequest basandosi sull’ID.
        if (this == o) {                                          // Stessa istanza → oggetti identici.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Controllo tipo → se diverso → false.
            return false;
        }
        InvoicePaymentRequest that = (InvoicePaymentRequest) o;   // Cast dopo verifica.
        return Objects.equals(id, that.id);                       // Confronto basato solo sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe InvoicePaymentRequest.
