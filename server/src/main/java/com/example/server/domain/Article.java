package com.example.server.domain; // Definisce il package della classe

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Id; // Importa l'annotazione per identificare la chiave primaria
import org.springframework.data.annotation.LastModifiedDate; // Importa l'annotazione per tracciare l'ultimo aggiornamento
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.math.BigDecimal; // Importa il tipo per gestire valori monetari
import java.time.Instant; // Importa il tipo per gestire timestamp
import java.util.Objects; // Importa utilità per confronti e hash

@Table("articles") // Associa la classe alla tabella "articles"
public class Article { // Definisce l'entità Article

    @Id // Identifica il campo come chiave primaria
    private Long id; // Identificativo univoco dell'articolo

    @NotBlank(message = "Il codice articolo è obbligatorio")
    private String code; // Codice articolo

    @NotBlank(message = "Il nome articolo è obbligatorio")
    private String name; // Nome dell'articolo

    private String description; // Descrizione dell'articolo

    @Column("unit_price") // Mappa il campo alla colonna unit_price
    @NotNull(message = "Il prezzo unitario è obbligatorio")
    @PositiveOrZero(message = "Il prezzo unitario deve essere positivo")
    private BigDecimal unitPrice; // Prezzo unitario

    @Column("vat_rate") // Mappa il campo alla colonna vat_rate
    @NotNull(message = "L'aliquota IVA è obbligatoria")
    @PositiveOrZero(message = "L'aliquota IVA deve essere positiva")
    private BigDecimal vatRate; // Aliquota IVA

    @Column("unit_of_measure") // Mappa il campo alla colonna unit_of_measure
    @NotBlank(message = "L'unità di misura è obbligatoria")
    private String unitOfMeasure; // Unità di misura dell'articolo

    @Column("created_at") // Mappa il campo alla colonna created_at
    private Instant createdAt; // Timestamp di creazione

    @LastModifiedDate // Indica che il campo viene aggiornato automaticamente all'ultimo salvataggio
    @Column("updated_at") // Mappa il campo alla colonna updated_at
    private Instant updatedAt; // Timestamp di ultimo aggiornamento

    public Article(Long id, // Costruttore completo con tutti i campi
                   String code, // Codice articolo
                   String name, // Nome dell'articolo
                   String description, // Descrizione dell'articolo
                   BigDecimal unitPrice, // Prezzo unitario
                   BigDecimal vatRate, // Aliquota IVA
                   String unitOfMeasure, // Unità di misura
                   Instant createdAt, // Data di creazione
                   Instant updatedAt) { // Data di ultimo aggiornamento
        this.id = id; // Imposta l'id
        this.code = code; // Imposta il codice
        this.name = name; // Imposta il nome
        this.description = description; // Imposta la descrizione
        this.unitPrice = unitPrice; // Imposta il prezzo unitario
        this.vatRate = vatRate; // Imposta l'aliquota IVA
        this.unitOfMeasure = unitOfMeasure; // Imposta l'unità di misura
        this.createdAt = createdAt; // Imposta la data di creazione
        this.updatedAt = updatedAt; // Imposta la data di ultimo aggiornamento
    }

    public static Article create(String code, // Factory method per creare un nuovo articolo senza id
                                 String name, // Nome dell'articolo
                                 String description, // Descrizione dell'articolo
                                 BigDecimal unitPrice, // Prezzo unitario
                                 BigDecimal vatRate, // Aliquota IVA
                                 String unitOfMeasure) { // Unità di misura
        return new Article(null, code, name, description, unitPrice, vatRate, unitOfMeasure, null, null); // Crea un nuovo articolo con campi temporali null
    }

    public Article withId(Long id) { // Restituisce una copia dell'articolo con un id specificato
        return new Article(id, code, name, description, unitPrice, vatRate, unitOfMeasure, createdAt, updatedAt); // Crea una nuova istanza con l'id passato
    }

    public Article updateFrom(Article source) { // Restituisce una copia aggiornata dai dati di un altro articolo
        return new Article(id, // Mantiene lo stesso id
                source.code, // Usa il nuovo codice
                source.name, // Usa il nuovo nome
                source.description, // Usa la nuova descrizione
                source.unitPrice, // Usa il nuovo prezzo unitario
                source.vatRate, // Usa la nuova aliquota IVA
                source.unitOfMeasure, // Usa la nuova unità di misura
                createdAt, // Mantiene la data di creazione originale
                updatedAt); // Mantiene la data di ultimo aggiornamento corrente
    }

    public Long getId() { // Restituisce l'id dell'articolo
        return id; // Ritorna il valore di id
    }

    public String getCode() { // Restituisce il codice articolo
        return code; // Ritorna il valore di code
    }

    public String getName() { // Restituisce il nome dell'articolo
        return name; // Ritorna il valore di name
    }

    public String getDescription() { // Restituisce la descrizione
        return description; // Ritorna il valore di description
    }

    public BigDecimal getUnitPrice() { // Restituisce il prezzo unitario
        return unitPrice; // Ritorna il valore di unitPrice
    }

    public BigDecimal getVatRate() { // Restituisce l'aliquota IVA
        return vatRate; // Ritorna il valore di vatRate
    }

    public String getUnitOfMeasure() { // Restituisce l'unità di misura
        return unitOfMeasure; // Ritorna il valore di unitOfMeasure
    }

    public Instant getCreatedAt() { // Restituisce la data di creazione
        return createdAt; // Ritorna il valore di createdAt
    }

    public Instant getUpdatedAt() { // Restituisce la data di ultimo aggiornamento
        return updatedAt; // Ritorna il valore di updatedAt
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public boolean equals(Object o) { // Confronta due articoli per uguaglianza
        if (this == o) { // Se i riferimenti coincidono, sono uguali
            return true; // Restituisce vero
        }
        if (o == null || getClass() != o.getClass()) { // Se l'oggetto è nullo o di classe diversa
            return false; // Restituisce falso
        }
        Article article = (Article) o; // Effettua il cast a Article
        return Objects.equals(id, article.id); // Confronta gli id per stabilire l'uguaglianza
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public int hashCode() { // Calcola l'hash dell'articolo
        return Objects.hash(id); // Usa l'id per calcolare l'hash
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", vatRate=" + vatRate +
                ", unitOfMeasure='" + unitOfMeasure + '\'' +
                '}';
    }
} // Chiude la definizione della classe
