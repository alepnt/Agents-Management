package com.example.server.service.mapper; // Defines the package for notification subscription mapping utilities

import com.example.common.dto.NotificationSubscriptionDTO; // Imports the DTO representation of a notification subscription
import com.example.server.domain.NotificationSubscription; // Imports the entity representation of a notification subscription

public final class NotificationSubscriptionMapper { // Utility class to convert between NotificationSubscription entity and DTO

    private NotificationSubscriptionMapper() { // Private constructor to prevent instantiation
    }

    public static NotificationSubscriptionDTO toDto(NotificationSubscription subscription) { // Converts a NotificationSubscription entity to its DTO form
        if (subscription == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new NotificationSubscriptionDTO(subscription.getId(), // Maps the subscription identifier
                subscription.getUserId(), // Maps the associated user identifier
                subscription.getChannel(), // Maps the notification channel
                subscription.getCreatedAt()); // Maps the subscription creation timestamp
    }
}
