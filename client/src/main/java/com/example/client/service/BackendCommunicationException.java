package com.example.client.service;

public class BackendCommunicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BackendCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
