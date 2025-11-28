package com.example.client.auth; // Package che contiene le entità e utilità legate all’autenticazione MSAL.

import java.time.Instant; // Tipo per rappresentare timestamp di scadenza dei token.

/**
 * Risultato dell’autenticazione MSAL lato client.
 * Contiene token, informazioni sull’account e dati di scadenza.
 */
public record MsalAuthenticationResult( // Record immutabile che rappresenta un esito completo di login.
        String accessToken, // Access token OAuth2 restituito da Azure AD.
        String refreshToken, // Refresh token utilizzabile per rigenerare l’access token.
        Instant expiresOn, // Timestamp di scadenza dell’access token.
        String authority, // Endpoint di autorizzazione utilizzato per il login.
        MsalAccount account // Account autenticato associato al token.
) {

    /**
     * Indica se il token è già scaduto rispetto all’orologio locale.
     *
     * @return true se expiresOn è valorizzato e antecedente al momento corrente.
     */
    public boolean isExpired() { // Metodo helper per verificare la validità del token.
        return expiresOn != null && expiresOn.isBefore(Instant.now());
    }
} // Fine del record MsalAuthenticationResult.
