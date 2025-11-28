package com.example.server.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSubscriptionTest {

    @Test
    @DisplayName("equals and hashCode rely on identifier")
    void equalsAndHashCode() {
        NotificationSubscription first = new NotificationSubscription(1L, 7L, "MAIL", Instant.EPOCH);
        NotificationSubscription sameId = new NotificationSubscription(1L, 8L, "PUSH", Instant.EPOCH.plusSeconds(1));
        NotificationSubscription differentId = new NotificationSubscription(2L, 7L, "MAIL", Instant.EPOCH);

        assertThat(first).isEqualTo(sameId);
        assertThat(first).hasSameHashCodeAs(sameId);
        assertThat(first).isNotEqualTo(differentId);
    }

    @Test
    @DisplayName("Factory and withId preserve field values")
    void factoryAndWithId() {
        NotificationSubscription created = NotificationSubscription.create(3L, "MAIL", Instant.parse("2024-01-01T00:00:00Z"));
        NotificationSubscription persisted = created.withId(10L);

        assertThat(created.getId()).isNull();
        assertThat(created.getUserId()).isEqualTo(3L);
        assertThat(created.getChannel()).isEqualTo("MAIL");
        assertThat(persisted.getId()).isEqualTo(10L);
        assertThat(persisted.getCreatedAt()).isEqualTo(created.getCreatedAt());
    }
}
