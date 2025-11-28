package com.example.common.dto;                                   // Package che contiene i DTO condivisi legati alle funzionalità comuni.

import jakarta.validation.constraints.NotBlank;                   // Validazioni che garantiscono che i campi non siano null né vuoti.

/**
 * DTO che rappresenta un allegato email.
 * Contiene metadati e contenuto codificato in base64.
 */
public record MailAttachmentDTO(                                   // Record immutabile per rappresentare allegati email.
        @NotBlank String filename,                                 // Nome del file allegato; richiesto e non vuoto.
        @NotBlank String contentType,                              // MIME type dell’allegato (es. application/pdf).
        @NotBlank String base64Data                                // Contenuto binario dell’allegato in formato Base64.
) {
}                                                                  // Fine del record MailAttachmentDTO.
