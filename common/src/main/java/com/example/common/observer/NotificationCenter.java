package com.example.common.observer;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implementazione thread-safe del pattern Observer per chat/notifiche.
 */
public class NotificationCenter<T> implements Subject<T> {

    private final Set<Observer<T>> observers = new CopyOnWriteArraySet<>();

    @Override
    public void registerObserver(@NonNull Observer<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(@NonNull Observer<T> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(@NonNull T event) {
        observers.forEach(observer -> observer.update(event));
    }

    @Override
    public @NonNull Collection<Observer<T>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }

    /**
     * Rimuove tutti gli observer registrati.
     */
    public void clearObservers() {
        observers.clear();
    }
}
