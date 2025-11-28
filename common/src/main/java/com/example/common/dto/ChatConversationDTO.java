package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.time.Instant;                                         // Timestamp utilizzato per indicare l’ultima attività della conversazione.

/**
 * DTO che rappresenta una conversazione di chat.
 * Contiene metadati utili per mostrare l’elenco conversazioni all’utente.
 */
public record ChatConversationDTO(                                // Record immutabile per una conversazione di chat.
        String conversationId,                                     // Identificatore univoco della conversazione.
        String title,                                              // Titolo o nome associato alla conversazione (es. interlocutore).
        Instant lastActivity,                                      // Timestamp dell’ultima attività o messaggio.
        String lastMessagePreview                                  // Anteprima dell’ultimo messaggio inviato o ricevuto.
) {
}                                                                  // Fine del record ChatConversationDTO.
