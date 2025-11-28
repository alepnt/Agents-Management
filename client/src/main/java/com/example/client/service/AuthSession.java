package com.example.client.service;
// Package dei servizi lato client, include modelli di sessione e API client.

import java.time.Instant;
// Timestamp usato per verificare scadenza della sessione.

/**
 * Rappresenta una sessione di autenticazione sul client.
 *
 * Contiene:
 * - accessToken → token JWT rilasciato dal backend
 * - tokenType → di solito "Bearer"
 * - expiresAt → istante di scadenza del token (Instant)
 * - user → informazioni utente (UserSummary)
 * - authority → ruolo/autorità (opzionale)
 * - refreshToken → token di refresh (opzionale)
 *
 * È un record → immutabile, compatto e serializzabile facilmente.
 */
public record AuthSession(
        String accessToken, // Token JWT ottenuto dal backend
        String tokenType, // Tipicamente "Bearer"
        Instant expiresAt, // Momento di scadenza del token
        UserSummary user, // Dati utente autenticato
        String authority, // Ruolo/autorizzazioni (opzionale)
        String refreshToken // Token per refresh sessione (opzionale)
) {

    /**
     * Costruttore secondario usato quando non si gestiscono
     * authority e refreshToken.
     *
     * Permette al codice esistente di continuare a funzionare
     * senza richiedere modifiche.
     */
    public AuthSession(String accessToken, String tokenType, Instant expiresAt, UserSummary user) {
        this(accessToken, tokenType, expiresAt, user, null, null);
    }

    /**
     * Indica se la sessione è scaduta rispetto all'ora attuale.
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}
