package com.example.client.auth;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * TokenProvider di sviluppo che bypassa MSAL restituendo un token fittizio
 * quando l'ambiente espone la variabile MSAL_DEV_BYPASS_SECRET.
 */
public final class DevBypassTokenProvider implements TokenProvider {

    private final MsalAuthenticationResult result;

    private DevBypassTokenProvider(MsalAuthenticationResult result) {
        this.result = Objects.requireNonNull(result, "result must not be null");
    }

    public static TokenProvider fromEnvironment() {
        String secret = System.getenv("MSAL_DEV_BYPASS_SECRET");
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("MSAL_DEV_BYPASS_SECRET mancante");
        }

        String azureId = Optional.ofNullable(System.getenv("MSAL_DEV_BYPASS_AZURE_ID"))
                .filter(id -> !id.isBlank())
                .orElse("dev-azure-id");
        String email = Optional.ofNullable(System.getenv("MSAL_DEV_BYPASS_EMAIL"))
                .filter(id -> !id.isBlank())
                .orElse("developer@example.com");
        String displayName = Optional.ofNullable(System.getenv("MSAL_DEV_BYPASS_DISPLAY_NAME"))
                .filter(id -> !id.isBlank())
                .orElse("Dev Bypass User");

        Instant expires = Instant.now().plusSeconds(3600);
        MsalAccount account = new MsalAccount(email, displayName, azureId, "dev-tenant", azureId);
        MsalAuthenticationResult result = new MsalAuthenticationResult(secret, null, expires, "dev-bypass", account);

        return new DevBypassTokenProvider(result);
    }

    @Override
    public Optional<MsalAuthenticationResult> acquireTokenSilently() throws MsalAuthenticationException {
        return Optional.of(result);
    }

    @Override
    public MsalAuthenticationResult acquireTokenInteractive() throws MsalAuthenticationException {
        return result;
    }
}
