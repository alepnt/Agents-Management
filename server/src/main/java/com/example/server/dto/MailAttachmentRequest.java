package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;

public record MailAttachmentRequest(@NotBlank String filename,
                                    @NotBlank String contentType,
                                    @NotBlank String base64Data) {
}
