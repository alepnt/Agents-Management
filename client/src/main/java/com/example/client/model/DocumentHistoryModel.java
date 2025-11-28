package com.example.client.model;
// Package dei modelli JavaFX utilizzati dal client.

/**
 * Modello JavaFX per rappresentare una voce di storico documentale.
 * Utilizzato per visualizzare lo storico di operazioni su documenti
 * come fatture, contratti, notifiche interne, ecc.
 */

import com.example.common.dto.DocumentHistoryDTO;
// DTO condiviso con il backend che rappresenta la singola entry dello storico.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import delle JavaFX properties per consentire binding dinamico nella UI.

import java.time.Instant;
// Timestamp preciso, utilizzato per mostrare l'orario dell'azione.

/**
 * Modello JavaFX che rappresenta una riga dello storico delle modifiche/azioni
 * compiute sui documenti nel sistema.
 */
public class DocumentHistoryModel {

    // Tipo di documento (INVOICE, CONTRACT, CUSTOMER, ecc.) rappresentato come
    // testo.
    private final StringProperty documentType = new SimpleStringProperty();

    // Identificativo del documento coinvolto nell'azione.
    private final StringProperty documentId = new SimpleStringProperty();

    // Tipo di azione eseguita (CREATED, UPDATED, DELETED, ecc.).
    private final StringProperty action = new SimpleStringProperty();

    // Descrizione estesa dell’evento (es. "Aggiunta nuova riga", "Stato cambiato",
    // ecc.).
    private final StringProperty description = new SimpleStringProperty();

    // Timestamp che indica quando è avvenuta l'azione.
    private final ObjectProperty<Instant> createdAt = new SimpleObjectProperty<>();

    /**
     * Converte un DocumentHistoryDTO in un DocumentHistoryModel JavaFX.
     * Mapping DTO → Model.
     */
    public static DocumentHistoryModel fromDto(DocumentHistoryDTO dto) {
        DocumentHistoryModel model = new DocumentHistoryModel();

        // documentType: se presente enum → stringa, altrimenti stringa vuota.
        model.setDocumentType(
                dto.getDocumentType() != null ? dto.getDocumentType().name() : "");

        // documentId: se presente Long → convertito in stringa, altrimenti stringa
        // vuota.
        model.setDocumentId(
                dto.getDocumentId() != null ? dto.getDocumentId().toString() : "");

        // action: enum sempre presente → nome in stringa.
        model.setAction(dto.getAction().name());

        // Descrizione così come fornita dal backend.
        model.setDescription(dto.getDescription());

        // Timestamp dell'operazione.
        model.setCreatedAt(dto.getCreatedAt());

        return model;
    }

    // ===========================
    // GETTER / SETTER
    // + JavaFX Properties
    // ===========================

    public String getDocumentType() {
        return documentType.get();
    }

    public void setDocumentType(String value) {
        documentType.set(value);
    }

    public StringProperty documentTypeProperty() {
        return documentType;
    }

    public String getDocumentId() {
        return documentId.get();
    }

    public void setDocumentId(String value) {
        documentId.set(value);
    }

    public StringProperty documentIdProperty() {
        return documentId;
    }

    public String getAction() {
        return action.get();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public StringProperty actionProperty() {
        return action;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<Instant> createdAtProperty() {
        return createdAt;
    }
}
