package com.example.common.observer; // Package che contiene le astrazioni del pattern Observer.

import org.springframework.lang.NonNull; // Annotazione per imporre parametri non null.

/**
 * Osservatore generico per notifiche di dominio condivise.
 * Viene notificato ogni volta che il Subject emette un evento.
 */
@FunctionalInterface // Indica che l'interfaccia ha un solo metodo astratto → può essere usata con
                     // lambda.
public interface Observer<T> { // Interfaccia parametrica sull’evento da osservare.

    void update(@NonNull T event); // Metodo richiamato dal Subject per notificare un evento.
} // Fine dell’interfaccia Observer.
