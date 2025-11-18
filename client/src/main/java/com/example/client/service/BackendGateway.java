package com.example.client.service;

import com.example.client.model.NotificationCreate;
import com.example.client.model.NotificationItem;
import com.example.client.model.NotificationSubscription;
import com.example.client.model.NotificationSubscriptionInfo;
import com.example.client.session.SessionStore;
import com.example.client.service.SessionExpiredException;
import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import com.example.common.dto.AgentDTO;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.ArticleDTO;
import com.example.common.dto.CommissionDTO;
import com.example.common.dto.ContractDTO;
import com.example.common.dto.CustomerDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.MailRequest;
import com.example.common.dto.MessageDTO;
import com.example.common.dto.RoleDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.dto.TeamDTO;
import com.example.common.dto.UserDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gateway REST minimale verso il backend Spring Boot.
 */
public class BackendGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final SessionStore sessionStore;

    public BackendGateway() {
        this("http://localhost:8080");
    }

    public BackendGateway(String baseUrl) {
        this(baseUrl, new SessionStore());
    }

    public BackendGateway(SessionStore sessionStore) {
        this("http://localhost:8080", sessionStore);
    }

    public BackendGateway(String baseUrl, SessionStore sessionStore) {
        this.baseUrl = baseUrl;
        this.sessionStore = sessionStore;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<InvoiceDTO> listInvoices() {
        HttpRequest request = authorizedRequest("/api/invoices")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoice) {
        HttpRequest request = authorizedRequest("/api/invoices")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoice) {
        HttpRequest request = authorizedRequest("/api/invoices/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteInvoice(Long id) {
        HttpRequest request = authorizedRequest("/api/invoices/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public InvoiceDTO registerInvoicePayment(Long id, InvoicePaymentRequest paymentRequest) {
        HttpRequest request = authorizedRequest("/api/invoices/" + id + "/payments")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(paymentRequest), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<CustomerDTO> listCustomers() {
        HttpRequest request = authorizedRequest("/api/customers")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public CustomerDTO createCustomer(CustomerDTO customer) {
        HttpRequest request = authorizedRequest("/api/customers")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(customer), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO customer) {
        HttpRequest request = authorizedRequest("/api/customers/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(customer), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteCustomer(Long id) {
        HttpRequest request = authorizedRequest("/api/customers/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<ArticleDTO> listArticles() {
        HttpRequest request = authorizedRequest("/api/articles")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ArticleDTO createArticle(ArticleDTO article) {
        HttpRequest request = authorizedRequest("/api/articles")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(article), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ArticleDTO updateArticle(Long id, ArticleDTO article) {
        HttpRequest request = authorizedRequest("/api/articles/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(article), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteArticle(Long id) {
        HttpRequest request = authorizedRequest("/api/articles/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<DocumentHistoryDTO> invoiceHistory(Long id) {
        HttpRequest request = authorizedRequest("/api/invoices/" + id + "/history")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ContractDTO> listContracts() {
        HttpRequest request = authorizedRequest("/api/contracts")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO createContract(ContractDTO contract) {
        HttpRequest request = authorizedRequest("/api/contracts")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO updateContract(Long id, ContractDTO contract) {
        HttpRequest request = authorizedRequest("/api/contracts/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteContract(Long id) {
        HttpRequest request = authorizedRequest("/api/contracts/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<AgentDTO> listAgents() {
        HttpRequest request = authorizedRequest("/api/agents")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public AgentDTO getAgent(Long id) {
        HttpRequest request = authorizedRequest("/api/agents/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public AgentDTO createAgent(AgentDTO agentDTO) {
        HttpRequest request = authorizedRequest("/api/agents")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(agentDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public AgentDTO updateAgent(Long id, AgentDTO agentDTO) {
        HttpRequest request = authorizedRequest("/api/agents/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(agentDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteAgent(Long id) {
        HttpRequest request = authorizedRequest("/api/agents/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<TeamDTO> listTeams() {
        HttpRequest request = authorizedRequest("/api/teams")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public TeamDTO getTeam(Long id) {
        HttpRequest request = authorizedRequest("/api/teams/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        HttpRequest request = authorizedRequest("/api/teams")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(teamDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        HttpRequest request = authorizedRequest("/api/teams/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(teamDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteTeam(Long id) {
        HttpRequest request = authorizedRequest("/api/teams/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<RoleDTO> listRoles() {
        HttpRequest request = authorizedRequest("/api/roles")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public RoleDTO getRole(Long id) {
        HttpRequest request = authorizedRequest("/api/roles/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public RoleDTO createRole(RoleDTO roleDTO) {
        HttpRequest request = authorizedRequest("/api/roles")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(roleDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        HttpRequest request = authorizedRequest("/api/roles/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(roleDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteRole(Long id) {
        HttpRequest request = authorizedRequest("/api/roles/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<UserDTO> listUsers() {
        HttpRequest request = authorizedRequest("/api/users")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public UserDTO getUser(Long id) {
        HttpRequest request = authorizedRequest("/api/users/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public UserDTO createUser(UserDTO userDTO) {
        HttpRequest request = authorizedRequest("/api/users")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(userDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        HttpRequest request = authorizedRequest("/api/users/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(userDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteUser(Long id) {
        HttpRequest request = authorizedRequest("/api/users/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<MessageDTO> listMessages() {
        HttpRequest request = authorizedRequest("/api/messages")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public MessageDTO getMessage(Long id) {
        HttpRequest request = authorizedRequest("/api/messages/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public MessageDTO createMessage(MessageDTO messageDTO) {
        HttpRequest request = authorizedRequest("/api/messages")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(messageDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public MessageDTO updateMessage(Long id, MessageDTO messageDTO) {
        HttpRequest request = authorizedRequest("/api/messages/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(messageDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteMessage(Long id) {
        HttpRequest request = authorizedRequest("/api/messages/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<CommissionDTO> listCommissions() {
        HttpRequest request = authorizedRequest("/api/commissions")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public CommissionDTO getCommission(Long id) {
        HttpRequest request = authorizedRequest("/api/commissions/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public CommissionDTO createCommission(CommissionDTO commissionDTO) {
        HttpRequest request = authorizedRequest("/api/commissions")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(commissionDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public CommissionDTO updateCommission(Long id, CommissionDTO commissionDTO) {
        HttpRequest request = authorizedRequest("/api/commissions/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(commissionDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteCommission(Long id) {
        HttpRequest request = authorizedRequest("/api/commissions/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<InvoiceLineDTO> listInvoiceLines(Long invoiceId) {
        String path = "/api/invoice-lines" + (invoiceId != null ? "?invoiceId=" + invoiceId : "");
        HttpRequest request = authorizedRequest(path)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceLineDTO getInvoiceLine(Long id) {
        HttpRequest request = authorizedRequest("/api/invoice-lines/" + id)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceLineDTO createInvoiceLine(InvoiceLineDTO invoiceLineDTO) {
        HttpRequest request = authorizedRequest("/api/invoice-lines")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(invoiceLineDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceLineDTO updateInvoiceLine(Long id, InvoiceLineDTO invoiceLineDTO) {
        HttpRequest request = authorizedRequest("/api/invoice-lines/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(invoiceLineDTO), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteInvoiceLine(Long id) {
        HttpRequest request = authorizedRequest("/api/invoice-lines/" + id)
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<DocumentHistoryDTO> contractHistory(Long id) {
        HttpRequest request = authorizedRequest("/api/contracts/" + id + "/history")
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public DocumentHistoryPageDTO searchDocumentHistory(DocumentType documentType,
                                                        Long documentId,
                                                        List<DocumentAction> actions,
                                                        Instant from,
                                                        Instant to,
                                                        String search,
                                                        int page,
                                                        int size) {
        String path = buildHistoryPath("/api/history", documentType, documentId, actions, from, to, search, page, size);
        HttpRequest request = authorizedRequest(path)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public byte[] exportDocumentHistory(DocumentType documentType,
                                        Long documentId,
                                        List<DocumentAction> actions,
                                        Instant from,
                                        Instant to,
                                        String search) {
        String path = buildHistoryPath("/api/history/export", documentType, documentId, actions, from, to, search, 0, 0);
        HttpRequest request = authorizedRequest(path)
                .GET()
                .build();
        return sendBytes(request);
    }

    public AgentStatisticsDTO agentStatistics(Integer year) {
        String path = year != null ? "/api/stats/agent?year=" + year : "/api/stats/agent";
        HttpRequest request = authorizedRequest(path)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public TeamStatisticsDTO teamStatistics(Integer year) {
        String path = year != null ? "/api/stats/team?year=" + year : "/api/stats/team";
        HttpRequest request = authorizedRequest(path)
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
        HttpRequest request = authorizedRequest(path.toString())
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<NotificationItem> pollNotifications(Long userId) {
        HttpRequest request = authorizedRequest("/api/notifications/subscribe?userId=" + userId)
                .timeout(Duration.ofSeconds(35))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public byte[] downloadClosedInvoicesReport(LocalDate from, LocalDate to, Long agentId) {
        StringBuilder path = new StringBuilder("/api/reports/closed-invoices");
        List<String> params = new ArrayList<>();
        if (from != null) {
            params.add("from=" + URLEncoder.encode(from.toString(), StandardCharsets.UTF_8));
        }
        if (to != null) {
            params.add("to=" + URLEncoder.encode(to.toString(), StandardCharsets.UTF_8));
        }
        if (agentId != null) {
            params.add("agentId=" + agentId);
        }
        if (!params.isEmpty()) {
            path.append('?').append(String.join("&", params));
        }
        HttpRequest request = authorizedRequest(path.toString())
                .GET()
                .build();
        return sendBytes(request);
    }

    public NotificationItem publishNotification(NotificationCreate create) {
        HttpRequest request = authorizedRequest("/api/notifications")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(create), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public NotificationSubscriptionInfo registerNotificationChannel(NotificationSubscription subscription) {
        HttpRequest request = authorizedRequest("/api/notification-subscriptions")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(subscription), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatConversationDTO> listChatConversations(Long userId) {
        HttpRequest request = authorizedRequest("/api/chat/conversations?userId=" + userId)
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatMessageDTO> listChatMessages(Long userId, String conversationId, Instant since) {
        StringBuilder path = new StringBuilder("/api/chat/messages?userId=")
                .append(userId)
                .append("&conversationId=")
                .append(conversationId);
        if (since != null) {
            path.append("&since=").append(since);
        }
        HttpRequest request = authorizedRequest(path.toString())
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ChatMessageDTO> pollChatMessages(Long userId, String conversationId) {
        HttpRequest request = authorizedRequest("/api/chat/poll?userId=" + userId + "&conversationId=" + conversationId)
                .timeout(Duration.ofSeconds(35))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ChatMessageDTO sendChatMessage(ChatMessageRequest message) {
        HttpRequest request = authorizedRequest("/api/chat/messages")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(message), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void sendMail(MailRequest mail, String delegatedToken) {
        HttpRequest request = authorizedRequest("/api/mail/send")
                .header("Content-Type", "application/json")
                .header("X-Delegated-Authorization", "Bearer " + delegatedToken)
                .POST(HttpRequest.BodyPublishers.ofString(write(mail), StandardCharsets.UTF_8))
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    private URI uri(String path) {
        return URI.create(baseUrl + path);
    }

    private HttpRequest.Builder authorizedRequest(String path) {
        String token = sessionStore.currentToken()
                .orElseThrow(() -> new SessionExpiredException("Nessuna sessione attiva o token scaduto. Effettua nuovamente il login."));
        return HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Authorization", "Bearer " + token);
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
            if (statusCode == 401 || statusCode == 403) {
                handleUnauthorized(statusCode, response.body());
            }
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

    private byte[] sendBytes(HttpRequest request) {
        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            if (statusCode == 401 || statusCode == 403) {
                handleUnauthorized(statusCode, "");
            }
            if (statusCode >= 200 && statusCode < 300) {
                byte[] body = response.body();
                return body != null ? body : new byte[0];
            }
            throw new IllegalStateException("Errore chiamata API: " + statusCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chiamata interrotta", e);
        } catch (IOException e) {
            throw new IllegalStateException("Errore di comunicazione con il backend", e);
        }
    }

    private void handleUnauthorized(int statusCode, String body) {
        try {
            sessionStore.clear();
        } catch (IOException ignored) {
            // Ignora eventuali errori di pulizia
        }
        throw new SessionExpiredException("Sessione non autorizzata o scaduta (HTTP " + statusCode + "). " + body);
    }

    private String buildHistoryPath(String basePath,
                                    DocumentType documentType,
                                    Long documentId,
                                    List<DocumentAction> actions,
                                    Instant from,
                                    Instant to,
                                    String search,
                                    int page,
                                    int size) {
        List<String> params = new ArrayList<>();
        params.add("page=" + Math.max(page, 0));
        params.add("size=" + Math.max(size, 0));
        if (documentType != null) {
            params.add("documentType=" + documentType.name());
        }
        if (documentId != null) {
            params.add("documentId=" + documentId);
        }
        if (actions != null && !actions.isEmpty()) {
            String joined = actions.stream()
                    .filter(Objects::nonNull)
                    .map(DocumentAction::name)
                    .sorted()
                    .reduce((a, b) -> a + "," + b)
                    .orElse(null);
            if (joined != null && !joined.isEmpty()) {
                params.add("actions=" + joined);
            }
        }
        if (from != null) {
            params.add("from=" + URLEncoder.encode(from.toString(), StandardCharsets.UTF_8));
        }
        if (to != null) {
            params.add("to=" + URLEncoder.encode(to.toString(), StandardCharsets.UTF_8));
        }
        if (search != null && !search.isBlank()) {
            params.add("q=" + URLEncoder.encode(search, StandardCharsets.UTF_8));
        }
        return params.isEmpty() ? basePath : basePath + "?" + String.join("&", params);
    }
}
