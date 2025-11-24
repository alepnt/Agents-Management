package com.example.client.command;

import com.example.common.observer.NotificationCenter;
import com.example.common.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Caretaker del pattern Memento che notifica gli osservatori ogni volta che viene eseguito un comando.
 */
public class CommandHistoryCaretaker {

    private final List<CommandMemento> history = new ArrayList<>();
    private final NotificationCenter<CommandMemento> notificationCenter = new NotificationCenter<>();

    public void addMemento(CommandMemento memento) {
        history.add(Objects.requireNonNull(memento));
        notificationCenter.notifyObservers(memento);
    }

    public List<CommandMemento> history() {
        return Collections.unmodifiableList(history);
    }

    public void subscribe(Observer<CommandMemento> observer) {
        notificationCenter.registerObserver(Objects.requireNonNull(observer));
    }

    public void unsubscribe(Observer<CommandMemento> observer) {
        notificationCenter.removeObserver(Objects.requireNonNull(observer));
    }

    /**
     * Pulisce la history e rimuove eventuali observer registrati.
     */
    public void reset() {
        history.clear();
        notificationCenter.clearObservers();
    }
}
