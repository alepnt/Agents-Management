package com.example.client.model;

import java.time.Instant;

public record ChatConversation(String conversationId,
                               String title,
                               Instant lastActivity,
                               String lastMessagePreview) {
}
