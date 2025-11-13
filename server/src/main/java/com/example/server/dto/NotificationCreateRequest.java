package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationCreateRequest(Long userId,
                                        Long teamId,
                                        @NotBlank String title,
                                        @NotBlank String message) {
}
