package com.example.server.controller;

import com.example.common.api.NotificationApiContract;
import com.example.common.dto.NotificationDTO;
import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApiContract {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    @GetMapping
    public List<NotificationDTO> listNotifications(@RequestParam("userId") Long userId,
                                                   @RequestParam(value = "since", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   Instant since) {
        return notificationService.findNotifications(userId, since);
    }

    @GetMapping("/subscribe")
    public DeferredResult<List<NotificationDTO>> subscribe(@RequestParam("userId") Long userId) {
        DeferredResult<List<NotificationDTO>> deferredResult = new DeferredResult<>(30_000L);
        notificationService.registerSubscriber(userId, deferredResult);
        return deferredResult;
    }

    @Override
    @PostMapping
    public NotificationDTO create(@Valid @RequestBody NotificationDTO request) {
        return notificationService.createNotification(request);
    }

    @Override
    @PutMapping("/{id}")
    public NotificationDTO update(@PathVariable Long id, @Valid @RequestBody NotificationDTO request) {
        return notificationService.updateNotification(id, request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifica non trovata"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping("/subscribe")
    public NotificationSubscriptionDTO registerChannel(@Valid @RequestBody NotificationSubscribeRequest request) {
        return notificationService.subscribe(request);
    }
}
