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

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
// Componenti UI JavaFX.

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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

    private final BackendGateway backendGateway;
    // Accesso al backend (polling, lista notifiche, ecc.).

    private final NotificationService notificationService;
    // Event bus interno dove pubblicare/ricevere messaggi.

    private final ObservableList<String> items = FXCollections.observableArrayList();
    // Lista osservabile per ListView: contiene stringhe formattate delle notifiche.

    private final ListView<String> listView = new ListView<>(items);
    // Vista grafica delle notifiche.

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
        container.setCenter(listView);

        // Imposta il contenuto della tab
        setContent(container);

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

        // Converte in stringhe leggibili
        List<String> mapped = notifications.stream()
                .map(item -> String.format("[%s] %s", item.createdAt(), item.title()))
                .collect(Collectors.toList());

        // Aggiornamento della UI (thread JavaFX)
        Platform.runLater(() -> items.setAll(mapped));
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
        Platform.runLater(() -> items.add(0, String.format("[%s] %s", event.getTimestamp(), event.getPayload())));
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
}
