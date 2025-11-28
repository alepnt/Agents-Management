package com.example.common.dto;                                   // Package che contiene tutti i DTO condivisi tra client e server.

import java.math.BigDecimal;                                      // Tipo ad alta precisione per valori monetari.
import java.time.Instant;                                         // Timestamp in UTC usato per audit.
import java.util.Objects;                                         // Utility per equals(), hashCode() e confronti null-safe.

/**
 * DTO per rappresentare un articolo vendibile inseribile nelle fatture.
 * Contiene informazioni descrittive, economiche e di auditing.
 */
public class ArticleDTO {                                         // DTO mutabile per la rappresentazione di un articolo di catalogo.

    private Long id;                                              // Identificativo dell'articolo.
    private String code;                                          // Codice univoco dell'articolo.
    private String name;                                          // Nome dell'articolo.
    private String description;                                   // Descrizione estesa dell'articolo.
    private BigDecimal unitPrice;                                 // Prezzo unitario (importo senza IVA).
    private BigDecimal vatRate;                                   // Aliquota IVA associata all'articolo.
    private String unitOfMeasure;                                 // Unità di misura (es. "pz", "kg", "m").
    private Instant createdAt;                                    // Timestamp di creazione del record.
    private Instant updatedAt;                                    // Timestamp dell’ultimo aggiornamento.

    public ArticleDTO() {                                         // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public ArticleDTO(Long id,                                   // Costruttore completo per inizializzare tutti i campi.
                      String code,
                      String name,
                      String description,
                      BigDecimal unitPrice,
                      BigDecimal vatRate,
                      String unitOfMeasure,
                      Instant createdAt,
                      Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
        this.unitOfMeasure = unitOfMeasure;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {                                         // Restituisce l’ID dell’articolo.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID dell’articolo.
        this.id = id;
    }

    public String getCode() {                                     // Restituisce il codice articolo.
        return code;
    }

    public void setCode(String code) {                            // Imposta il codice articolo.
        this.code = code;
    }

    public String getName() {                                     // Restituisce il nome dell’articolo.
        return name;
    }

    public void setName(String name) {                            // Imposta il nome dell’articolo.
        this.name = name;
    }

    public String getDescription() {                              // Restituisce la descrizione dell’articolo.
        return description;
    }

    public void setDescription(String description) {              // Imposta la descrizione dell’articolo.
        this.description = description;
    }

    public BigDecimal getUnitPrice() {                            // Restituisce il prezzo unitario.
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {              // Imposta il prezzo unitario.
        this.unitPrice = unitPrice;
    }

    public BigDecimal getVatRate() {                              // Restituisce l’aliquota IVA.
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {                  // Imposta l’aliquota IVA.
        this.vatRate = vatRate;
    }

    public String getUnitOfMeasure() {                            // Restituisce l’unità di misura.
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {          // Imposta l’unità di misura.
        this.unitOfMeasure = unitOfMeasure;
    }

    public Instant getCreatedAt() {                               // Restituisce il timestamp di creazione.
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {                 // Imposta il timestamp di creazione.
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {                               // Restituisce il timestamp dell’ultimo aggiornamento.
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {                 // Imposta il timestamp dell’ultimo aggiornamento.
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due ArticleDTO in base all’ID.
        if (this == o) {                                          // Se stesso oggetto → true.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Se null o classi diverse → false.
            return false;
        }
        ArticleDTO that = (ArticleDTO) o;                         // Cast dopo essere certi della classe.
        return Objects.equals(id, that.id);                       // confronto basato solo sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                    // Rappresentazione leggibile utile per logging/debug.
        return "ArticleDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}                                                                  // Fine della classe ArticleDTO.
