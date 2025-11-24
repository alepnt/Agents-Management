package com.example.common.observer;

import org.springframework.lang.NonNull;

import java.util.Collection;

/**
 * Interfaccia per i soggetti osservabili.
 */
public interface Subject<T> {

    void registerObserver(@NonNull Observer<T> observer);

    void removeObserver(@NonNull Observer<T> observer);

    void notifyObservers(@NonNull T event);

    @NonNull
    Collection<Observer<T>> getObservers();
}
