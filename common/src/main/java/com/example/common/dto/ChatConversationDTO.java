package com.example.common.dto;

import java.time.Instant;

public record ChatConversationDTO(String conversationId,
                                  String title,
                                  Instant lastActivity,
                                  String lastMessagePreview) {
}
