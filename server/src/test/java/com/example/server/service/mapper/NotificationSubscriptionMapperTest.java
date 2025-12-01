package com.example.server.service.mapper;

import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.NotificationSubscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSubscriptionMapperTest {

    @Test
    @DisplayName("toDto maps all fields")
    void toDtoMapsFields() {
        NotificationSubscription entity = new NotificationSubscription(4L, 12L, "PUSH", Instant.parse("2024-02-02T10:00:00Z"));

        NotificationSubscriptionDTO dto = NotificationSubscriptionMapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getUserId()).isEqualTo(12L);
        assertThat(dto.getChannel()).isEqualTo("PUSH");
        assertThat(dto.getCreatedAt()).isEqualTo(Instant.parse("2024-02-02T10:00:00Z"));
    }

    @Test
    @DisplayName("toDto returns null when input is null")
    void toDtoHandlesNull() {
        NotificationSubscriptionDTO dto = NotificationSubscriptionMapper.toDto(null);

        assertThat(dto).isNull();
    }
}
