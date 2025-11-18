package com.example.common.api;

import com.example.common.dto.NotificationSubscriptionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API per la gestione delle sottoscrizioni alle notifiche.
 */
public interface NotificationSubscriptionApiContract {

    List<NotificationSubscriptionDTO> listSubscriptions(Long userId);

    Optional<NotificationSubscriptionDTO> findById(Long id);

    NotificationSubscriptionDTO create(NotificationSubscriptionDTO subscription);

    NotificationSubscriptionDTO update(Long id, NotificationSubscriptionDTO subscription);

    void delete(Long id);
}
