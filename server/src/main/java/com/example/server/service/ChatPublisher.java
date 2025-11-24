package com.example.server.service; // Inserisce il componente nel package dedicato ai servizi del server.

import com.example.common.dto.ChatMessageDTO; // Importa il DTO che incapsula il contenuto di un messaggio di chat.
import org.springframework.stereotype.Component; // Importa l'annotazione che registra la classe come componente Spring.

import java.util.List; // Importa l'interfaccia List per gestire collezioni di listener.
import java.util.Map; // Importa Map per associare conversazioni e relativi listener.
import java.util.concurrent.ConcurrentHashMap; // Importa la mappa concorrente che consente accessi thread-safe.
import java.util.concurrent.CopyOnWriteArrayList; // Importa una lista thread-safe adatta a letture frequenti.
import java.util.function.Consumer; // Importa l'interfaccia Consumer usata per ricevere i messaggi pubblicati.

@Component // Indica che la classe è un componente gestito dal container Spring.
public class ChatPublisher { // Gestisce la registrazione dei listener e la diffusione dei messaggi di chat.

    private final Map<String, List<Consumer<ChatMessageDTO>>> listeners = new ConcurrentHashMap<>(); // Mantiene i listener organizzati per identificativo di conversazione.

    public Subscription subscribe(String conversationId, Consumer<ChatMessageDTO> listener) { // Registra un nuovo listener per la conversazione indicata.
        listeners.computeIfAbsent(conversationId, key -> new CopyOnWriteArrayList<>()).add(listener); // Crea la lista se assente e aggiunge il listener in modo thread-safe.
        return () -> listeners.getOrDefault(conversationId, List.of()).remove(listener); // Restituisce un handler che permette di annullare in seguito la sottoscrizione.
    } // Chiusura del metodo subscribe.

    public void publish(ChatMessageDTO message) { // Invia un messaggio a tutti i listener della conversazione.
        listeners.getOrDefault(message.conversationId(), List.of()) // Recupera i listener registrati per l'id di conversazione.
                .forEach(listener -> listener.accept(message)); // Notifica ogni listener passando il messaggio ricevuto.
    } // Chiusura del metodo publish.

    @FunctionalInterface // Specifica che l'interfaccia dichiara un solo metodo astratto.
    public interface Subscription { // Rappresenta la gestione di una sottoscrizione a eventi di chat.
        void cancel(); // Annulla la sottoscrizione rimossa precedentemente.
    } // Chiusura dell'interfaccia Subscription.
} // Fine della classe ChatPublisher.
