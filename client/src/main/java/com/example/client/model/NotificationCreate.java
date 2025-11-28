package com.example.client.model;
// Package dei modelli lato client, include anche DTO leggeri/di supporto.

/**
 * Record Java che rappresenta i dati necessari alla creazione di una notifica.
 * Questo tipo è tipicamente usato quando il client invia una richiesta
 * al server per creare una nuova notifica per:
 * - un singolo utente (userId)
 * - un intero team (teamId)
 *
 * title : titolo breve della notifica
 * message: contenuto della notifica
 *
 * I record sono immutabili, perfetti per request DTO nel client.
 */
public record NotificationCreate(
        Long userId, // Identificativo dell'utente destinatario (opzionale se si notifica un team)
        Long teamId, // Identificativo del team destinatario (opzionale se si notifica un singolo
                     // utente)
        String title, // Titolo breve della notifica
        String message // Corpo testuale della notifica
) {
    // Nessun corpo aggiuntivo: i record generano automaticamente constructor,
    // equals, hashCode, toString e accedenti (userId(), teamId(), etc.).
}
