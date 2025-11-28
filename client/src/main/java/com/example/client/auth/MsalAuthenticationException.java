package com.example.client.auth; // Package dedicato ai componenti di autenticazione lato client.

/**
 * Eccezione applicativa che incapsula gli errori generati da MSAL4J.
 * Utilizzata per distinguere gli errori di autenticazione Microsoft
 * da quelli generici di runtime.
 */
public class MsalAuthenticationException extends Exception { // Estende Exception → checked exception gestibile dal
                                                             // chiamante.

    private static final long serialVersionUID = 1L; // Versione di serializzazione per compatibilità in ambienti
                                                     // distribuiti.

    public MsalAuthenticationException(String message) { // Costruttore con solo messaggio.
        super(message);
    }

    public MsalAuthenticationException(String message, // Costruttore con messaggio e causa originale.
            Throwable cause) {
        super(message, cause);
    }
} // Fine della classe MsalAuthenticationException.
