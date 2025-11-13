package com.example.server.controller;

import com.example.server.dto.ChatConversationSummary;
import com.example.server.dto.ChatMessageRequest;
import com.example.server.dto.ChatMessageResponse;
import com.example.server.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationSummary>> conversations(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(chatService.listConversations(userId));
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> messages(@RequestParam("userId") Long userId,
                                                              @RequestParam("conversationId") String conversationId,
                                                              @RequestParam(value = "since", required = false)
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                              Instant since) {
        return ResponseEntity.ok(chatService.getMessages(userId, conversationId, since));
    }

    @GetMapping("/poll")
    public DeferredResult<List<ChatMessageResponse>> poll(@RequestParam("userId") Long userId,
                                                          @RequestParam("conversationId") String conversationId) {
        DeferredResult<List<ChatMessageResponse>> deferredResult = new DeferredResult<>(30_000L);
        chatService.registerConversationListener(userId, conversationId, deferredResult);
        return deferredResult;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> send(@Valid @RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }
}
