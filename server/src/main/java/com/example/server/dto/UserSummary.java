package com.example.server.dto; // Package that holds lightweight data transfer objects

public record UserSummary( // Record providing a concise view of user details shared across responses
        Long id, // Unique identifier of the user in the application database
        String email, // Email address associated with the account
        String displayName, // User-friendly name displayed in the UI
        String azureId, // Azure AD identifier linking the account to the identity provider
        Long roleId, // Identifier of the user's role for authorization purposes
        Long teamId // Identifier of the team to which the user belongs
) { // Start of the UserSummary record body (empty because record supplies boilerplate)
} // End of UserSummary definition
