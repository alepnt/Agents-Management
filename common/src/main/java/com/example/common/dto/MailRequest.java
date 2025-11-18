package com.example.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MailRequest(@NotBlank String subject,
                          @NotBlank String body,
                          @NotEmpty List<@Email String> to,
                          List<@Email String> cc,
                          List<@Email String> bcc,
                          List<MailAttachmentDTO> attachments) {
}
