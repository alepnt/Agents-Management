package com.example.server.service;

import com.example.common.dto.NotificationDTO;
import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.Notification;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.repository.NotificationRepository;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final NotificationPublisher publisher;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationSubscriptionService subscriptionService,
                               UserRepository userRepository,
                               TeamRepository teamRepository,
                               NotificationPublisher publisher,
                               Clock clock) {
        this.notificationRepository = notificationRepository;
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
        this.clock = clock;
    }

    public NotificationSubscriptionDTO subscribe(NotificationSubscribeRequest request) {
        NotificationSubscribeRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        NotificationSubscriptionDTO dto = new NotificationSubscriptionDTO();
        dto.setUserId(requiredRequest.userId());
        dto.setChannel(requiredRequest.channel());
        dto.setCreatedAt(Instant.now(clock));
        return subscriptionService.create(dto);
    }

    public List<NotificationDTO> findNotifications(Long userId, Instant since) {
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
                .map(this::toDto)
                .toList();
    }

    public NotificationDTO createNotification(NotificationDTO request) {
        NotificationDTO requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Assert.hasText(requiredRequest.getTitle(), "Il titolo è obbligatorio");
        Assert.hasText(requiredRequest.getMessage(), "Il messaggio è obbligatorio");
        Assert.isTrue(requiredRequest.getUserId() != null || requiredRequest.getTeamId() != null,
                "È necessario specificare un destinatario per la notifica");
        Assert.isTrue(!(requiredRequest.getUserId() != null && requiredRequest.getTeamId() != null),
                "Una notifica può essere destinata a un utente o a un team, non a entrambi");

        Notification notification;
        if (requiredRequest.getUserId() != null) {
            User user = requireUser(requiredRequest.getUserId());
            notification = Objects.requireNonNull(Notification.forUser(user.getId(), requiredRequest.getTitle(),
                    requiredRequest.getMessage(), Instant.now(clock)), "notification must not be null");
        } else {
            Team team = teamRepository.findById(requiredRequest.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + requiredRequest.getTeamId()));
            notification = Objects.requireNonNull(Notification.forTeam(team.getId(), requiredRequest.getTitle(),
                    requiredRequest.getMessage(), Instant.now(clock)), "notification must not be null");
        }

        Notification saved = notificationRepository.save(notification);
        publisher.publish(saved);
        return toDto(saved);
    }

    public Optional<NotificationDTO> updateNotification(Long id, NotificationDTO request) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        NotificationDTO requiredRequest = Objects.requireNonNull(request, "notification must not be null");

        return notificationRepository.findById(requiredId)
                .map(existing -> {
                    Assert.isTrue(existing.getUserId() != null || existing.getTeamId() != null,
                            "La notifica deve avere un destinatario");

                    String title = Optional.ofNullable(requiredRequest.getTitle()).orElse(existing.getTitle());
                    String message = Optional.ofNullable(requiredRequest.getMessage()).orElse(existing.getMessage());
                    Assert.hasText(title, "Il titolo è obbligatorio");
                    Assert.hasText(message, "Il messaggio è obbligatorio");

                    Notification updated = existing
                            .withTitle(title)
                            .withMessage(message)
                            .withRead(Optional.ofNullable(requiredRequest.getRead()).orElse(existing.isRead()));

                    Notification saved = notificationRepository.save(updated);
                    return toDto(saved);
                });
    }

    public void deleteNotification(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        if (!notificationRepository.existsById(requiredId)) {
            throw new IllegalArgumentException("Notifica non trovata: " + requiredId);
        }
        notificationRepository.deleteById(requiredId);
    }

    public void registerSubscriber(Long userId, DeferredResult<List<NotificationDTO>> deferredResult) {
        DeferredResult<List<NotificationDTO>> requiredDeferredResult = Objects.requireNonNull(deferredResult,
                "deferredResult must not be null");
        User user = requireUser(userId);
        List<NotificationPublisher.Subscription> subscriptions = new ArrayList<>();

        var listener = new java.util.concurrent.atomic.AtomicBoolean(true);
        java.util.function.Consumer<Notification> consumer = notification -> {
            if (listener.getAndSet(false)) {
                NotificationDTO response = toDto(Objects.requireNonNull(notification, "notification must not be null"));
                requiredDeferredResult.setResult(List.of(response));
            }
        };

        subscriptions.add(publisher.subscribeToUser(user.getId(), consumer));
        if (user.getTeamId() != null) {
            subscriptions.add(publisher.subscribeToTeam(user.getTeamId(), consumer));
        }

        Runnable cancelAction = () -> subscriptions.forEach(NotificationPublisher.Subscription::cancel);
        requiredDeferredResult.onCompletion(cancelAction);
        requiredDeferredResult.onTimeout(() -> {
            List<NotificationDTO> emptyResult = List.of();
            requiredDeferredResult.setResult(emptyResult);
            cancelAction.run();
        });
    }

    private User requireUser(Long userId) {
        Long requiredUserId = Objects.requireNonNull(userId, "userId must not be null");
        return userRepository.findById(requiredUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredUserId));
    }

    private NotificationDTO toDto(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setTeamId(notification.getTeamId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
