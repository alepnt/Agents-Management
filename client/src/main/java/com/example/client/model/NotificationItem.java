package com.example.client.model;

import java.time.Instant;

public record NotificationItem(Long id,
                               Long userId,
                               Long teamId,
                               String title,
                               String message,
                               boolean read,
                               Instant createdAt) {
}
