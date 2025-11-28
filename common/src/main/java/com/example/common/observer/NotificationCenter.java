package com.example.common.observer; // Package che contiene le implementazioni del pattern Observer.

import org.springframework.lang.NonNull; // Annotazioni per indicare valori che non possono essere null.

import java.util.Collection; // Tipo generico per collezioni.
import java.util.Collections; // Utility per creare viste immodificabili.
import java.util.Set; // Interfaccia Set per gestire gli observer senza duplicati.
import java.util.concurrent.CopyOnWriteArraySet; // Implementazione thread-safe adatta a scenari di lettura intensiva.

/**
 * Implementazione thread-safe del pattern Observer per chat/notifiche.
 * Gestisce la sottoscrizione e notifica degli observer, garantendo sicurezza in
 * multithreading.
 */
public class NotificationCenter<T> implements Subject<T> { // Implementazione generica del Subject parametrizzata sul
                                                           // tipo di evento.

    private final @NonNull Set<Observer<T>> observers // Insieme degli observer registrati.
            = new CopyOnWriteArraySet<>(); // Struttura thread-safe: perfetta per molti notify e pochi update.

    @Override
    public void registerObserver(@NonNull Observer<T> observer) { // Registra un observer al centro notifiche.
        observers.add(observer); // CopyOnWriteArraySet garantisce assenza di duplicati.
    }

    @Override
    public void removeObserver(@NonNull Observer<T> observer) { // Rimuove un observer registrato.
        observers.remove(observer); // Operazione sicura in contesti multithread.
    }

    @Override
    public void notifyObservers(@NonNull T event) { // Notifica un evento a tutti gli observer.
        observers.forEach(observer -> observer.update(event)); // Iterazione thread-safe garantita dalla struttura dati
                                                               // scelta.
    }

    @Override
    public @NonNull Collection<Observer<T>> getObservers() { // Restituisce gli observer registrati.
        return Collections.unmodifiableSet(observers); // Vista read-only per garantire immutabilità esterna.
    }

    /**
     * Rimuove tutti gli observer registrati.
     * Utile in fase di shutdown o reset di sistemi che usano NotificationCenter.
     */
    public void clearObservers() { // Cancella tutte le sottoscrizioni.
        observers.clear(); // CopyOnWriteArraySet gestisce correttamente l'operazione.
    }
} // Fine della classe NotificationCenter.
