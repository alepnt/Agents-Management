package com.example.server.dto; // Package grouping DTOs exchanged with the server API

import jakarta.validation.constraints.NotBlank; // Constraint ensuring text fields are not empty or whitespace

public record LoginRequest( // Record representing the login payload received from the client
        @NotBlank String accessToken, // Access token issued by Azure AD that authenticates the client
        @NotBlank String email, // User email extracted from the identity provider
        @NotBlank String displayName, // Full name shown in the application UI
        @NotBlank String azureId // Unique identifier of the user within Azure AD
) { // Start of the LoginRequest record body (empty because record generates accessors automatically)
} // End of LoginRequest definition
