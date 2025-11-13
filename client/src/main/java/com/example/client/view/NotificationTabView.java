package com.example.client.view;

import com.example.client.model.NotificationItem;
import com.example.client.service.BackendGateway;
import com.example.client.service.NotificationService;
import com.example.common.dto.NotificationMessage;
import com.example.common.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class NotificationTabView extends Tab implements Observer<NotificationMessage>, AutoCloseable {

    private final BackendGateway backendGateway;
    private final NotificationService notificationService;
    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ListView<String> listView = new ListView<>(items);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);

    private Long userId;

    public NotificationTabView(BackendGateway backendGateway, NotificationService notificationService) {
        super("Notifiche");
        this.backendGateway = backendGateway;
        this.notificationService = notificationService;
        BorderPane container = new BorderPane();
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setOnAction(event -> refresh());
        container.setTop(refreshButton);
        container.setCenter(listView);
        setContent(container);
        notificationService.subscribe(this);
    }

    public void bindUser(Long userId) {
        this.userId = userId;
        refresh();
        startPolling();
    }

    public void refresh() {
        if (userId == null) {
            return;
        }
        List<NotificationItem> notifications = backendGateway.listNotifications(userId, null);
        List<String> mapped = notifications.stream()
                .map(item -> String.format("[%s] %s", item.createdAt(), item.title()))
                .collect(Collectors.toList());
        Platform.runLater(() -> items.setAll(mapped));
    }

    private void startPolling() {
        if (userId == null || running.getAndSet(true)) {
            return;
        }
        executor.submit(() -> {
            while (running.get()) {
                try {
                    List<NotificationItem> updates = backendGateway.pollNotifications(userId);
                    if (!updates.isEmpty()) {
                        updates.forEach(item -> notificationService.publish(new NotificationMessage(
                                item.teamId() != null ? "team" + item.teamId() : "user" + item.userId(),
                                item.title() + ": " + item.message(),
                                item.createdAt()
                        )));
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

    @Override
    public void update(NotificationMessage event) {
        Platform.runLater(() -> items.add(0, String.format("[%s] %s", event.getTimestamp(), event.getPayload())));
    }

    @Override
    public void close() {
        running.set(false);
        executor.shutdownNow();
        notificationService.unsubscribe(this);
    }
}
