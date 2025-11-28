package com.example.client.service;

import com.example.common.dto.NotificationMessage;
import com.example.common.observer.NotificationCenter;
import com.example.common.observer.Observer;

import org.springframework.lang.NonNull;

/**
 * Servizio leggero che funge da wrapper attorno a NotificationCenter.
 *
 * Scopo:
 * - fornire un punto unico dove la UI può registrare, deregistrare
 * e ricevere notifiche in tempo reale provenienti da diverse parti dell'app
 *
 * Pattern:
 * - Observer pattern (pub-sub interno)
 * - NotificationCenter è il dispatcher centralizzato
 *
 * Perché esiste questa classe:
 * - semplifica il codice della UI
 * - mantiene separazione tra Vista e sistema di notifica reale
 * - permette futura estensione (filtri, logging, canali multipli, ecc.)
 */
public class NotificationService {

    // Dispatcher condiviso per tutti gli observer che gestiscono
    // NotificationMessage.
    private final NotificationCenter<NotificationMessage> notificationCenter = new NotificationCenter<>();

    /**
     * Registra un nuovo observer.
     * La UI (es. NotificationTabView) lo usa per ricevere aggiornamenti.
     *
     * @param observer componente che vuole ricevere notifiche
     */
    public void subscribe(@NonNull Observer<NotificationMessage> observer) {
        notificationCenter.registerObserver(observer);
    }

    /**
     * Deregistra un observer.
     * Utile quando una vista viene chiusa o distrutta per evitare memory leak.
     */
    public void unsubscribe(@NonNull Observer<NotificationMessage> observer) {
        notificationCenter.removeObserver(observer);
    }

    /**
     * Pubblica una nuova notifica.
     * Tutti gli observer registrati riceveranno l'evento.
     *
     * @param notification notifica da distribuire
     */
    public void publish(@NonNull NotificationMessage notification) {
        notificationCenter.notifyObservers(notification);
    }

    /**
     * Ritorna il numero di observer attualmente registrati.
     * Utile per debugging o per mostrare se ci sono viste in ascolto.
     */
    public int observerCount() {
        return notificationCenter.getObservers().size();
    }
}
