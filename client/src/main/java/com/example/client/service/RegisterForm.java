package com.example.client.service;

/**
 * Record che rappresenta i dati necessari per registrare un nuovo utente
 * attraverso il client.
 *
 * Scopo:
 * - Incapsulare tutti i campi richiesti dall'endpoint /api/auth/register
 * - Fornire un modello immutabile, semplice da creare e serializzare
 * - Essere passato direttamente all'AuthApiClient senza conversioni aggiuntive
 *
 * Campi principali:
 * - azureId: identificativo esterno dell'utente (es. Microsoft Identity)
 * - email: indirizzo email dell’utente
 * - displayName: nome visualizzato nell'app
 * - agentCode: codice agente (opzionale, dipende dal ruolo)
 * - password: password in chiaro da inviare al backend (che la hasha)
 * - teamName: nome del team (opzionale)
 * - roleName: nome del ruolo da associare all'utente
 *
 * Il record è immutabile → perfetto per una request REST.
 */
public record RegisterForm(
                String azureId,
                String email,
                String displayName,
                String agentCode,
                String password,
                String teamName,
                String roleName) {
}
