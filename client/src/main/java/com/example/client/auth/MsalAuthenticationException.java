package com.example.client.auth;

/**
 * Eccezione applicativa per incapsulare gli errori provenienti da MSAL4J.
 */
public class MsalAuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    public MsalAuthenticationException(String message) {
        super(message);
    }

    public MsalAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
