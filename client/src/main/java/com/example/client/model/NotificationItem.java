package com.example.client.model;
// Package dei modelli lato client, include DTO, record e strutture per la UI.

import java.time.Instant;
// Timestamp dell’invio della notifica.

/**
 * Record che rappresenta una singola notifica visualizzabile nel client.
 * Si tratta del modello utilizzato per popolare liste, badge, popup o pannelli
 * delle notifiche dell'utente.
 *
 * Questo record è immutabile e perfetto per essere scambiato tra:
 * - chiamate API
 * - controller JavaFX
 * - caching locale delle notifiche
 */
public record NotificationItem(
        Long id, // Identificativo univoco della notifica generata dal server
        Long userId, // Utente destinatario (se la notifica è personale)
        Long teamId, // Team destinatario (se la notifica è di gruppo)
        String title, // Titolo breve da mostrare nella UI
        String message, // Testo principale della notifica
        boolean read, // Stato lettura: true se l’utente l’ha già letta
        Instant createdAt // Data e ora di creazione della notifica
) {
    // Nessun corpo aggiuntivo: i record generano automaticamente:
    // - constructor
    // - equals()
    // - hashCode()
    // - toString()
    // - accessor (id(), title(), read(), createdAt() ecc.)
}
