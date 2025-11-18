package com.example.common.api;

import com.example.common.dto.NotificationDTO;

import java.time.Instant;
import java.util.List;

public interface NotificationApiContract {

    List<NotificationDTO> listNotifications(Long userId, Instant since);

    NotificationDTO create(NotificationDTO notification);

    NotificationDTO update(Long id, NotificationDTO notification);

    void delete(Long id);
}
