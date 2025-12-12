package com.example.client.service;

/**
 * Risolve l'URL base del backend leggendo la variabile d'ambiente
 * {@code BACKEND_BASE_URL}. Se non è valorizzata viene utilizzato il
 * valore di default locale.
 */
final class BaseUrlResolver {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private BaseUrlResolver() {
        // Utility class
    }

    static String resolve() {
        String env = System.getenv("BACKEND_BASE_URL");
        if (env == null || env.isBlank()) {
            return DEFAULT_BASE_URL;
        }

        String sanitized = env.trim();
        while (sanitized.endsWith("/")) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }
        return sanitized.isBlank() ? DEFAULT_BASE_URL : sanitized;
    }
}
