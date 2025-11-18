package com.example.client.service;

public class BackendCommunicationException extends RuntimeException {

    public BackendCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
