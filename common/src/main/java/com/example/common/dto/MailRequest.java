package com.example.common.dto;                                   // Package dei DTO condivisi tra client e server.

import java.util.List;                       // Garantisce che la stringa sia un indirizzo email valido.

import jakarta.validation.constraints.Email;                    // Campo obbligatorio: non null e non vuoto.
import jakarta.validation.constraints.NotBlank;                    // Lista obbligatoria: non vuota.
import jakarta.validation.constraints.NotEmpty;                                            // Usato per rappresentare elenchi di destinatari e allegati.

/**
 * DTO che rappresenta una richiesta di invio email.
 * Include destinatari, contenuto, intestazioni e allegati.
 */
public record MailRequest(                                        // Record immutabile per modellare una richiesta di invio email.
        @NotBlank String subject,                                  // Oggetto dell’email; obbligatorio.
        @NotBlank String body,                                     // Corpo del messaggio; obbligatorio.
        @NotEmpty List<@Email String> to,                          // Lista dei destinatari principali; deve contenere almeno un elemento valido.
        List<@Email String> cc,                                    // Destinatari in copia; opzionali.
        List<@Email String> bcc,                                   // Destinatari in copia nascosta; opzionali.
        List<MailAttachmentDTO> attachments                        // Elenco degli allegati email; opzionale.
) {
}                                                                  // Fine del record MailRequest.
