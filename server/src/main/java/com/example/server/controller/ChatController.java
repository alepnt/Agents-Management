package com.example.server.controller;

import com.example.common.api.ChatApiContract;
import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import com.example.server.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController implements ChatApiContract {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    @GetMapping("/conversations")
    public List<ChatConversationDTO> conversations(@RequestParam("userId") Long userId) {
        return chatService.listConversations(userId);
    }

    @Override
    @GetMapping("/messages")
    public List<ChatMessageDTO> messages(@RequestParam("userId") Long userId,
                                         @RequestParam("conversationId") String conversationId,
                                         @RequestParam(value = "since", required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         Instant since) {
        return chatService.getMessages(userId, conversationId, since);
    }

    @Override
    @GetMapping("/poll")
    public DeferredResult<List<ChatMessageDTO>> poll(@RequestParam("userId") Long userId,
                                                    @RequestParam("conversationId") String conversationId) {
        DeferredResult<List<ChatMessageDTO>> deferredResult = new DeferredResult<>(30_000L);
        chatService.registerConversationListener(userId, conversationId, deferredResult);
        return deferredResult;
    }

    @Override
    @PostMapping("/messages")
    public ChatMessageDTO send(@Valid @RequestBody ChatMessageRequest request) {
        return chatService.sendMessage(request);
    }
}
