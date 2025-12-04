package com.example.client.view;
// Package che contiene le viste JavaFX, inclusi componenti complessi come tab e pannelli.

import com.example.client.model.NotificationItem;
// DTO lato client che rappresenta una singola notifica.

import com.example.client.service.BackendGateway;
// Gateway di comunicazione verso il backend (REST API).

import com.example.client.service.NotificationService;
// Event bus locale per distribuire notifiche in tempo reale all'interno del client.

import com.example.common.dto.NotificationMessage;
// Evento di notifica utilizzato dall'observer pattern.

import com.example.common.observer.Observer;
// Interfaccia observer generica del progetto (pattern Observer/Publish–Subscribe).

import javafx.application.Platform;
// Necessario per aggiornare l’interfaccia da thread non-JavaFX.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// Liste osservabili per ListView.

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
// Componenti UI JavaFX.

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
// Threading e utility.

import org.springframework.lang.NonNull;
// Annotazione Spring per parametri non null.

/**
 * Tab dedicata alla visualizzazione delle notifiche.
 *
 * Funzionalità:
 * - mostra notifiche correnti dell'utente
 * - polling periodico delle nuove notifiche dal backend
 * - integrazione con NotificationService per aggiornamenti push locali
 * - possibilità di refresh manuale
 *
 * Implementa:
 * - Observer<NotificationMessage> → riceve eventi dal NotificationService
 * - AutoCloseable → chiude thread e deregistra observer quando il tab viene
 * chiuso
 */
public class NotificationTabView extends Tab implements Observer<NotificationMessage>, AutoCloseable {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    private static final List<NotificationItem> DEFAULT_NOTIFICATIONS = List.of(
            new NotificationItem(null, null, null, "Benvenuto in Agents Management",
                    "", false, Instant.now().minusSeconds(10 * 60)),
            new NotificationItem(null, null, null, "Suggerimento: usa la barra in alto per aprire chat e notifiche",
                    "", false, Instant.now().minusSeconds(5 * 60)),
            new NotificationItem(null, null, null, "Le statistiche sono aggiornate ogni ora",
                    "", false, Instant.now().minusSeconds(4 * 60)),
            new NotificationItem(null, null, null, "Consiglio: prova a ricaricare i dati dal pulsante Aggiorna",
                    "", false, Instant.now().minusSeconds(3 * 60)),
            new NotificationItem(null, null, null, "Notifiche in tempo reale abilitate",
                    "", false, Instant.now().minusSeconds(2 * 60))
    );

    private final BackendGateway backendGateway;
    // Accesso al backend (polling, lista notifiche, ecc.).

    private final NotificationService notificationService;
    // Event bus interno dove pubblicare/ricevere messaggi.

    private final ObservableList<NotificationItem> items = FXCollections.observableArrayList();
    // Lista osservabile per ListView: contiene notifiche con stato di lettura.

    private final ListView<NotificationItem> listView = new ListView<>(items);
    // Vista grafica delle notifiche con cell factory personalizzata.

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // Thread dedicato al polling delle notifiche.

    private final AtomicBoolean running = new AtomicBoolean(false);
    // Indica se il polling è in esecuzione (thread-safe, evita doppio avvio).

    private Long userId;
    // Utente a cui è associato questo tab.

    /**
     * Costruttore della tab di notifiche.
     * Configura UI, pulsanti e registrazione come observer.
     */
    public NotificationTabView(BackendGateway backendGateway, NotificationService notificationService) {
        super("Notifiche"); // titolo della tab

        this.backendGateway = backendGateway;
        this.notificationService = notificationService;

        BorderPane container = new BorderPane();

        // Bottone per refresh manuale
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setOnAction(event -> refresh());
        container.setTop(refreshButton);

        // Lista notifiche al centro
        configureListView();
        container.setCenter(listView);

        // Imposta il contenuto della tab
        setContent(container);

        // Notifiche di default visualizzate finché non arrivano dati reali
        items.setAll(DEFAULT_NOTIFICATIONS);

        // Registra questa vista come observer nel NotificationService
        notificationService.subscribe(this);
    }

