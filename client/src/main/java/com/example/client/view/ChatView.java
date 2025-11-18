package com.example.client.view;

import com.example.client.service.BackendGateway;
import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatView extends BorderPane {

    private final BackendGateway backendGateway;
    private final ObservableList<ChatConversationDTO> conversations = FXCollections.observableArrayList();
    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private final ListView<ChatConversationDTO> conversationList = new ListView<>(conversations);
    private final ListView<String> messageList = new ListView<>(messages);
    private final TextArea composer = new TextArea();
    private final Button sendButton = new Button("Invia");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean polling = new AtomicBoolean(false);

    private Long userId;
    private String conversationId;

    public ChatView(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(conversationList);
        splitPane.getItems().add(messageList);
        splitPane.setDividerPositions(0.3);
        setCenter(splitPane);

        HBox composerBox = new HBox(8, composer, sendButton);
        composerBox.setPadding(new Insets(8));
        composer.setPromptText("Scrivi un messaggio...");
        composer.setPrefRowCount(2);
        sendButton.setOnAction(event -> sendMessage());
        setBottom(composerBox);

        conversationList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                conversationId = selected.conversationId();
                loadMessages();
                startPolling();
            }
        });
    }

    public void bindUser(Long userId) {
        this.userId = userId;
        loadConversations();
    }

    private void loadConversations() {
        if (userId == null) {
            return;
        }
        List<ChatConversationDTO> data = backendGateway.listChatConversations(userId);
        Platform.runLater(() -> conversations.setAll(data));
    }

    private void loadMessages() {
        if (userId == null || conversationId == null) {
            return;
        }
        List<ChatMessageDTO> data = backendGateway.listChatMessages(userId, conversationId, null);
        Platform.runLater(() -> {
            messages.setAll(data.stream()
                    .map(msg -> String.format("%s: %s", msg.createdAt(), msg.body()))
                    .toList());
        });
    }

    private void sendMessage() {
        if (userId == null || conversationId == null || composer.getText().isBlank()) {
            return;
        }
        backendGateway.sendChatMessage(new ChatMessageRequest(userId, conversationId, composer.getText()));
        composer.clear();
        loadMessages();
    }

    private void startPolling() {
        if (polling.getAndSet(true)) {
            return;
        }
        executor.submit(() -> {
            while (polling.get()) {
                try {
                    List<ChatMessageDTO> newMessages = backendGateway.pollChatMessages(userId, conversationId);
                    if (!newMessages.isEmpty()) {
                        Platform.runLater(() -> newMessages.forEach(msg -> messages.add(String.format("%s: %s",
                                msg.createdAt(), msg.body()))));
                    }
                } catch (Exception ex) {
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }

    public void stop() {
        polling.set(false);
        executor.shutdownNow();
    }
}
