package com.example.server.controller;

import com.example.server.dto.NotificationCreateRequest;
import com.example.server.dto.NotificationResponse;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.dto.NotificationSubscriptionResponse;
import com.example.server.service.NotificationService;
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
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestParam("userId") Long userId,
                                                                       @RequestParam(value = "since", required = false)
                                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                       Instant since) {
        return ResponseEntity.ok(notificationService.findNotifications(userId, since));
    }

    @GetMapping("/subscribe")
    public DeferredResult<List<NotificationResponse>> subscribe(@RequestParam("userId") Long userId) {
        DeferredResult<List<NotificationResponse>> deferredResult = new DeferredResult<>(30_000L);
        notificationService.registerSubscriber(userId, deferredResult);
        return deferredResult;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> publish(@Valid @RequestBody NotificationCreateRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<NotificationSubscriptionResponse> registerChannel(@Valid @RequestBody NotificationSubscribeRequest request) {
        return ResponseEntity.ok(notificationService.subscribe(request));
    }
}
