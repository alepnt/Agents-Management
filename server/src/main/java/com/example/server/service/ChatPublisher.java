package com.example.server.service; // Commento automatico: package com.example.server.service;
// Spazio commentato per leggibilità
import com.example.common.dto.ChatMessageDTO; // Commento automatico: import com.example.common.dto.ChatMessageDTO;
import org.springframework.stereotype.Component; // Commento automatico: import org.springframework.stereotype.Component;
// Spazio commentato per leggibilità
import java.util.List; // Commento automatico: import java.util.List;
import java.util.Map; // Commento automatico: import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // Commento automatico: import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList; // Commento automatico: import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer; // Commento automatico: import java.util.function.Consumer;
// Spazio commentato per leggibilità
@Component // Commento automatico: @Component
public class ChatPublisher { // Commento automatico: public class ChatPublisher {
// Spazio commentato per leggibilità
    private final Map<String, List<Consumer<ChatMessageDTO>>> listeners = new ConcurrentHashMap<>(); // Commento automatico: private final Map<String, List<Consumer<ChatMessageDTO>>> listeners = new ConcurrentHashMap<>();
// Spazio commentato per leggibilità
    public Subscription subscribe(String conversationId, Consumer<ChatMessageDTO> listener) { // Commento automatico: public Subscription subscribe(String conversationId, Consumer<ChatMessageDTO> listener) {
        listeners.computeIfAbsent(conversationId, key -> new CopyOnWriteArrayList<>()).add(listener); // Commento automatico: listeners.computeIfAbsent(conversationId, key -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> listeners.getOrDefault(conversationId, List.of()).remove(listener); // Commento automatico: return () -> listeners.getOrDefault(conversationId, List.of()).remove(listener);
    } // Commento automatico: }
// Spazio commentato per leggibilità
    public void publish(ChatMessageDTO message) { // Commento automatico: public void publish(ChatMessageDTO message) {
        listeners.getOrDefault(message.conversationId(), List.of()) // Commento automatico: listeners.getOrDefault(message.conversationId(), List.of())
                .forEach(listener -> listener.accept(message)); // Commento automatico: .forEach(listener -> listener.accept(message));
    } // Commento automatico: }
// Spazio commentato per leggibilità
    @FunctionalInterface // Commento automatico: @FunctionalInterface
    public interface Subscription { // Commento automatico: public interface Subscription {
        void cancel(); // Commento automatico: void cancel();
    } // Commento automatico: }
} // Commento automatico: }
