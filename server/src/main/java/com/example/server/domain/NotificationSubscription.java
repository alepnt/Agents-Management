package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Objects;

@Table("notification_subscriptions")
public class NotificationSubscription {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    private String channel;

    @Column("created_at")
    private Instant createdAt;

    public NotificationSubscription(Long id, Long userId, String channel, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public static NotificationSubscription create(Long userId, String channel, Instant createdAt) {
        return new NotificationSubscription(null, userId, channel, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getChannel() {
        return channel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public NotificationSubscription withId(Long id) {
        return new NotificationSubscription(id, userId, channel, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationSubscription that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
