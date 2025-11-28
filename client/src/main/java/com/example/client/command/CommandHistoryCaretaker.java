package com.example.client.command; // Package che implementa Command + Memento + Observer lato client.

import com.example.common.observer.NotificationCenter; // Centro notifiche thread-safe per observer.
import com.example.common.observer.Observer; // Interfaccia Observer generica.

import java.util.ArrayList; // Per mantenere lo storico dei memento.
import java.util.Collections; // Per restituire liste immodificabili.
import java.util.List; // Tipo della history.
import java.util.Objects; // Validazione null-safe.

/**
 * Caretaker del pattern Memento che conserva la history dei comandi eseguiti
 * e notifica automaticamente gli osservatori ogni volta che un nuovo memento
 * viene aggiunto.
 *
 * Ruoli implementati:
 * - PATTERN MEMENTO: mantiene la lista dei CommandMemento
 * - PATTERN OBSERVER: notifica gli observer su ogni nuova esecuzione di comando
 */
public class CommandHistoryCaretaker {

    private final List<CommandMemento> history = new ArrayList<>(); // Lista immutabile di memento dei comandi.
    private final NotificationCenter<CommandMemento> notificationCenter // Centro notifiche per gli observer.
            = new NotificationCenter<>();

    /**
     * Aggiunge un memento alla history e notifica tutti gli observer.
     *
     * @param memento memento del comando eseguito
     */
    public void addMemento(CommandMemento memento) {
        history.add(Objects.requireNonNull(memento)); // Aggiunge alla history (validazione null-safe).
        notificationCenter.notifyObservers(memento); // Notifica tutti gli observer registrati.
    }

    /**
     * Restituisce la lista completa dei memento registrati.
     * La lista è immodificabile per garantire integrità esterna.
     *
     * @return history dei comandi eseguiti
     */
    public List<CommandMemento> history() {
        return Collections.unmodifiableList(history); // Protegge la lista da modifiche esterne.
    }

    /**
     * Registra un observer che verrà notificato ad ogni nuova esecuzione di
     * comando.
     *
     * @param observer osservatore da registrare
     */
    public void subscribe(Observer<CommandMemento> observer) {
        notificationCenter.registerObserver(Objects.requireNonNull(observer)); // Validazione + registrazione.
    }

    /**
     * Rimuove un observer precedentemente registrato.
     *
     * @param observer osservatore da rimuovere
     */
    public void unsubscribe(Observer<CommandMemento> observer) {
        notificationCenter.removeObserver(Objects.requireNonNull(observer)); // Rimozione observer.
    }

    /**
     * Ripristina completamente lo stato del caretaker:
     * - cancella la history
     * - rimuove tutti gli observer registrati
     *
     * Utile per test, logout o reset dell'applicazione.
     */
    public void reset() {
        history.clear(); // Svuota la lista dei memento.
        notificationCenter.clearObservers(); // Svuota gli observer registrati.
    }
} // Fine classe CommandHistoryCaretaker.
