package com.example.client.model;

public record ChatMessageSend(Long senderId,
                              String conversationId,
                              String body) {
}
