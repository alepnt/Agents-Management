package com.example.client.service;

import java.time.Instant;

public record AuthSession(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        UserSummary user,
        String authority,
        String refreshToken
) {
    public AuthSession(String accessToken, String tokenType, Instant expiresAt, UserSummary user) {
        this(accessToken, tokenType, expiresAt, user, null, null);
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}
