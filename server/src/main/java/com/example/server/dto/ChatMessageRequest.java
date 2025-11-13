package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(@NotNull Long senderId,
                                 @NotBlank String conversationId,
                                 @NotBlank String body) {
}
