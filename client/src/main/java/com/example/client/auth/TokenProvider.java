package com.example.client.auth;

import java.util.Optional;

/**
 * Abstraction used by la UI per richiedere token di accesso Microsoft.
 */
public interface TokenProvider {

    /**
     * Tenta di ottenere un token MSAL senza interazione con l'utente.
     *
     * @return l'eventuale token valido già presente in cache
     * @throws MsalAuthenticationException quando MSAL restituisce un errore non recuperabile
     */
    Optional<MsalAuthenticationResult> acquireTokenSilently() throws MsalAuthenticationException;

    /**
     * Avvia il flusso interattivo MSAL.
     *
     * @return il risultato dell'autenticazione appena completata
     * @throws MsalAuthenticationException quando l'autenticazione fallisce
     */
    MsalAuthenticationResult acquireTokenInteractive() throws MsalAuthenticationException;
}
