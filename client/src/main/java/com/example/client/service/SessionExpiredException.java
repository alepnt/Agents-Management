package com.example.client.service;

/**
 * Eccezione custom che indica che la sessione utente non è più valida.
 *
 * Questa eccezione viene utilizzata quando:
 * - il token JWT è scaduto
 * - il backend risponde con HTTP 401 o 403
 * - lo SessionStore rileva una sessione senza token valido
 *
 * È una RuntimeException → non richiede dichiarazione di throws,
 * permettendo di interrompere velocemente il flusso e reindirizzare
 * l’utente alla schermata di login.
 *
 * Tipicamente viene gestita dal livello UI per:
 * - mostrare un messaggio user-friendly
 * - riportare l’utente al login
 */
public class SessionExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    // Identificatore di serializzazione standard.

    /**
     * Costruisce l’eccezione con il solo messaggio descrittivo.
     *
     * @param message testo che descrive la causa (es. "Token scaduto")
     */
    public SessionExpiredException(String message) {
        super(message);
    }
}
