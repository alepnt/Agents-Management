package com.example.server.service; // Definisce il package del gestore di pubblicazione chat

import com.example.common.dto.ChatMessageDTO; // Importa il DTO utilizzato per i messaggi di chat
import org.springframework.stereotype.Component; // Importa l'annotazione per registrare il componente Spring

import java.util.List; // Importa la lista per memorizzare i listener
import java.util.Map; // Importa la mappa che associa le conversazioni ai listener
import java.util.concurrent.ConcurrentHashMap; // Importa una mappa concorrente per l'accesso thread-safe
import java.util.concurrent.CopyOnWriteArrayList; // Importa una lista thread-safe per i listener
import java.util.function.Consumer; // Importa l'interfaccia Consumer per processare i messaggi

@Component // Indica che la classe è un componente Spring gestito dal contenitore
public class ChatPublisher { // Gestisce la pubblicazione degli eventi di chat ai sottoscrittori

    private final Map<String, List<Consumer<ChatMessageDTO>>> listeners = new ConcurrentHashMap<>(); // Mappa dei listener organizzati per conversazione

    public Subscription subscribe(String conversationId, Consumer<ChatMessageDTO> listener) { // Registra un nuovo listener per una conversazione
        listeners.computeIfAbsent(conversationId, key -> new CopyOnWriteArrayList<>()).add(listener); // Crea la lista se manca e aggiunge il listener
        return () -> listeners.getOrDefault(conversationId, List.of()).remove(listener); // Restituisce una subscription che permette di annullare l'iscrizione
    }

    public void publish(ChatMessageDTO message) { // Pubblica un messaggio a tutti i listener della conversazione
        listeners.getOrDefault(message.conversationId(), List.of()) // Recupera i listener registrati per la conversazione
                .forEach(listener -> listener.accept(message)); // Notifica ogni listener passando il messaggio
    }

    @FunctionalInterface // Specifica che l'interfaccia ha un unico metodo astratto
    public interface Subscription { // Rappresenta la gestione della sottoscrizione
        void cancel(); // Metodo per annullare la sottoscrizione
    }
}