    /**
     * Associa un utente alla tab.
     * Dopo il binding vengono caricate le notifiche e avviato il polling.
     */
    public void bindUser(Long userId) {
        this.userId = userId;
        refresh(); // carica notifiche iniziali
        startPolling(); // avvia polling in background
    }

    /**
     * Carica le notifiche dal backend e aggiorna la UI.
     */
    public void refresh() {
        if (userId == null) {
            return;
        }

        // Richiede tutte le notifiche dell'utente
        List<NotificationItem> notifications = backendGateway.listNotifications(userId, null);

        // Mantiene stato di lettura e contenuto
        List<NotificationItem> mapped = mapNotifications(notifications);

        if (mapped.isEmpty()) {
            mapped = DEFAULT_NOTIFICATIONS;
        }

        // Aggiornamento della UI (thread JavaFX)
        List<NotificationItem> finalMapped = mapped;
        Platform.runLater(() -> items.setAll(finalMapped));
    }

    /**
     * Avvia il polling delle notifiche.
     * Se è già attivo, non avvia un secondo thread.
     */
    private void startPolling() {
        if (userId == null || running.getAndSet(true)) {
            return; // evita doppio avvio
        }

        executor.submit(() -> {
            while (running.get()) {
                try {
                    // Recupera nuove notifiche dal backend
                    List<NotificationItem> updates = backendGateway.pollNotifications(userId);

                    if (!updates.isEmpty()) {
                        // Per ogni nuova notifica, pubblica un evento
                        updates.forEach(item -> notificationService.publish(new NotificationMessage(
                                // Il canale dipende dal destinatario:
                                item.teamId() != null
                                        ? "team" + item.teamId()
                                        : "user" + item.userId(),
                                // Payload della notifica
                                item.title() + ": " + item.message(),
                                item.createdAt())));
                    }

                } catch (Exception ex) {
                    // In caso di errore, attende 1 secondo e riprova
                    try {
                        Thread.sleep(1_000);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return; // uscita dal polling
                    }
                }
            }
        });
    }

    /**
     * Callback invocata quando il NotificationService pubblica
     * un nuovo NotificationMessage.
     */
    @Override
    public void update(@NonNull NotificationMessage event) {
        NotificationItem item = new NotificationItem(null, userId, null, event.getPayload(),
                "", false, event.getTimestamp());
        Platform.runLater(() -> items.add(0, item));
    }

    /**
     * Chiusura del tab:
     * - interrompe polling
     * - spegne executor
     * - deregistra observer
     */
    @Override
    public void close() {
        running.set(false);
        executor.shutdownNow();
        notificationService.unsubscribe(this);
    }

    private List<NotificationItem> mapNotifications(List<NotificationItem> notifications) {
        return List.copyOf(notifications);
    }

    private void configureListView() {
        listView.setCellFactory(view -> new ListCell<>() {
            private final Label messageLabel = new Label();
            private final Label timeLabel = new Label();
            private final Region spacer = new Region();
            private final HBox container = new HBox(10, messageLabel, spacer, timeLabel);

            {
                container.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setWrapText(true);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                timeLabel.setStyle("-fx-text-fill: #666;");
            }

            @Override
            protected void updateItem(NotificationItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                String text = item.message() != null && !item.message().isBlank()
                        ? item.title() + " - " + item.message()
                        : item.title();
                messageLabel.setText(text);
                messageLabel.setStyle(item.read()
                        ? ""
                        : "-fx-text-fill: red; -fx-font-weight: bold;");

                timeLabel.setText(TIME_FORMATTER.format(item.createdAt().atZone(ZoneId.systemDefault())));
                setGraphic(container);
            }
        });
    }
}
