package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationSubscribeRequest(@NotNull Long userId,
                                           @NotBlank String channel) {
}
