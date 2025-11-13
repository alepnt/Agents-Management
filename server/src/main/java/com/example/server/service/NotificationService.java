package com.example.server.service;

import com.example.server.domain.Notification;
import com.example.server.domain.NotificationSubscription;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.server.dto.NotificationCreateRequest;
import com.example.server.dto.NotificationResponse;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.dto.NotificationSubscriptionResponse;
import com.example.server.repository.NotificationRepository;
import com.example.server.repository.NotificationSubscriptionRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationPublisher publisher;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               TeamRepository teamRepository,
                               NotificationPublisher publisher,
                               Clock clock) {
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
        this.clock = clock;
    }

    public NotificationSubscriptionResponse subscribe(NotificationSubscribeRequest request) {
        User user = requireUser(request.userId());
        NotificationSubscription subscription = NotificationSubscription
                .create(user.getId(), request.channel(), Instant.now(clock));
        NotificationSubscription saved = subscriptionRepository.save(subscription);
        return new NotificationSubscriptionResponse(saved.getId(), saved.getUserId(), saved.getChannel(), saved.getCreatedAt());
    }

    public List<NotificationResponse> findNotifications(Long userId, Instant since) {
        User user = requireUser(userId);
        Stream<Notification> userNotifications = Optional.ofNullable(since)
                .map(instant -> notificationRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(user.getId(), instant).stream())
                .orElseGet(() -> notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream());

        Stream<Notification> teamNotifications = Optional.ofNullable(user.getTeamId())
                .map(teamId -> Optional.ofNullable(since)
                        .map(instant -> notificationRepository.findByTeamIdAndCreatedAtAfterOrderByCreatedAtDesc(teamId, instant).stream())
                        .orElseGet(() -> notificationRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream()))
                .orElse(Stream.empty());

        return Stream.concat(userNotifications, teamNotifications)
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public NotificationResponse createNotification(NotificationCreateRequest request) {
        Assert.isTrue(request.userId() != null || request.teamId() != null,
                "È necessario specificare un destinatario per la notifica");
        Assert.isTrue(!(request.userId() != null && request.teamId() != null),
                "Una notifica può essere destinata a un utente o a un team, non a entrambi");

        Notification notification;
        if (request.userId() != null) {
            User user = requireUser(request.userId());
            notification = Notification.forUser(user.getId(), request.title(), request.message(), Instant.now(clock));
        } else {
            Team team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + request.teamId()));
            notification = Notification.forTeam(team.getId(), request.title(), request.message(), Instant.now(clock));
        }

        Notification saved = notificationRepository.save(notification);
        publisher.publish(saved);
        return toResponse(saved);
    }

    public void registerSubscriber(Long userId, DeferredResult<List<NotificationResponse>> deferredResult) {
        User user = requireUser(userId);
        List<NotificationPublisher.Subscription> subscriptions = new ArrayList<>();

        var listener = new java.util.concurrent.atomic.AtomicBoolean(true);
        java.util.function.Consumer<Notification> consumer = notification -> {
            if (listener.getAndSet(false)) {
                deferredResult.setResult(List.of(toResponse(notification)));
            }
        };

        subscriptions.add(publisher.subscribeToUser(user.getId(), consumer));
        if (user.getTeamId() != null) {
            subscriptions.add(publisher.subscribeToTeam(user.getTeamId(), consumer));
        }

        Runnable cancelAction = () -> subscriptions.forEach(NotificationPublisher.Subscription::cancel);
        deferredResult.onCompletion(cancelAction);
        deferredResult.onTimeout(() -> {
            deferredResult.setResult(List.of());
            cancelAction.run();
        });
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + userId));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(),
                notification.getUserId(),
                notification.getTeamId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
