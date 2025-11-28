package com.example.common.dto;                                   // Package che contiene i DTO condivisi dell’applicazione.

import jakarta.validation.constraints.NotBlank;                   // Valida che il campo non sia null e non vuoto.
import jakarta.validation.constraints.NotNull;                    // Valida che il campo non possa essere null.

/**
 * Richiesta per l'invio di un messaggio in chat.
 * Utilizzato dal client per trasmettere al server i dati necessari alla creazione del messaggio.
 */
public record ChatMessageRequest(                                 // Record immutabile che rappresenta la richiesta di invio messaggio.
        @NotNull Long senderId,                                    // ID del mittente; obbligatorio.
        @NotBlank String conversationId,                           // ID della conversazione; non può essere null o vuoto.
        @NotBlank String body                                      // Contenuto testuale del messaggio; deve essere valorizzato.
) {
}                                                                  // Fine del record ChatMessageRequest.
