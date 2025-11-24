package com.example.server.service;

import com.example.common.dto.MessageDTO;
import com.example.server.domain.Message;
import com.example.server.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2024-05-01T10:15:30Z");

    @Mock
    private MessageRepository messageRepository;

    private MessageService service;

    @BeforeEach
    void setUp() {
        service = new MessageService(messageRepository, Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC));
    }

    @Test
    void shouldFillMissingCreatedAtWhenCreatingMessage() {
        MessageDTO payload = new MessageDTO();
        payload.setConversationId("  chat-1  ");
        payload.setSenderId(7L);
        payload.setTeamId(3L);
        payload.setBody("  Hello world  ");
        payload.setCreatedAt(null);

        when(messageRepository.save(any())).thenAnswer(invocation -> {
            Message toSave = invocation.getArgument(0);
            return toSave.withId(99L);
        });

        MessageDTO created = service.create(payload);

        ArgumentCaptor<Message> stored = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(stored.capture());

        assertThat(stored.getValue().getCreatedAt()).isEqualTo(FIXED_INSTANT);
        assertThat(stored.getValue().getConversationId()).isEqualTo("chat-1");
        assertThat(stored.getValue().getBody()).isEqualTo("Hello world");
        assertThat(created.getCreatedAt()).isEqualTo(FIXED_INSTANT);
    }

    @Test
    void shouldRejectMissingConversation() {
        MessageDTO payload = new MessageDTO();
        payload.setSenderId(1L);
        payload.setBody("missing conversation");

        assertThatThrownBy(() -> service.create(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La conversazione è obbligatoria");
    }
}
