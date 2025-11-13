package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table("messages")
public class Message {

    @Id
    private Long id;

    @Column("conversation_id")
    private String conversationId;

    @Column("sender_id")
    private Long senderId;

    @Column("team_id")
    private Long teamId;

    private String body;

    @Column("created_at")
    private Instant createdAt;

    public Message(Long id,
                   String conversationId,
                   Long senderId,
                   Long teamId,
                   String body,
                   Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.teamId = teamId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public static Message create(String conversationId, Long senderId, Long teamId, String body, Instant createdAt) {
        return new Message(null, conversationId, senderId, teamId, body, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Message withId(Long id) {
        return new Message(id, conversationId, senderId, teamId, body, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
