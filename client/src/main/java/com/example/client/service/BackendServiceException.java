package com.example.client.service;

public class BackendServiceException extends RuntimeException {

    private final int statusCode;

    public BackendServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
