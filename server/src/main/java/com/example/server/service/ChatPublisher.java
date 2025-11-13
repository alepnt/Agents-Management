package com.example.server.service;

import com.example.server.dto.ChatMessageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class ChatPublisher {

    private final Map<String, List<Consumer<ChatMessageResponse>>> listeners = new ConcurrentHashMap<>();

    public Subscription subscribe(String conversationId, Consumer<ChatMessageResponse> listener) {
        listeners.computeIfAbsent(conversationId, key -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> listeners.getOrDefault(conversationId, List.of()).remove(listener);
    }

    public void publish(ChatMessageResponse message) {
        listeners.getOrDefault(message.conversationId(), List.of())
                .forEach(listener -> listener.accept(message));
    }

    @FunctionalInterface
    public interface Subscription {
        void cancel();
    }
}
