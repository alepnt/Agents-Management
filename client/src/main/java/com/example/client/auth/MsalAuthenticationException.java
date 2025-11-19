package com.example.client.auth;

/**
 * Eccezione applicativa per incapsulare gli errori provenienti da MSAL4J.
 */
public class MsalAuthenticationException extends Exception {
    public MsalAuthenticationException(String message) {
        super(message);
    }

    public MsalAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
