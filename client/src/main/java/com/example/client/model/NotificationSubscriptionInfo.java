package com.example.client.model;

import java.time.Instant;

public record NotificationSubscriptionInfo(Long id,
                                           Long userId,
                                           String channel,
                                           Instant createdAt) {
}
