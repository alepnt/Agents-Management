package com.example.client.service;

// Eccezione personalizzata per rappresentare errori provenienti dal backend.
// Usata quando il server risponde con HTTP 4xx o 5xx e si vuole fornire al
// livello superiore (controller/view) un oggetto semplice da gestire, con
// un messaggio leggibile e lo status code di riferimento.

/**
 * Eccezione unchecked che rappresenta una risposta di errore del backend.
 *
 * Contiene:
 * - uno statusCode HTTP (es. 400, 404, 409, 500)
 * - un messaggio leggibile dall’utente o dal controller
 *
 * È distinta da BackendCommunicationException, che invece rappresenta
 * problemi di rete o I/O.
 */
public class BackendServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    // Fornisce compatibilità nella serializzazione dell’eccezione.

    private final int statusCode;
    // Codice HTTP restituito dal backend (es. 400, 404, 500).

    /**
     * Costruisce una nuova eccezione con uno status code e un messaggio.
     *
     * @param statusCode codice HTTP restituito dal backend
     * @param message    messaggio descrittivo dell'errore
     */
    public BackendServiceException(int statusCode, String message) {
        super(message); // Passa il messaggio alla RuntimeException
        this.statusCode = statusCode;
    }

    /**
     * Restituisce lo status HTTP che ha generato l’errore.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
