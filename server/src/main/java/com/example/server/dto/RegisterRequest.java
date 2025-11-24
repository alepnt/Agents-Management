package com.example.server.dto; // Package defining data transfer objects exchanged with clients

import jakarta.validation.constraints.Email; // Validates that the provided string is in email format
import jakarta.validation.constraints.NotBlank; // Requires text fields to contain non-empty values
import jakarta.validation.constraints.Size; // Enforces minimum and maximum length constraints on strings

public record RegisterRequest( // Record describing the payload for a new user registration
        @NotBlank String azureId, // Unique Azure AD identifier that ties the user to the identity provider
        @NotBlank @Email String email, // Email address associated with the new account
        @NotBlank String displayName, // Display name shown throughout the application
        @Size(min = 6, max = 64) String agentCode, // Code assigned to the agent with length restrictions
        @Size(min = 8, max = 128) String password, // Password selected by the user meeting security length requirements
        String teamName, // Optional name of the team the user should join
        String roleName // Optional role label requested for the user
) { // Start of the RegisterRequest record body (empty because records auto-generate boilerplate)
} // End of RegisterRequest definition
