package com.example.common.dto;

import java.time.Instant;

public record ChatMessageDTO(Long id,
                             String conversationId,
                             Long senderId,
                             Long teamId,
                             String body,
                             Instant createdAt) {
}
