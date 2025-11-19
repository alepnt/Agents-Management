package com.example.client.auth;

public record MsalAccount(
        String username,
        String displayName,
        String objectId,
        String tenantId,
        String homeAccountId
) {
}
