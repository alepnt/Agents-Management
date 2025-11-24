package com.example.server.domain; // Pacchetto delle entità di dominio

import org.springframework.data.annotation.Id; // Annotazione per il campo identificativo
import org.springframework.data.relational.core.mapping.Column; // Mappa un campo su una colonna
import org.springframework.data.relational.core.mapping.Table; // Mappa la classe su una tabella

import java.math.BigDecimal; // Gestisce numeri decimali con precisione
import java.util.Objects; // Utility per equals e hashCode

@Table("invoice_lines") // Associa la classe alla tabella "invoice_lines"
public class InvoiceLine { // Rappresenta una riga di dettaglio di fattura

    @Id // Identificativo univoco della riga
    private Long id; // Campo ID della riga

    @Column("invoice_id") // Colonna per l'ID della fattura
    private Long invoiceId; // Identificativo della fattura a cui appartiene la riga

    @Column("article_id") // Colonna per l'ID dell'articolo
    private Long articleId; // Identificativo dell'articolo fatturato

    @Column("article_code") // Colonna per il codice articolo
    private String articleCode; // Codice che identifica l'articolo

    private String description; // Descrizione della riga

    private BigDecimal quantity; // Quantità fatturata

    @Column("unit_price") // Colonna per il prezzo unitario
    private BigDecimal unitPrice; // Prezzo per unità

    @Column("vat_rate") // Colonna per l'aliquota IVA
    private BigDecimal vatRate; // Aliquota IVA applicata

    private BigDecimal total; // Totale calcolato per la riga

    public InvoiceLine(Long id, // Costruttore completo con ID
                       Long invoiceId, // ID della fattura
                       Long articleId, // ID dell'articolo
                       String articleCode, // Codice articolo
                       String description, // Descrizione della riga
                       BigDecimal quantity, // Quantità
                       BigDecimal unitPrice, // Prezzo unitario
                       BigDecimal vatRate, // Aliquota IVA
                       BigDecimal total) { // Totale della riga
        this.id = id; // Assegna l'ID
        this.invoiceId = invoiceId; // Imposta l'ID della fattura
        this.articleId = articleId; // Imposta l'ID dell'articolo
        this.articleCode = articleCode; // Imposta il codice articolo
        this.description = description; // Imposta la descrizione
        this.quantity = quantity; // Imposta la quantità
        this.unitPrice = unitPrice; // Imposta il prezzo unitario
        this.vatRate = vatRate; // Imposta l'aliquota IVA
        this.total = total; // Imposta il totale
    }

    public static InvoiceLine create(Long invoiceId, // Factory method per creare una riga senza ID
                                     Long articleId, // ID dell'articolo
                                     String articleCode, // Codice articolo
                                     String description, // Descrizione
                                     BigDecimal quantity, // Quantità
                                     BigDecimal unitPrice, // Prezzo unitario
                                     BigDecimal vatRate, // Aliquota IVA
                                     BigDecimal total) { // Totale della riga
        return new InvoiceLine(null, invoiceId, articleId, articleCode, description, quantity, unitPrice, vatRate, total); // Crea una riga con ID nullo
    }

    public InvoiceLine withInvoice(Long invoiceId) { // Ritorna una copia associata a un'altra fattura
        return new InvoiceLine(id, invoiceId, articleId, articleCode, description, quantity, unitPrice, vatRate, total); // Clona la riga aggiornando l'ID fattura
    }

    public Long getId() { // Restituisce l'ID della riga
        return id; // Ritorna l'identificativo
    }

    public Long getInvoiceId() { // Restituisce l'ID della fattura
        return invoiceId; // Ritorna l'identificativo della fattura
    }

    public Long getArticleId() { // Restituisce l'ID dell'articolo
        return articleId; // Ritorna l'identificativo dell'articolo
    }

    public String getArticleCode() { // Restituisce il codice articolo
        return articleCode; // Ritorna il codice
    }

    public String getDescription() { // Restituisce la descrizione della riga
        return description; // Ritorna il testo descrittivo
    }

    public BigDecimal getQuantity() { // Restituisce la quantità fatturata
        return quantity; // Ritorna la quantità
    }

    public BigDecimal getUnitPrice() { // Restituisce il prezzo unitario
        return unitPrice; // Ritorna il prezzo per unità
    }

    public BigDecimal getVatRate() { // Restituisce l'aliquota IVA
        return vatRate; // Ritorna la percentuale IVA
    }

    public BigDecimal getTotal() { // Restituisce il totale della riga
        return total; // Ritorna l'importo totale
    }

    @Override // Ridefinisce equals per la classe
    public boolean equals(Object o) { // Confronta l'oggetto con un altro
        if (this == o) { // Se è lo stesso riferimento
            return true; // Sono uguali
        }
        if (o == null || getClass() != o.getClass()) { // Se l'altro è nullo o di classe differente
            return false; // Non sono uguali
        }
        InvoiceLine that = (InvoiceLine) o; // Effettua il cast sicuro
        return Objects.equals(id, that.id); // Due righe sono uguali se l'ID coincide
    }

    @Override // Ridefinisce hashCode coerente con equals
    public int hashCode() { // Calcola l'hash della riga
        return Objects.hash(id); // Usa l'ID come base
    }
}
