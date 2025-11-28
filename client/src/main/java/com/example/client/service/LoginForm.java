package com.example.client.service;

// LoginForm è un record che rappresenta i dati necessari per eseguire
// il login dal client verso il backend. Essendo un record, fornisce:
// - immutabilità
// - costruttore, equals, hashCode e toString generati automaticamente
// - un modello semplice e leggibile

/**
 * Modello dati utilizzato dal client per inviare informazioni di login
 * al backend o per rappresentare una sessione di accesso esterna.
 *
 * Campi:
 * - accessToken: token ottenuto tramite provider esterno (es. MSAL)
 * - email: email dell’utente
 * - displayName: nome visualizzato
 * - azureId: identificativo univoco dell’utente lato identity provider
 * - authority: ruolo/permessi (opzionale)
 * - refreshToken: eventuale token di refresh (opzionale)
 *
 * Il record semplifica la gestione di questi dati nel flusso Auth.
 */
public record LoginForm(
        String accessToken,
        String email,
        String displayName,
        String azureId,
        String authority,
        String refreshToken) {

    /**
     * Costruttore semplificato, pensato per i casi in cui non sia
     * necessario indicare authority e refreshToken (scenari comuni).
     *
     * Riduce il boilerplate e mantiene retrocompatibilità con codice esistente.
     */
    public LoginForm(String accessToken, String email, String displayName, String azureId) {
        this(accessToken, email, displayName, azureId, null, null);
    }
}
