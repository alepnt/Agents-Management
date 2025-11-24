package com.example.server.domain; // Pacchetto che raggruppa le entità di dominio del server

import com.example.common.enums.DocumentAction; // Enum che rappresenta le azioni effettuate sul documento
import com.example.common.enums.DocumentType; // Enum che identifica il tipo di documento
import org.springframework.data.annotation.Id; // Annotazione per indicare il campo identificativo
import org.springframework.data.relational.core.mapping.Column; // Annotazione per mappare un campo su una colonna
import org.springframework.data.relational.core.mapping.Table; // Annotazione per mappare la classe su una tabella

import java.time.Instant; // Rappresenta l'istante temporale della creazione
import java.util.Objects; // Utilità per equals e hashCode

@Table("document_history") // Associa la classe alla tabella "document_history"
public class DocumentHistory { // Modello di dominio che traccia le azioni compiute sui documenti

    @Id // Identificativo univoco del record
    private Long id; // Campo per l'ID della riga

    @Column("document_type") // Colonna che memorizza il tipo di documento
    private DocumentType documentType; // Valore che rappresenta il tipo del documento interessato

    @Column("document_id") // Colonna che memorizza l'ID del documento
    private Long documentId; // Identificativo del documento su cui è avvenuta l'azione

    private DocumentAction action; // Azione effettuata sul documento (creazione, modifica, ecc.)

    private String description; // Eventuale descrizione dell'evento registrato

    @Column("created_at") // Colonna che memorizza il momento dell'azione
    private Instant createdAt; // Timestamp in cui l'azione è stata eseguita

    public DocumentHistory(Long id, // Costruttore completo con ID opzionale
                           DocumentType documentType, // Tipo di documento
                           Long documentId, // ID del documento
                           DocumentAction action, // Azione eseguita
                           String description, // Descrizione dell'azione
                           Instant createdAt) { // Momento di creazione della traccia
        this.id = id; // Assegna l'ID
        this.documentType = documentType; // Imposta il tipo di documento
        this.documentId = documentId; // Imposta l'ID del documento
        this.action = action; // Imposta l'azione
        this.description = description; // Imposta la descrizione
        this.createdAt = createdAt; // Imposta il timestamp
    }

    public static DocumentHistory create(DocumentType type, Long documentId, DocumentAction action, String description, Instant createdAt) { // Factory method che crea una traccia senza ID preimpostato
        return new DocumentHistory(null, type, documentId, action, description, createdAt); // Istanzia un nuovo DocumentHistory con ID nullo
    }

    public Long getId() { // Restituisce l'ID del record
        return id; // Ritorna l'identificativo
    }

    public DocumentType getDocumentType() { // Restituisce il tipo di documento
        return documentType; // Ritorna il tipo associato
    }

    public Long getDocumentId() { // Restituisce l'ID del documento
        return documentId; // Ritorna l'identificativo del documento
    }

    public DocumentAction getAction() { // Restituisce l'azione eseguita
        return action; // Ritorna l'azione registrata
    }

    public String getDescription() { // Restituisce la descrizione dell'evento
        return description; // Ritorna il testo descrittivo
    }

    public Instant getCreatedAt() { // Restituisce il momento della creazione
        return createdAt; // Ritorna il timestamp registrato
    }

    @Override // Ridefinisce equals per confrontare le entità
    public boolean equals(Object o) { // Confronta questo oggetto con un altro
        if (this == o) return true; // Se è lo stesso riferimento sono uguali
        if (!(o instanceof DocumentHistory that)) return false; // Se l'altro non è DocumentHistory restituisce false
        return Objects.equals(id, that.id); // Due record sono uguali se hanno lo stesso ID
    }

    @Override // Ridefinisce hashCode in coerenza con equals
    public int hashCode() { // Calcola l'hash dell'entità
        return Objects.hash(id); // Usa l'ID come base per l'hash
    }
}
