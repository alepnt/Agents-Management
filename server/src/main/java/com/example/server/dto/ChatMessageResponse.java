package com.example.server.dto;

import java.time.Instant;

public record ChatMessageResponse(Long id,
                                  String conversationId,
                                  Long senderId,
                                  Long teamId,
                                  String body,
                                  Instant createdAt) {
}
