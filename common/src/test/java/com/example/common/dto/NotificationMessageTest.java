package com.example.common.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationMessageTest {

    @Test
    void shouldExposeValues() {
        Instant timestamp = Instant.parse("2024-03-03T12:00:00Z");
        NotificationMessage message = new NotificationMessage("channel", "payload", timestamp);

        assertThat(message.getChannel()).isEqualTo("channel");
        assertThat(message.getPayload()).isEqualTo("payload");
        assertThat(message.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldRequireNonNullArguments() {
        Instant timestamp = Instant.now();
        assertThatThrownBy(() -> new NotificationMessage(null, "payload", timestamp))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("channel");
        assertThatThrownBy(() -> new NotificationMessage("channel", null, timestamp))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("payload");
        assertThatThrownBy(() -> new NotificationMessage("channel", "payload", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("timestamp");
    }
}
