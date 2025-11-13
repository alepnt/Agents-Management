package com.example.server.dto;

import java.time.Instant;

public record NotificationSubscriptionResponse(Long id,
                                               Long userId,
                                               String channel,
                                               Instant createdAt) {
}
