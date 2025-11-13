package com.example.server.service;

import com.example.server.dto.MailAttachmentRequest;
import com.example.server.dto.MailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailService {

    private static final URI GRAPH_ENDPOINT = URI.create("https://graph.microsoft.com/v1.0/me/sendMail");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public void sendMail(String delegatedToken, MailRequest request) {
        if (delegatedToken == null || delegatedToken.isBlank()) {
            throw new IllegalArgumentException("Token Microsoft Graph non presente");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", buildMessage(request));
        payload.put("saveToSentItems", Boolean.TRUE);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(GRAPH_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + delegatedToken)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Invio email fallito con stato " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Invio email interrotto", e);
        } catch (IOException e) {
            throw new IllegalStateException("Errore durante l'invio dell'email", e);
        }
    }

    private Map<String, Object> buildMessage(MailRequest request) {
        Map<String, Object> message = new HashMap<>();
        message.put("subject", request.subject());

        Map<String, Object> body = Map.of(
                "contentType", "HTML",
                "content", request.body()
        );
        message.put("body", body);

        message.put("toRecipients", toRecipients(request.to()));
        if (request.cc() != null && !request.cc().isEmpty()) {
            message.put("ccRecipients", toRecipients(request.cc()));
        }
        if (request.bcc() != null && !request.bcc().isEmpty()) {
            message.put("bccRecipients", toRecipients(request.bcc()));
        }
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            message.put("attachments", request.attachments().stream().map(this::toAttachment).toList());
        }
        return message;
    }

    private List<Map<String, Object>> toRecipients(List<String> addresses) {
        return addresses.stream()
                .map(address -> Map.of("emailAddress", Map.of("address", address)))
                .toList();
    }

    private Map<String, Object> toAttachment(MailAttachmentRequest request) {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("@odata.type", "#microsoft.graph.fileAttachment");
        attachment.put("name", request.filename());
        attachment.put("contentType", request.contentType());
        attachment.put("contentBytes", request.base64Data());
        return attachment;
    }
}
