package com.example.common.dto;

import java.time.Instant;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class NotificationSubscriptionDTO {

    private Long id;
    private Long userId;
    private @NonNull String channel;
    private @Nullable Instant createdAt;

    public NotificationSubscriptionDTO() {
        this.channel = "";
        this.createdAt = null;
    }

    public NotificationSubscriptionDTO(Long id, Long userId, String channel, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.channel = Objects.requireNonNull(channel, "channel");
        this.createdAt = createdAt;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@Nullable Long userId) {
        this.userId = userId;
    }

    @NonNull
    public String getChannel() {
        return channel;
    }

    public void setChannel(@NonNull String channel) {
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    @Nullable
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Instant createdAt) {
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
