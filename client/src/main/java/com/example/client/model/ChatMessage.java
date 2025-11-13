package com.example.client.model;

import java.time.Instant;

public record ChatMessage(Long id,
                          String conversationId,
                          Long senderId,
                          Long teamId,
                          String body,
                          Instant createdAt) {
}
