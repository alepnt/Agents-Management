package com.example.common.observer; // Package che contiene le astrazioni del pattern Observer.

import org.springframework.lang.NonNull; // Annotazione per indicare parametri non null.
import java.util.Collection; // Usata per restituire l’elenco degli observer.

/**
 * Interfaccia per i soggetti osservabili.
 * Definisce il contratto che ogni "Subject" deve rispettare nel pattern
 * Observer.
 */
public interface Subject<T> { // Interfaccia generica parametrizzata sul tipo di evento osservato.

    void registerObserver(@NonNull Observer<T> observer); // Registra un observer al soggetto osservabile.

    void removeObserver(@NonNull Observer<T> observer); // Rimuove un observer precedentemente registrato.

    void notifyObservers(@NonNull T event); // Notifica l’evento a tutti gli observer registrati.

    @NonNull
    Collection<Observer<T>> getObservers(); // Restituisce la lista attuale degli observer.
} // Fine dell’interfaccia Subject.
