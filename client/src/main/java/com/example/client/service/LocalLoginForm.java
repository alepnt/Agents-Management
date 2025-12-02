package com.example.client.service;

/**
 * Modello dati per l'autenticazione locale tramite codice agente
 * e password memorizzata nel portale.
 */
public record LocalLoginForm(
        String agentCode,
        String password
) {
}
