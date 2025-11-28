package com.example.common.dto; // Package dei DTO condivisi e relativi test.

import org.junit.jupiter.api.Test; // Annotazione JUnit 5 per definire un test.

import java.time.Instant; // Timestamp usato nei test.

import static org.assertj.core.api.Assertions.assertThat; // AssertJ: asserzioni fluenti.
import static org.assertj.core.api.Assertions.assertThatThrownBy; // AssertJ: verifica eccezioni.

/**
 * Test di unità per NotificationMessage.
 * Verifica l’esposizione dei valori e la validazione dei parametri non null.
 */
class NotificationMessageTest {

    @Test
    void shouldExposeValues() { // Verifica che i getter restituiscano i valori forniti.
        Instant timestamp = Instant.parse("2024-03-03T12:00:00Z"); // Timestamp di esempio.
        NotificationMessage message = new NotificationMessage("channel", "payload", timestamp);
        // Istanza completa con valori noti.

        assertThat(message.getChannel()).isEqualTo("channel"); // Getter channel.
        assertThat(message.getPayload()).isEqualTo("payload"); // Getter payload.
        assertThat(message.getTimestamp()).isEqualTo(timestamp); // Getter timestamp.
    }

    @Test
    void shouldRequireNonNullArguments() { // Verifica la validazione dei parametri null.
        Instant timestamp = Instant.now(); // Timestamp valido.

        assertThatThrownBy(() -> new NotificationMessage(null, "payload", timestamp))
                .isInstanceOf(NullPointerException.class) // Deve lanciare NPE.
                .hasMessageContaining("channel"); // Con messaggio riferito al parametro null.

        assertThatThrownBy(() -> new NotificationMessage("channel", null, timestamp))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("payload");

        assertThatThrownBy(() -> new NotificationMessage("channel", "payload", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("timestamp");
    }
} // Fine test NotificationMessageTest.
