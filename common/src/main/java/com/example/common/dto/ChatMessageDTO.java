package com.example.common.dto;                                   // Package che contiene i DTO condivisi dell’applicazione.

import java.time.Instant;                                         // Tipo temporale utilizzato per la data di creazione del messaggio.

/**
 * DTO che rappresenta un singolo messaggio di chat.
 * Contiene metadati essenziali per ordinamento, rendering e audit.
 */
public record ChatMessageDTO(                                     // Record immutabile che modella un messaggio di chat.
        Long id,                                                   // Identificatore univoco del messaggio.
        String conversationId,                                     // ID della conversazione alla quale il messaggio appartiene.
        Long senderId,                                             // ID dell'utente che ha inviato il messaggio.
        Long teamId,                                               // ID del team del mittente (utile per filtri e grouping).
        String body,                                               // Contenuto testuale del messaggio.
        Instant createdAt                                          // Timestamp UTC di creazione del messaggio.
) {
}                                                                  // Fine del record ChatMessageDTO.
