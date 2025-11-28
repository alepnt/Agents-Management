package com.example.common.dto;                                   // Package contenente tutti i DTO condivisi.

import java.time.Instant;                   // Enum che rappresenta il tipo di azione registrata (CREATE, UPDATE, ecc.).
import java.util.Objects;                     // Enum che rappresenta il tipo di documento (CONTRACT, INVOICE, ecc.).

import com.example.common.enums.DocumentAction;                                         // Timestamp UTC utilizzato per tracciare la data dell’evento.
import com.example.common.enums.DocumentType;                                         // Utility per equals() e hashCode.

/**
 * DTO per trasferire le informazioni dello storico dei documenti.
 * Contiene metadati su tipo documento, azione eseguita, descrizione ed istante di registrazione.
 */
public class DocumentHistoryDTO {                                 // DTO mutabile per la rappresentazione dello storico documentale.

    private Long id;                                              // Identificativo univoco della voce di storico.
    private DocumentType documentType;                            // Tipo di documento interessato (CONTRACT, INVOICE, ecc.).
    private Long documentId;                                      // Identificativo del documento collegato.
    private DocumentAction action;                                // Azione eseguita (CREATED, UPDATED, DELETED, ecc.).
    private String description;                                   // Descrizione testuale dell’evento.
    private Instant createdAt;                                    // Timestamp della registrazione dell’evento.

    public DocumentHistoryDTO() {                                 // Costruttore vuoto richiesto per la serializzazione/deserializzazione.
    }

    public DocumentHistoryDTO(Long id,                            // Costruttore completo per inizializzazione diretta.
                              DocumentType documentType,
                              Long documentId,
                              DocumentAction action,
                              String description,
                              Instant createdAt) {
        this.id = id;
        this.documentType = documentType;
        this.documentId = documentId;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {                                         // Restituisce l’ID dello storico.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID dello storico.
        this.id = id;
    }

    public DocumentType getDocumentType() {                       // Restituisce il tipo di documento.
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {      // Imposta il tipo di documento.
        this.documentType = documentType;
    }

    public Long getDocumentId() {                                 // Restituisce l’ID del documento associato.
        return documentId;
    }

    public void setDocumentId(Long documentId) {                  // Imposta l’ID del documento associato.
        this.documentId = documentId;
    }

    public DocumentAction getAction() {                           // Restituisce l’azione registrata.
        return action;
    }

    public void setAction(DocumentAction action) {                // Imposta l’azione registrata.
        this.action = action;
    }

    public String getDescription() {                              // Restituisce la descrizione testuale dell’evento.
        return description;
    }

    public void setDescription(String description) {              // Imposta la descrizione testuale dell’evento.
        this.description = description;
    }

    public Instant getCreatedAt() {                               // Restituisce il timestamp di creazione della voce di storico.
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {                 // Imposta il timestamp di creazione della voce di storico.
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due DocumentHistoryDTO basandosi sull’ID.
        if (this == o) {                                          // Se stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Null o classe diversa → non uguali.
            return false;
        }
        DocumentHistoryDTO that = (DocumentHistoryDTO) o;         // Cast dell’oggetto dopo controllo classe.
        return Objects.equals(id, that.id);                       // Confronto basato sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe DocumentHistoryDTO.
