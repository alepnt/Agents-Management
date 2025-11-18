package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table("notifications")
public class Notification {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("team_id")
    private Long teamId;

    private String title;

    private String message;

    @Column("is_read")
    private Boolean read;

    @Column("created_at")
    private Instant createdAt;

    public Notification(Long id,
                        Long userId,
                        Long teamId,
                        String title,
                        String message,
                        Boolean read,
                        Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static Notification forUser(Long userId, String title, String message, Instant createdAt) {
        return new Notification(null, userId, null, title, message, Boolean.FALSE, createdAt);
    }

    public static Notification forTeam(Long teamId, String title, String message, Instant createdAt) {
        return new Notification(null, null, teamId, title, message, Boolean.FALSE, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTitle() {
        return title;
    }

    public Notification withTitle(String title) {
        return new Notification(id, userId, teamId, title, message, read, createdAt);
    }

    public String getMessage() {
        return message;
    }

    public Notification withMessage(String message) {
        return new Notification(id, userId, teamId, title, message, read, createdAt);
    }

    public Boolean isRead() {
        return Boolean.TRUE.equals(read);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Notification markRead() {
        return new Notification(id, userId, teamId, title, message, Boolean.TRUE, createdAt);
    }

    public Notification withRead(Boolean read) {
        return new Notification(id, userId, teamId, title, message, read, createdAt);
    }

    public Notification withId(Long id) {
        return new Notification(id, userId, teamId, title, message, read, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
