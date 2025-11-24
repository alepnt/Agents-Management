package com.example.server.service;

import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import com.example.server.domain.Message;
import com.example.server.domain.User;
import com.example.server.repository.MessageRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    private static final Instant NOW = Instant.parse("2024-02-01T10:15:30Z");

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatPublisher chatPublisher;

    private ChatService service;

    @BeforeEach
    void setUp() {
        service = new ChatService(messageRepository, userRepository, chatPublisher, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void shouldListOnlyAccessibleConversationsOrderedByActivity() {
        User user = userWithTeam(1L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        Message teamMessage = new Message(1L, "team:1", 2L, 1L, "Hello team", NOW);
        Message personalOld = new Message(2L, "direct:4", 4L, null, "older", NOW.minus(1, ChronoUnit.HOURS));
        Message personalNew = new Message(3L, "direct:4", 4L, null, "newer", NOW.minusSeconds(10));
        Message otherTeam = new Message(4L, "team:2", 5L, 2L, "secret", NOW);
        when(messageRepository.findAll()).thenReturn(List.of(teamMessage, personalOld, personalNew, otherTeam));

        var conversations = service.listConversations(7L);

        assertThat(conversations)
                .extracting(ChatConversationDTO::conversationId, ChatConversationDTO::lastMessagePreview)
                .containsExactly(
                        org.assertj.core.api.Assertions.tuple("team:1", "Hello team"),
                        org.assertj.core.api.Assertions.tuple("direct:4", "newer")
                );
        assertThat(conversations.getFirst().lastActivity()).isAfterOrEqualTo(conversations.get(1).lastActivity());
    }

    @Test
    void shouldRetrieveMessagesSinceTimestamp() {
        User user = userWithTeam(null);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        List<Message> messages = List.of(new Message(1L, "conv", 3L, null, "body", NOW));
        when(messageRepository.findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc("conv", NOW.minusSeconds(60)))
                .thenReturn(messages);

        var result = service.getMessages(3L, "conv", NOW.minusSeconds(60));

        assertThat(result).extracting(ChatMessageDTO::body).containsExactly("body");
    }

    @Test
    void shouldSendMessageAndPublishNotification() {
        User sender = userWithTeam(null);
        when(userRepository.findById(8L)).thenReturn(Optional.of(sender));
        Message saved = new Message(10L, "conv", 8L, null, "payload", NOW);
        when(messageRepository.save(any())).thenReturn(saved);

        ChatMessageDTO response = service.sendMessage(new ChatMessageRequest(8L, "conv", "payload"));

        ArgumentCaptor<Message> persisted = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(persisted.capture());
        assertThat(persisted.getValue().getBody()).isEqualTo("payload");
        verify(chatPublisher).publish(response);
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    void shouldRejectUnauthorizedTeamConversation() {
        User user = userWithTeam(1L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.getMessages(2L, "team:9", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Accesso alla conversazione negato");
    }

    @Test
    void shouldCompleteDeferredResultOnPublish() {
        User user = userWithTeam(5L);
        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        AtomicReference<Consumer<ChatMessageDTO>> listener = new AtomicReference<>();
        when(chatPublisher.subscribe(eq("team:5"), any())).thenAnswer(invocation -> {
            listener.set(invocation.getArgument(1));
            return () -> { };
        });
        DeferredResult<List<ChatMessageDTO>> deferredResult = new DeferredResult<>();

        service.registerConversationListener(12L, "team:5", deferredResult);
        ChatMessageDTO message = new ChatMessageDTO(1L, "team:5", 12L, 5L, "hi", NOW);
        listener.get().accept(message);

        assertThat(deferredResult.getResult()).isEqualTo(List.of(message));
    }

    @Test
    void shouldFailOnUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listConversations(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Utente non trovato: 99");
        verify(messageRepository, never()).findAll();
    }

    private User userWithTeam(Long teamId) {
        return new User(1L, "az", "mail", "name", null, null, teamId, true, null);
    }
}
