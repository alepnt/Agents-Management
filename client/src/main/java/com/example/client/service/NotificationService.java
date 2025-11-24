package com.example.client.service;

import com.example.common.dto.NotificationMessage;
import com.example.common.observer.NotificationCenter;
import com.example.common.observer.Observer;

import org.springframework.lang.NonNull;

/**
 * Servizio leggero che incapsula l'Observer condiviso per la UI.
 */
public class NotificationService {

    private final NotificationCenter<NotificationMessage> notificationCenter = new NotificationCenter<>();

    public void subscribe(@NonNull Observer<NotificationMessage> observer) {
        notificationCenter.registerObserver(observer);
    }

    public void unsubscribe(@NonNull Observer<NotificationMessage> observer) {
        notificationCenter.removeObserver(observer);
    }

    public void publish(@NonNull NotificationMessage notification) {
        notificationCenter.notifyObservers(notification);
    }

    public int observerCount() {
        return notificationCenter.getObservers().size();
    }
}
