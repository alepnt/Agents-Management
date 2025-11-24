package com.example.server.dto; // Package containing data transfer objects for the server layer

import java.time.Instant; // Import Instant to represent token expiration timestamps

public record AuthResponse( // Record representing the payload returned after successful authentication
        String accessToken, // JWT string granting access to secured resources
        String tokenType, // Label describing how the token should be used in authorization headers
        Instant expiresAt, // Exact time at which the provided token will no longer be valid
        UserSummary user // Basic information about the authenticated user included in the response
) { // Start of the AuthResponse record body (empty because records provide boilerplate)
} // End of AuthResponse definition
