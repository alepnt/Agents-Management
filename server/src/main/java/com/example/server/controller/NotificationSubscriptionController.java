package com.example.server.controller;

import com.example.common.api.NotificationSubscriptionApiContract;
import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.service.NotificationSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notification-subscriptions")
public class NotificationSubscriptionController implements NotificationSubscriptionApiContract {

    private final NotificationSubscriptionService subscriptionService;

    public NotificationSubscriptionController(NotificationSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    @GetMapping
    public List<NotificationSubscriptionDTO> listSubscriptions(@RequestParam(value = "userId", required = false) Long userId) {
        return subscriptionService.list(userId);
    }

    @Override
    @GetMapping("/{id}")
    public Optional<NotificationSubscriptionDTO> findById(@PathVariable Long id) {
        return subscriptionService.findById(id);
    }

    @Override
    @PostMapping
    public NotificationSubscriptionDTO create(@RequestBody NotificationSubscriptionDTO subscription) {
        return subscriptionService.create(subscription);
    }

    @Override
    @PutMapping("/{id}")
    public NotificationSubscriptionDTO update(@PathVariable Long id, @RequestBody NotificationSubscriptionDTO subscription) {
        return subscriptionService.update(id, subscription)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sottoscrizione non trovata"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = subscriptionService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sottoscrizione non trovata");
        }
    }
}
