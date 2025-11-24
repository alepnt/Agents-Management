package com.example.server.service; // Package declaration for service layer

import com.example.common.dto.MailAttachmentDTO; // DTO describing a mail attachment
import com.example.common.dto.MailRequest; // DTO carrying mail request data
import com.fasterxml.jackson.databind.ObjectMapper; // JSON mapper interface
import com.fasterxml.jackson.databind.json.JsonMapper; // Concrete JSON mapper builder
import org.springframework.http.HttpHeaders; // Constants for HTTP headers
import org.springframework.stereotype.Service; // Annotation to mark service components

import java.io.IOException; // Exception for IO failures
import java.net.URI; // URI representation
import java.net.http.HttpClient; // HTTP client for requests
import java.net.http.HttpRequest; // HTTP request builder
import java.net.http.HttpResponse; // HTTP response container
import java.nio.charset.StandardCharsets; // Charset constants
import java.util.HashMap; // HashMap implementation
import java.util.List; // List interface
import java.util.Map; // Map interface

@Service // Marks the class as a Spring service
public class MailService { // Service responsible for sending emails

    private static final URI GRAPH_ENDPOINT = URI.create("https://graph.microsoft.com/v1.0/me/sendMail"); // Microsoft Graph endpoint for sending mail

    private final HttpClient httpClient = HttpClient.newHttpClient(); // Reusable HTTP client instance
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build(); // JSON mapper with modules

    public void sendMail(String delegatedToken, MailRequest request) { // Send an email using Graph API
        if (delegatedToken == null || delegatedToken.isBlank()) { // Validate presence of token
            throw new IllegalArgumentException("Token Microsoft Graph non presente"); // Error when token missing
        } // End token check

        Map<String, Object> payload = new HashMap<>(); // Payload map for Graph request
        payload.put("message", buildMessage(request)); // Add message content
        payload.put("saveToSentItems", Boolean.TRUE); // Request saving to sent items

        try { // Begin try block for network operations
            HttpRequest httpRequest = HttpRequest.newBuilder(GRAPH_ENDPOINT) // Build HTTP request to Graph endpoint
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + delegatedToken) // Set authorization header
                    .header(HttpHeaders.CONTENT_TYPE, "application/json") // Set content type header
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8)) // Serialize payload as JSON body
                    .build(); // Finalize request

            HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding()); // Execute request and discard body
            if (response.statusCode() >= 400) { // Check for error responses
                throw new IllegalStateException("Invio email fallito con stato " + response.statusCode()); // Throw exception on failure
            } // End status check
        } catch (InterruptedException e) { // Handle interruption
            Thread.currentThread().interrupt(); // Restore interrupt status
            throw new IllegalStateException("Invio email interrotto", e); // Wrap and rethrow
        } catch (IOException e) { // Handle IO issues
            throw new IllegalStateException("Errore durante l'invio dell'email", e); // Wrap and rethrow
        } // End catch blocks
    } // End sendMail

    private Map<String, Object> buildMessage(MailRequest request) { // Build message structure for Graph API
        Map<String, Object> message = new HashMap<>(); // Container for message fields
        message.put("subject", request.subject()); // Set email subject

        Map<String, Object> body = Map.of( // Build body map
                "contentType", "HTML", // Use HTML format
                "content", request.body() // Email content
        ); // End body map
        message.put("body", body); // Attach body to message

        message.put("toRecipients", toRecipients(request.to())); // Add recipients list
        if (request.cc() != null && !request.cc().isEmpty()) { // If CC present
            message.put("ccRecipients", toRecipients(request.cc())); // Add CC recipients
        } // End CC check
        if (request.bcc() != null && !request.bcc().isEmpty()) { // If BCC present
            message.put("bccRecipients", toRecipients(request.bcc())); // Add BCC recipients
        } // End BCC check
        if (request.attachments() != null && !request.attachments().isEmpty()) { // If attachments present
            message.put("attachments", request.attachments().stream().map(this::toAttachment).toList()); // Map attachments to Graph format
        } // End attachments check
        return message; // Return assembled message
    } // End buildMessage

    private List<Map<String, Object>> toRecipients(List<String> addresses) { // Convert email addresses to recipient objects
        return addresses.stream() // Stream addresses
                .map(address -> { // Map each address
                    Map<String, Object> recipient = new HashMap<>(); // Create recipient map
                    recipient.put("emailAddress", Map.of("address", address)); // Put email address structure
                    return recipient; // Return recipient map
                }) // End mapping function
                .toList(); // Collect to list
    } // End toRecipients

    private Map<String, Object> toAttachment(MailAttachmentDTO request) { // Convert attachment DTO to Graph format
        Map<String, Object> attachment = new HashMap<>(); // Attachment map
        attachment.put("@odata.type", "#microsoft.graph.fileAttachment"); // Specify Graph attachment type
        attachment.put("name", request.filename()); // Attachment name
        attachment.put("contentType", request.contentType()); // Attachment MIME type
        attachment.put("contentBytes", request.base64Data()); // Base64 content
        return attachment; // Return attachment map
    } // End toAttachment
} // End MailService class
