package com.example.client.service;

import com.example.client.model.ChatConversation;
import com.example.client.model.ChatMessage;
import com.example.client.model.ChatMessageSend;
import com.example.client.model.MailMessage;
import com.example.client.model.NotificationCreate;
import com.example.client.model.NotificationItem;
import com.example.client.model.NotificationSubscription;
import com.example.client.model.NotificationSubscriptionInfo;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Gateway REST minimale verso il backend Spring Boot.
 */
public class BackendGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public BackendGateway() {
        this("http://localhost:8080");
    }

    public BackendGateway(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<InvoiceDTO> listInvoices() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoice) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoice) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteInvoice(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id))
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public InvoiceDTO registerInvoicePayment(Long id, InvoicePaymentRequest paymentRequest) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id + "/payments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(paymentRequest), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<DocumentHistoryDTO> invoiceHistory(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id + "/history"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ContractDTO> listContracts() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO createContract(ContractDTO contract) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO updateContract(Long id, ContractDTO contract) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteContract(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id))
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<DocumentHistoryDTO> contractHistory(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id + "/history"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<NotificationItem> listNotifications(Long userId, Instant since) {
        StringBuilder path = new StringBuilder("/api/notifications?userId=").append(userId);
        if (since != null) {
            path.append("&since=").append(since);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri(path.toString()))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<NotificationItem> pollNotifications(Long userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/notifications/subscribe?userId=" + userId))
                .timeout(Duration.ofSeconds(35))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public NotificationItem publishNotification(NotificationCreate create) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/notifications"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(create), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public NotificationSubscriptionInfo registerNotificationChannel(NotificationSubscription subscription) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/notifications/subscribe"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(subscription), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatConversation> listChatConversations(Long userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/chat/conversations?userId=" + userId))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatMessage> listChatMessages(Long userId, String conversationId, Instant since) {
        StringBuilder path = new StringBuilder("/api/chat/messages?userId=")
                .append(userId)
                .append("&conversationId=")
                .append(conversationId);
        if (since != null) {
            path.append("&since=").append(since);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri(path.toString()))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatMessage> pollChatMessages(Long userId, String conversationId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/chat/poll?userId=" + userId + "&conversationId=" + conversationId))
                .timeout(Duration.ofSeconds(35))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ChatMessage sendChatMessage(ChatMessageSend message) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/chat/messages"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(message), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void sendMail(MailMessage mail, String delegatedToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/mail/send"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + delegatedToken)
                .POST(HttpRequest.BodyPublishers.ofString(write(mail), StandardCharsets.UTF_8))
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    private URI uri(String path) {
        return URI.create(baseUrl + path);
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile serializzare la richiesta", e);
        }
    }

    private <T> T send(HttpRequest request, TypeReference<T> typeReference) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                if (typeReference.getType() == Void.class || response.body() == null || response.body().isBlank()) {
                    return null;
                }
                return objectMapper.readValue(response.body(), typeReference);
            }
            throw new IllegalStateException("Errore chiamata API: " + statusCode + " - " + response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chiamata interrotta", e);
        } catch (IOException e) {
            throw new IllegalStateException("Errore di comunicazione con il backend", e);
        }
    }
}
