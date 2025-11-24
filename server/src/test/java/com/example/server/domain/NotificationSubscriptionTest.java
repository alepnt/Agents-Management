package com.example.server.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSubscriptionTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createShouldOmitIdBeforePersistence() {
        Instant createdAt = Instant.parse("2024-04-04T10:00:00Z");
        NotificationSubscription subscription = NotificationSubscription.create(10L, "EMAIL", createdAt);

        assertThat(subscription.getId()).isNull();
        assertThat(subscription.getUserId()).isEqualTo(10L);
        assertThat(subscription.getChannel()).isEqualTo("EMAIL");
        assertThat(subscription.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldValidateMandatoryFields() {
        NotificationSubscription invalid = new NotificationSubscription(1L, null, " ", Instant.now());

        Set<ConstraintViolation<NotificationSubscription>> violations = validator.validate(invalid);

        assertThat(violations).extracting("message")
                .containsExactlyInAnyOrder("L'utente è obbligatorio", "Il canale di notifica è obbligatorio");
    }

    @Test
    void utilityMethodsShouldUseIdentifierAndReadableToString() {
        NotificationSubscription first = new NotificationSubscription(1L, 5L, "EMAIL", Instant.now());
        NotificationSubscription second = new NotificationSubscription(1L, 6L, "PUSH", Instant.now());
        NotificationSubscription third = new NotificationSubscription(2L, 5L, "EMAIL", Instant.now());

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(third);
        assertThat(first.toString()).contains("NotificationSubscription{", "id=1", "channel='EMAIL'");
    }
}
