package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.math.BigDecimal;                                      // Tipo preciso per quantità e importi monetari.
import java.util.Objects;                                         // Utility per equals() e hashCode().

/**
 * DTO per rappresentare una riga di fattura con riferimento all'articolo.
 * Contiene dati economici e descrittivi utili per calcolo e visualizzazione.
 */
public class InvoiceLineDTO {                                     // DTO mutabile per modellare le righe di fattura.

    private Long id;                                              // Identificatore della riga.
    private Long invoiceId;                                       // ID della fattura di cui la riga fa parte.
    private Long articleId;                                       // ID dell’articolo collegato.
    private String articleCode;                                   // Codice articolo (ridondante, utile per stampa/visualizzazione).
    private String description;                                   // Descrizione dell’articolo o della riga.
    private BigDecimal quantity;                                  // Quantità fatturata.
    private BigDecimal unitPrice;                                 // Prezzo unitario dell’articolo.
    private BigDecimal vatRate;                                   // Aliquota IVA applicata alla riga.
    private BigDecimal total;                                     // Totale riga (qty × unitPrice ± IVA a seconda del dominio).

    public InvoiceLineDTO() {                                     // Costruttore vuoto richiesto dai sistemi di serializzazione.
    }

    public InvoiceLineDTO(Long id,
                          Long invoiceId,
                          Long articleId,
                          String articleCode,
                          String description,
                          BigDecimal quantity,
                          BigDecimal unitPrice,
                          BigDecimal vatRate,
                          BigDecimal total) {                     // Costruttore completo per inizializzazione diretta.
        this.id = id;
        this.invoiceId = invoiceId;
        this.articleId = articleId;
        this.articleCode = articleCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
        this.total = total;
    }

    public Long getId() {                                         // Restituisce l’ID della riga fattura.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID della riga fattura.
        this.id = id;
    }

    public Long getInvoiceId() {                                  // Restituisce l’ID della fattura associata.
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {                    // Imposta l’ID della fattura associata.
        this.invoiceId = invoiceId;
    }

    public Long getArticleId() {                                  // Restituisce l’ID dell’articolo.
        return articleId;
    }

    public void setArticleId(Long articleId) {                    // Imposta l’ID dell’articolo.
        this.articleId = articleId;
    }

    public String getArticleCode() {                              // Restituisce il codice articolo.
        return articleCode;
    }

    public void setArticleCode(String articleCode) {              // Imposta il codice articolo.
        this.articleCode = articleCode;
    }

    public String getDescription() {                              // Restituisce la descrizione della riga.
        return description;
    }

    public void setDescription(String description) {              // Imposta la descrizione.
        this.description = description;
    }

    public BigDecimal getQuantity() {                             // Restituisce la quantità della riga.
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {                // Imposta la quantità della riga.
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {                            // Restituisce il prezzo unitario.
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {              // Imposta il prezzo unitario.
        this.unitPrice = unitPrice;
    }

    public BigDecimal getVatRate() {                              // Restituisce l’aliquota IVA applicata.
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {                  // Imposta l’aliquota IVA.
        this.vatRate = vatRate;
    }

    public BigDecimal getTotal() {                                // Restituisce il totale della riga.
        return total;
    }

    public void setTotal(BigDecimal total) {                      // Imposta il totale della riga.
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due InvoiceLineDTO basandosi sull’ID.
        if (this == o) {                                          // Stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Se null o classi diverse → non uguali.
            return false;
        }
        InvoiceLineDTO that = (InvoiceLineDTO) o;                 // Cast dopo controllo tipo.
        return Objects.equals(id, that.id);                       // Confronto basato solo sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                    // Rappresentazione leggibile per log/debug.
        return "InvoiceLineDTO{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", articleId=" + articleId +
                ", articleCode='" + articleCode + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", total=" + total +
                '}';
    }
}                                                                  // Fine della classe InvoiceLineDTO.
