package com.example.client.model;

import com.example.common.dto.MessageDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;

/**
 * Modello JavaFX per la gestione dei messaggi.
 */
public class MessageModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty conversationId = new SimpleStringProperty();
    private final ObjectProperty<Long> senderId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> teamId = new SimpleObjectProperty<>();
    private final StringProperty body = new SimpleStringProperty();
    private final ObjectProperty<Instant> createdAt = new SimpleObjectProperty<>();

    public static MessageModel fromDto(MessageDTO dto) {
        MessageModel model = new MessageModel();
        model.setId(dto.getId());
        model.setConversationId(dto.getConversationId());
        model.setSenderId(dto.getSenderId());
        model.setTeamId(dto.getTeamId());
        model.setBody(dto.getBody());
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }

    public MessageDTO toDto() {
        return new MessageDTO(getId(), getConversationId(), getSenderId(), getTeamId(), getBody(), getCreatedAt());
    }

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
