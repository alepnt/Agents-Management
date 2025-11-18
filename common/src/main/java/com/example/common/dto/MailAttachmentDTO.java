package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

public record MailAttachmentDTO(@NotBlank String filename,
                                @NotBlank String contentType,
                                @NotBlank String base64Data) {
}
