package com.example.client.model;
// Package dei modelli JavaFX utilizzati lato client.

/**
 * Modello JavaFX per la gestione dei messaggi.
 * Incapsula MessageDTO e fornisce proprietà osservabili utili per la UI
 * (chat, conversazioni, notifiche in tempo reale, ecc.).
 */

import com.example.common.dto.MessageDTO;
// DTO condiviso che rappresenta un messaggio.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Proprietà JavaFX per il binding dinamico nella UI.

import java.time.Instant;
// Timestamp del messaggio.

/**
 * Modello JavaFX per rappresentare un messaggio in una conversazione.
 */
public class MessageModel {

    // Identificativo univoco del messaggio.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Identificativo della conversazione (stringa perché può essere UUID).
    private final StringProperty conversationId = new SimpleStringProperty();

    // Id mittente del messaggio (utente).
    private final ObjectProperty<Long> senderId = new SimpleObjectProperty<>();

    // Id del team destinatario (per messaggi broadcast).
    private final ObjectProperty<Long> teamId = new SimpleObjectProperty<>();

    // Corpo testuale del messaggio.
    private final StringProperty body = new SimpleStringProperty();

    // Timestamp di creazione.
    private final ObjectProperty<Instant> createdAt = new SimpleObjectProperty<>();

    /**
     * Converte un MessageDTO in MessageModel (DTO → JavaFX Model).
     */
    public static MessageModel fromDto(MessageDTO dto) {
        MessageModel model = new MessageModel();

        model.setId(dto.getId()); // ID messaggio.
        model.setConversationId(dto.getConversationId()); // ID conversazione.
        model.setSenderId(dto.getSenderId()); // Mittente.
        model.setTeamId(dto.getTeamId()); // Team destinatario.
        model.setBody(dto.getBody()); // Testo.
        model.setCreatedAt(dto.getCreatedAt()); // Timestamp.

        return model;
    }

    /**
     * Converte il modello JavaFX in un DTO (Model → DTO).
     */
    public MessageDTO toDto() {
        return new MessageDTO(
                getId(),
                getConversationId(),
                getSenderId(),
                getTeamId(),
                getBody(),
                getCreatedAt());
    }

    // ===========================
    // GETTER / SETTER
    // + Proprietà JavaFX
    // ===========================

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public String getConversationId() {
        return conversationId.get();
    }

    public void setConversationId(String conversationId) {
        this.conversationId.set(conversationId);
    }

    public StringProperty conversationIdProperty() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId.get();
    }

    public void setSenderId(Long senderId) {
        this.senderId.set(senderId);
    }

    public ObjectProperty<Long> senderIdProperty() {
        return senderId;
    }

    public Long getTeamId() {
        return teamId.get();
    }

    public void setTeamId(Long teamId) {
        this.teamId.set(teamId);
    }

    public ObjectProperty<Long> teamIdProperty() {
        return teamId;
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    public StringProperty bodyProperty() {
        return body;
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
