package com.example.server.service;

import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.NotificationSubscription;
import com.example.server.domain.User;
import com.example.server.repository.NotificationSubscriptionRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSubscriptionServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2024-02-10T08:00:00Z");

    @Mock
    private NotificationSubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    private NotificationSubscriptionService service;

    @BeforeEach
    void setUp() {
        service = new NotificationSubscriptionService(subscriptionRepository, userRepository,
                Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC));
    }

    @Test
    void shouldDefaultCreatedAtWhenMissing() {
        NotificationSubscriptionDTO payload = new NotificationSubscriptionDTO();
        payload.setUserId(17L);
        payload.setChannel("  email  ");
        payload.setCreatedAt(null);

        when(userRepository.findById(17L)).thenReturn(Optional.of(new User(17L, "azure", "user@example.com",
                "User", null, 1L, 2L, Boolean.TRUE, LocalDateTime.now())));
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> {
            NotificationSubscription toSave = invocation.getArgument(0);
            return toSave.withId(55L);
        });

        NotificationSubscriptionDTO created = service.create(payload);

        ArgumentCaptor<NotificationSubscription> stored = ArgumentCaptor.forClass(NotificationSubscription.class);
        verify(subscriptionRepository).save(stored.capture());

        assertThat(stored.getValue().getCreatedAt()).isEqualTo(FIXED_INSTANT);
        assertThat(stored.getValue().getChannel()).isEqualTo("email");
        assertThat(created.getCreatedAt()).isEqualTo(FIXED_INSTANT);
    }

    @Test
    void shouldFailWhenUserIdMissing() {
        NotificationSubscriptionDTO payload = new NotificationSubscriptionDTO();
        payload.setChannel("email");

        assertThatThrownBy(() -> service.create(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("L'utente è obbligatorio");
    }

    @Test
    void shouldListSubscriptionsForUserSortedByCreationDate() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(new User(7L, "azure", "user@example.com",
                "User", null, 1L, 2L, Boolean.TRUE, LocalDateTime.now())));
        NotificationSubscription first = new NotificationSubscription(1L, 7L, " email ", FIXED_INSTANT.minusSeconds(10));
        NotificationSubscription second = new NotificationSubscription(2L, 7L, "email", FIXED_INSTANT);
        when(subscriptionRepository.findByUserId(7L)).thenReturn(List.of(second, first));

        var results = service.list(7L);

        assertThat(results).extracting(NotificationSubscriptionDTO::getId).containsExactly(1L, 2L);
        assertThat(results).allMatch(dto -> "email".equals(dto.getChannel()));
    }

    @Test
    void shouldUpdateExistingSubscriptionAndPreserveCreatedAtWhenMissing() {
        NotificationSubscription existing = new NotificationSubscription(3L, 9L, "webhook", FIXED_INSTANT);
        NotificationSubscriptionDTO update = new NotificationSubscriptionDTO();
        update.setUserId(9L);
        update.setChannel("   webhook-updated   ");

        when(subscriptionRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(9L)).thenReturn(Optional.of(new User(9L, "azure", "mail@example.com",
                "User", null, 1L, 2L, Boolean.TRUE, LocalDateTime.now())));
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<NotificationSubscriptionDTO> result = service.update(3L, update);

        assertThat(result).isPresent();
        NotificationSubscriptionDTO dto = result.orElseThrow();
        assertThat(dto.getChannel()).isEqualTo("webhook-updated");
        assertThat(dto.getCreatedAt()).isEqualTo(FIXED_INSTANT);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingSubscription() {
        when(subscriptionRepository.findById(44L)).thenReturn(Optional.empty());

        boolean deleted = service.delete(44L);

        assertThat(deleted).isFalse();
        verify(subscriptionRepository, never()).delete(any());
    }
}
