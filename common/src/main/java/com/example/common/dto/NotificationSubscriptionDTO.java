package com.example.common.dto;

import java.time.Instant;
import java.util.Objects;

public class NotificationSubscriptionDTO {

    private Long id;
    private Long userId;
    private String channel;
    private Instant createdAt;

    public NotificationSubscriptionDTO() {
    }

    public NotificationSubscriptionDTO(Long id, Long userId, String channel, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationSubscriptionDTO that = (NotificationSubscriptionDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
