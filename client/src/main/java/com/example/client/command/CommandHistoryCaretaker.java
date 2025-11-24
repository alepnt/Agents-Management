package com.example.client.command;

import com.example.common.observer.NotificationCenter;
import com.example.common.observer.Observer;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Caretaker del pattern Memento che notifica gli osservatori ogni volta che viene eseguito un comando.
 */
public class CommandHistoryCaretaker {

    private final List<CommandMemento> history = new ArrayList<>();
    private final NotificationCenter<CommandMemento> notificationCenter = new NotificationCenter<>();

    public void addMemento(@NonNull CommandMemento memento) {
        history.add(memento);
        notificationCenter.notifyObservers(memento);
    }

    public @NonNull List<CommandMemento> history() {
        return Collections.unmodifiableList(history);
    }

    public void subscribe(@NonNull Observer<CommandMemento> observer) {
        notificationCenter.registerObserver(observer);
    }

    public void unsubscribe(@NonNull Observer<CommandMemento> observer) {
        notificationCenter.removeObserver(observer);
    }

    /**
     * Pulisce la history e rimuove eventuali observer registrati.
     */
    public void reset() {
        history.clear();
        notificationCenter.clearObservers();
    }
}
