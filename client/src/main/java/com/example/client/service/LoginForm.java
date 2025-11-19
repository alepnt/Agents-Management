package com.example.client.service;

public record LoginForm(
        String accessToken,
        String email,
        String displayName,
        String azureId,
        String authority,
        String refreshToken
) {
    public LoginForm(String accessToken, String email, String displayName, String azureId) {
        this(accessToken, email, displayName, azureId, null, null);
    }
}
