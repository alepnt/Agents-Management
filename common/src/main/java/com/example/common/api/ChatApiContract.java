package com.example.common.api;

import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.List;

/**
 * Contratto API per la messaggistica interna.
 */
public interface ChatApiContract {

    List<ChatConversationDTO> conversations(Long userId);

    List<ChatMessageDTO> messages(Long userId, String conversationId, Instant since);

    DeferredResult<List<ChatMessageDTO>> poll(Long userId, String conversationId);

    ChatMessageDTO send(ChatMessageRequest request);
}
