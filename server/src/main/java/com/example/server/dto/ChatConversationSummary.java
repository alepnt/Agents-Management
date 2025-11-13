package com.example.server.dto;

import java.time.Instant;

public record ChatConversationSummary(String conversationId,
                                      String title,
                                      Instant lastActivity,
                                      String lastMessagePreview) {
}
