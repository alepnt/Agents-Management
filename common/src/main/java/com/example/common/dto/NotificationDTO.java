package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class NotificationDTO {

    private Long id;
    private Long userId;
    private Long teamId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    private Boolean read;
    private Instant createdAt;

    public NotificationDTO() {
        this.title = "";
        this.message = "";
        this.read = Boolean.FALSE;
        this.createdAt = Instant.now();
    }

    public NotificationDTO(Long id,
                           Long userId,
                           Long teamId,
                           String title,
                           String message,
                           Boolean read,
                           Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.title = Objects.requireNonNull(title, "title");
        this.message = Objects.requireNonNull(message, "message");
        this.read = Objects.requireNonNullElse(read, Boolean.FALSE);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
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

    @Nullable
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(@Nullable Long teamId) {
        this.teamId = teamId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = Objects.requireNonNull(title, "title");
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = Objects.requireNonNull(message, "message");
    }

    @NonNull
    public Boolean getRead() {
        return read;
    }

    public void setRead(@Nullable Boolean read) {
        this.read = Objects.requireNonNullElse(read, Boolean.FALSE);
    }

    @NonNull
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Instant createdAt) {
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
