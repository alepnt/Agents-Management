package com.example.client.auth;

import java.time.Instant;

public record MsalAuthenticationResult(
        String accessToken,
        String refreshToken,
        Instant expiresOn,
        String authority,
        MsalAccount account
) {
    public boolean isExpired() {
        return expiresOn != null && expiresOn.isBefore(Instant.now());
    }
}
