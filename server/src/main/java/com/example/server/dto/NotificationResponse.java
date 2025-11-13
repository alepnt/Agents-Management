package com.example.server.dto;

import java.time.Instant;

public record NotificationResponse(Long id,
                                   Long userId,
                                   Long teamId,
                                   String title,
                                   String message,
                                   boolean read,
                                   Instant createdAt) {
}
