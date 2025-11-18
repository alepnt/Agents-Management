package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Objects;

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
        this.title = title;
        this.message = message;
        this.read = read;
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

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
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
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
