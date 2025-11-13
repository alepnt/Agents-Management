package com.example.client.model;

public record MailAttachment(String filename,
                             String contentType,
                             String base64Data) {
}
