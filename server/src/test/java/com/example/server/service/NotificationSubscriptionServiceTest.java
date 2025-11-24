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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
}
