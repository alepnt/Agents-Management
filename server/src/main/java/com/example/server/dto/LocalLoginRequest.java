package com.example.server.dto; // Package grouping authentication DTOs

import jakarta.validation.constraints.NotBlank; // Validates that text fields are provided
import jakarta.validation.constraints.Size; // Enforces minimum/maximum length for strings

public record LocalLoginRequest( // Record representing the payload for local (non-Microsoft) login
        @NotBlank @Size(min = 6, max = 64) String agentCode, // Agent code used as username
        @NotBlank @Size(min = 8, max = 128) String password // Local password chosen by the agent
) {
} // End of LocalLoginRequest definition
