package com.example.client.view;
// Package che contiene le viste JavaFX personalizzate lato client.

import com.example.client.service.BackendGateway;
// Gateway per comunicare con il backend (API chat).

import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import com.example.common.dto.UserDTO;
// DTO condivisi per conversazioni, messaggi e richiesta invio messaggio.

import javafx.application.Platform;
// Usato per eseguire aggiornamenti thread-safe sulla UI JavaFX.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// Liste osservabili per ListView.

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
// Componenti UI JavaFX.

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
// Threading per polling continuo dei messaggi.

/**
 * Vista JavaFX completa per la chat:
 * - lista conversazioni (sinistra)
 * - lista messaggi (destra)
 * - composer per inviare nuovi messaggi (in basso)
 *
 * Gestisce anche:
 * - polling attivo dei messaggi (thread dedicato)
 * - caricamento conversazioni e messaggi
 * - invio messaggi
 *
 * Questa classe funge sia da UI che da controller.
 */
public class ChatView extends BorderPane {

    private final BackendGateway backendGateway;
    // Gateway di comunicazione verso il server per la chat.

    private final ObservableList<ChatConversationDTO> conversations = FXCollections.observableArrayList();
    // Lista osservabile delle conversazioni.

    private final ObservableList<RenderedMessage> messages = FXCollections.observableArrayList();
    // Messaggi arricchiti con nome visualizzato per la ListView.

    private final ListView<ChatConversationDTO> conversationList = new ListView<>(conversations);
    // ListView delle conversazioni.

    private final ListView<RenderedMessage> messageList = new ListView<>(messages);
    // ListView dei messaggi della conversazione selezionata.

    private final TextArea composer = new TextArea();
    // Area di testo in cui scrivere il messaggio.

    private final Button sendButton = new Button("Invia");
    // Bottone che invia il messaggio.

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // Thread dedicato al polling dei nuovi messaggi.

    private final AtomicBoolean polling = new AtomicBoolean(false);
    // Indica se il polling è attivo (thread-safe).

    private final Map<Long, String> displayNameCache = new ConcurrentHashMap<>();
    // Cache thread-safe dei nomi visualizzati degli utenti.

    private Long userId;
    // ID dell’utente che sta usando la chat.

    private String conversationId;
    // ID della conversazione selezionata.

    /**
     * Costruzione della vista completa.
     * Imposta layout, listeners e pulsanti.
     */
    public ChatView(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;

        // === Layout principale con SplitPane (conversazioni | messaggi) ===
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(conversationList); // colonna sinistra
        splitPane.getItems().add(messageList); // colonna destra
        splitPane.setDividerPositions(0.3); // rapporto larghezze
        setCenter(splitPane);

        conversationList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ChatConversationDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.title());
                }
            }
        });

        messageList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RenderedMessage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label senderLabel = new Label(item.displayName());
                senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorForUser(item.senderId()) + ";");

                Label timestampLabel = new Label(item.createdAt());
                timestampLabel.setStyle("-fx-text-fill: derive(-fx-text-base-color, -20%);");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox header = new HBox(8, senderLabel, spacer, timestampLabel);
                header.setFillHeight(false);

                Label bodyLabel = new Label(item.body());
                bodyLabel.setWrapText(true);

                VBox content = new VBox(4, header, bodyLabel);
                content.setPadding(new Insets(6));

                setGraphic(content);
            }
        });

        // === Box inferiore per composizione messaggi ===
        HBox composerBox = new HBox(8, composer, sendButton);
        composerBox.setPadding(new Insets(8));
        composer.setPromptText("Scrivi un messaggio...");
        composer.setPrefRowCount(2);

        // Invio messaggio al click del pulsante
        sendButton.setOnAction(event -> sendMessage());
        setBottom(composerBox);

        // Listener sulla selezione conversazione
        conversationList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                conversationId = selected.conversationId();
                loadMessages();
                startPolling();
            }
        });
    }

    /**
     * Associa la view a un utente specifico.
     * Una volta impostato l’utente viene caricata la lista conversazioni.
     */
    public void bindUser(Long userId) {
        this.userId = userId;
        loadConversations();
    }

    /**
     * Carica la lista delle conversazioni dell’utente dal backend.
     */
    private void loadConversations() {
        if (userId == null) {
            return;
        }
        List<ChatConversationDTO> data = backendGateway.listChatConversations(userId);
        Platform.runLater(() -> conversations.setAll(data));
    }

    /**
     * Carica i messaggi della conversazione attualmente selezionata.
     */
    private void loadMessages() {
        if (userId == null || conversationId == null) {
            return;
        }
        List<ChatMessageDTO> data = backendGateway.listChatMessages(userId, conversationId, null);

        Platform.runLater(() -> {
            messages.setAll(
                    data.stream()
                            .map(this::renderMessage)
                            .toList());
        });
    }

    /**
     * Invia un messaggio al backend.
     */
    private void sendMessage() {
        if (userId == null || conversationId == null || composer.getText().isBlank()) {
            return;
        }

        backendGateway.sendChatMessage(
                new ChatMessageRequest(userId, conversationId, composer.getText()));

        composer.clear();
        loadMessages(); // ricarica messaggi dopo invio
    }

    /**
     * Avvia il polling dei nuovi messaggi per la conversazione selezionata.
     * Il polling gira in un thread separato.
     */
    private void startPolling() {
        // Se il polling è già attivo → non avviare un secondo thread.
        if (polling.getAndSet(true)) {
            return;
        }

        executor.submit(() -> {
            while (polling.get()) {
                try {
                    // Richiede nuovi messaggi dal backend
                    List<ChatMessageDTO> newMessages = backendGateway.pollChatMessages(userId, conversationId);

                    // Se ci sono messaggi non ancora mostrati → aggiungili
                    if (!newMessages.isEmpty()) {
                        Platform.runLater(() -> newMessages.stream()
                                .map(this::renderMessage)
                                .forEach(messages::add));
                    }

                } catch (Exception ex) {
                    // Se errore → attende 1 secondo e riprende polling
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return; // termina polling
                    }
                }
            }
        });
    }

    /**
     * Ferma la vista: interrompe il polling e chiude il thread.
     */
    public void stop() {
        polling.set(false);
        executor.shutdownNow();
    }

    private RenderedMessage renderMessage(ChatMessageDTO message) {
        String displayName = resolveDisplayName(message.senderId());
        return new RenderedMessage(message.createdAt().toString(), displayName, message.senderId(), message.body());
    }

    private String colorForUser(Long userId) {
        int hash = userId != null ? userId.hashCode() : 0;
        double hue = 48 + (hash % 20 - 10); // gamme vicino al giallo
        double saturation = 0.65 + (Math.abs(hash % 7) / 20.0);
        double brightness = 0.65 + (Math.abs(hash % 5) / 25.0);
        Color color = Color.hsb(hue, Math.min(1.0, saturation), Math.min(1.0, brightness));
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("rgb(%d,%d,%d)", r, g, b);
    }

    private String resolveDisplayName(Long userId) {
        if (userId == null) {
            return "Sconosciuto";
        }

        return displayNameCache.computeIfAbsent(userId, id -> {
            try {
                UserDTO user = backendGateway.getUser(id);
                String displayName = user != null ? user.getDisplayName() : null;
                return displayName != null && !displayName.isBlank() ? displayName : "Utente " + id;
            } catch (Exception ex) {
                return "Utente " + id;
            }
        });
    }

    private record RenderedMessage(String createdAt, String displayName, Long senderId, String body) { }
}
