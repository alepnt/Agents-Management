package com.example.common.observer;

import org.springframework.lang.NonNull;

/**
 * Osservatore generico per notifiche di dominio condivise.
 */
@FunctionalInterface
public interface Observer<T> {

    void update(@NonNull T event);
}
