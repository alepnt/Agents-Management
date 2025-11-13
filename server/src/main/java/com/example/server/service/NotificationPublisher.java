package com.example.server.service;

import com.example.server.domain.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class NotificationPublisher {

    private final Map<Long, List<Consumer<Notification>>> userObservers = new ConcurrentHashMap<>();
    private final Map<Long, List<Consumer<Notification>>> teamObservers = new ConcurrentHashMap<>();

    public Subscription subscribeToUser(Long userId, Consumer<Notification> listener) {
        userObservers.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> userObservers.getOrDefault(userId, List.of()).remove(listener);
    }

    public Subscription subscribeToTeam(Long teamId, Consumer<Notification> listener) {
        teamObservers.computeIfAbsent(teamId, key -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> teamObservers.getOrDefault(teamId, List.of()).remove(listener);
    }

    public void publish(Notification notification) {
        if (notification.getUserId() != null) {
            userObservers.getOrDefault(notification.getUserId(), List.of())
                    .forEach(listener -> listener.accept(notification));
        }
        if (notification.getTeamId() != null) {
            teamObservers.getOrDefault(notification.getTeamId(), List.of())
                    .forEach(listener -> listener.accept(notification));
        }
    }

    @FunctionalInterface
    public interface Subscription {
        void cancel();
    }
}
