package com.example.server.dto; // Package containing DTOs for communication between client and server

import jakarta.validation.constraints.NotBlank; // Ensures string fields are provided with non-empty content
import jakarta.validation.constraints.NotNull; // Ensures numeric identifiers are not null when received

public record NotificationSubscribeRequest( // Record describing a subscription request to notifications
        @NotNull Long userId, // Identifier of the user who wants to subscribe
        @NotBlank String channel // Specific channel or topic the user wishes to follow
) { // Start of the NotificationSubscribeRequest record body (left empty because records are self-contained)
} // End of NotificationSubscribeRequest definition
