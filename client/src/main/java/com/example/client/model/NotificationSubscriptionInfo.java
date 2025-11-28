package com.example.client.model;
// Package dei modelli lato client, include anche oggetti record per notifiche.

/**
 * Record che rappresenta una sottoscrizione registrata nel sistema.
 * 
 * Diversamente da NotificationSubscription (che è una richiesta di iscrizione),
 * questo record rappresenta un oggetto persistito lato server e restituito
 * al client per visualizzare o gestire le sottoscrizioni attive.
 */

import java.time.Instant;
// Timestamp per indicare quando la sottoscrizione è stata creata.

public record NotificationSubscriptionInfo(
        Long id, // Identificativo univoco della sottoscrizione registrata nel database
        Long userId, // Utente che ha effettuato la sottoscrizione
        String channel, // Canale a cui l’utente è iscritto (es. "user-15", "team-3", "global")
        Instant createdAt // Data e ora in cui la sottoscrizione è stata creata
) {
    // Il record fornisce automaticamente:
    // - costruttore
    // - accessor (id(), userId(), channel(), createdAt())
    // - equals / hashCode
    // - toString
}
