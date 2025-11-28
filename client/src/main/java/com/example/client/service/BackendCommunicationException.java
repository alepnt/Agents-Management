package com.example.client.service;
// Package dei servizi lato client, include eccezioni specifiche e logic API.

/**
 * Eccezione unchecked utilizzata per rappresentare errori
 * nella comunicazione con il backend.
 *
 * Viene lanciata quando:
 * - la chiamata HTTP fallisce
 * - Jackson non riesce a deserializzare la risposta
 * - si verificano problemi di rete, timeout o IO
 *
 * Essendo una RuntimeException:
 * - non necessita blocchi "throws" o try/catch obbligatori
 * - si propaga facilmente attraverso i livelli applicativi
 */
public class BackendCommunicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    // SerialVersionUID per compatibilità tra runtime in caso di serializzazione.

    /**
     * Costruttore principale.
     *
     * @param message descrizione dell’errore
     * @param cause   eccezione originaria (IOException, InterruptedException, ecc.)
     */
    public BackendCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
