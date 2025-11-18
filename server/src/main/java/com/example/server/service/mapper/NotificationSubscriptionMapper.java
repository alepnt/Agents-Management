package com.example.server.service.mapper;

import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.NotificationSubscription;

public final class NotificationSubscriptionMapper {

    private NotificationSubscriptionMapper() {
    }

    public static NotificationSubscriptionDTO toDto(NotificationSubscription subscription) {
        if (subscription == null) {
            return null;
        }
        return new NotificationSubscriptionDTO(subscription.getId(),
                subscription.getUserId(),
                subscription.getChannel(),
                subscription.getCreatedAt());
    }
}
