package com.example.client.model;

import java.util.List;

public record MailMessage(String subject,
                          String body,
                          List<String> to,
                          List<String> cc,
                          List<String> bcc,
                          List<MailAttachment> attachments) {
}
