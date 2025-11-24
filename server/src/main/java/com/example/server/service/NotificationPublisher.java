package com.example.server.service; // Package for service components

import com.example.server.domain.Notification; // Domain entity representing a notification
import org.springframework.stereotype.Component; // Annotation marking a Spring component

import java.util.List; // List interface
import java.util.Map; // Map interface
import java.util.concurrent.ConcurrentHashMap; // Thread-safe map implementation
import java.util.concurrent.CopyOnWriteArrayList; // Thread-safe list implementation
import java.util.function.Consumer; // Functional interface for consumers

@Component // Marks the class as a Spring-managed component
public class NotificationPublisher { // Publishes notifications to observers

    private final Map<Long, List<Consumer<Notification>>> userObservers = new ConcurrentHashMap<>(); // Observers keyed by user
    private final Map<Long, List<Consumer<Notification>>> teamObservers = new ConcurrentHashMap<>(); // Observers keyed by team

    public Subscription subscribeToUser(Long userId, Consumer<Notification> listener) { // Subscribe to user notifications
        userObservers.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(listener); // Add listener for user
        return () -> userObservers.getOrDefault(userId, List.of()).remove(listener); // Provide cancellation handle
    } // End subscribeToUser

    public Subscription subscribeToTeam(Long teamId, Consumer<Notification> listener) { // Subscribe to team notifications
        teamObservers.computeIfAbsent(teamId, key -> new CopyOnWriteArrayList<>()).add(listener); // Add listener for team
        return () -> teamObservers.getOrDefault(teamId, List.of()).remove(listener); // Provide cancellation handle
    } // End subscribeToTeam

    public void publish(Notification notification) { // Publish a notification to relevant observers
        if (notification.getUserId() != null) { // If targeted to user
            userObservers.getOrDefault(notification.getUserId(), List.of()) // Get user listeners
                    .forEach(listener -> listener.accept(notification)); // Notify each listener
        } // End user check
        if (notification.getTeamId() != null) { // If targeted to team
            teamObservers.getOrDefault(notification.getTeamId(), List.of()) // Get team listeners
                    .forEach(listener -> listener.accept(notification)); // Notify each listener
        } // End team check
    } // End publish

    @FunctionalInterface // Functional interface marker
    public interface Subscription { // Represents a subscription handle
        void cancel(); // Method to cancel subscription
    } // End Subscription interface
} // End NotificationPublisher class
