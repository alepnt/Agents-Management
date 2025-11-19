package com.example.client.auth;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record MsalConfiguration(
        String clientId,
        String authority,
        URI redirectUri,
        Set<String> scopes
) {
    public MsalConfiguration {
        Objects.requireNonNull(clientId, "clientId");
        Objects.requireNonNull(authority, "authority");
        Objects.requireNonNull(redirectUri, "redirectUri");
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("Almeno uno scope MSAL è richiesto");
        }
    }

    public static MsalConfiguration fromEnvironment() {
        String clientId = firstNonBlank(System.getProperty("msal.clientId"), System.getenv("MSAL_CLIENT_ID"));
        String authority = firstNonBlank(System.getProperty("msal.authority"), System.getenv("MSAL_AUTHORITY"),
                "https://login.microsoftonline.com/common");
        String redirect = firstNonBlank(System.getProperty("msal.redirectUri"), System.getenv("MSAL_REDIRECT_URI"));
        String scopes = firstNonBlank(System.getProperty("msal.scopes"), System.getenv("MSAL_SCOPES"),
                "https://graph.microsoft.com/.default");

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("Variabile MSAL_CLIENT_ID non configurata");
        }
        if (redirect == null || redirect.isBlank()) {
            throw new IllegalStateException("Variabile MSAL_REDIRECT_URI non configurata");
        }
        Set<String> scopeSet = new LinkedHashSet<>(Arrays.asList(scopes.split(",")));
        scopeSet.removeIf(String::isBlank);
        if (scopeSet.isEmpty()) {
            throw new IllegalStateException("Nessuno scope valido configurato per MSAL");
        }
        return new MsalConfiguration(clientId.trim(), authority.trim(), URI.create(redirect.trim()), scopeSet);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
