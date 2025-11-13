package com.example.client.model;

public record NotificationCreate(Long userId,
                                 Long teamId,
                                 String title,
                                 String message) {
}
