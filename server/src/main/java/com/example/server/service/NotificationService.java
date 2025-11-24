package com.example.server.service; // Package for service layer classes

import com.example.common.dto.NotificationDTO; // DTO for notifications
import com.example.common.dto.NotificationSubscriptionDTO; // DTO for notification subscriptions
import com.example.server.domain.Notification; // Domain entity for notifications
import com.example.server.domain.Team; // Domain entity for teams
import com.example.server.domain.User; // Domain entity for users
import com.example.server.dto.NotificationSubscribeRequest; // Request object for subscriptions
import com.example.server.repository.NotificationRepository; // Repository for notifications
import com.example.server.repository.TeamRepository; // Repository for teams
import com.example.server.repository.UserRepository; // Repository for users
import org.springframework.stereotype.Service; // Annotation marking a service component
import org.springframework.util.Assert; // Utility for assertions
import org.springframework.web.context.request.async.DeferredResult; // Async result holder for long polling

import java.time.Clock; // Clock abstraction
import java.time.Instant; // Instant timestamp
import java.util.ArrayList; // ArrayList implementation
import java.util.Comparator; // Comparator utility
import java.util.List; // List interface
import java.util.Objects; // Utility for null checks
import java.util.Optional; // Optional wrapper
import java.util.stream.Stream; // Stream API

@Service // Marks the class as a Spring service
public class NotificationService { // Service managing notifications

    private final NotificationRepository notificationRepository; // Repository dependency for notifications
    private final NotificationSubscriptionService subscriptionService; // Service managing subscriptions
    private final UserRepository userRepository; // Repository for users
    private final TeamRepository teamRepository; // Repository for teams
    private final NotificationPublisher publisher; // Publisher to push notifications
    private final Clock clock; // Clock used for timestamps

    public NotificationService(NotificationRepository notificationRepository, // Constructor injecting notification repository
                               NotificationSubscriptionService subscriptionService, // Constructor injecting subscription service
                               UserRepository userRepository, // Constructor injecting user repository
                               TeamRepository teamRepository, // Constructor injecting team repository
                               NotificationPublisher publisher, // Constructor injecting publisher
                               Clock clock) { // Constructor injecting clock
        this.notificationRepository = notificationRepository; // Assign notification repository
        this.subscriptionService = subscriptionService; // Assign subscription service
        this.userRepository = userRepository; // Assign user repository
        this.teamRepository = teamRepository; // Assign team repository
        this.publisher = publisher; // Assign publisher
        this.clock = clock; // Assign clock
    } // End constructor

    public NotificationSubscriptionDTO subscribe(NotificationSubscribeRequest request) { // Create a subscription
        NotificationSubscribeRequest requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Validate request
        NotificationSubscriptionDTO dto = new NotificationSubscriptionDTO(); // Instantiate DTO
        dto.setUserId(requiredRequest.userId()); // Set user id
        dto.setChannel(requiredRequest.channel()); // Set channel
        dto.setCreatedAt(Instant.now(clock)); // Set creation timestamp
        return subscriptionService.create(dto); // Persist subscription
    } // End subscribe

    public List<NotificationDTO> findNotifications(Long userId, Instant since) { // Fetch notifications for user and team
        User user = requireUser(userId); // Ensure user exists
        Stream<Notification> userNotifications = Optional.ofNullable(since) // Handle optional since timestamp
                .map(instant -> notificationRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(user.getId(), instant).stream()) // Stream user notifications after timestamp
                .orElseGet(() -> notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()); // Otherwise all user notifications

        Stream<Notification> teamNotifications = Optional.ofNullable(user.getTeamId()) // Handle optional team id
                .map(teamId -> Optional.ofNullable(since) // Nested optional for timestamp
                        .map(instant -> notificationRepository.findByTeamIdAndCreatedAtAfterOrderByCreatedAtDesc(teamId, instant).stream()) // Stream team notifications after timestamp
                        .orElseGet(() -> notificationRepository.findByTeamIdOrderByCreatedAtDesc(teamId).stream())) // Otherwise all team notifications
                .orElse(Stream.empty()); // If no team, empty stream

        return Stream.concat(userNotifications, teamNotifications) // Merge streams
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed()) // Sort by creation date descending
                .map(this::toDto) // Map to DTOs
                .toList(); // Collect to list
    } // End findNotifications

    public NotificationDTO createNotification(NotificationDTO request) { // Create a new notification
        NotificationDTO requiredRequest = Objects.requireNonNull(request, "request must not be null"); // Validate request
        Assert.hasText(requiredRequest.getTitle(), "Il titolo è obbligatorio"); // Ensure title present
        Assert.hasText(requiredRequest.getMessage(), "Il messaggio è obbligatorio"); // Ensure message present
        Assert.isTrue(requiredRequest.getUserId() != null || requiredRequest.getTeamId() != null, // Ensure recipient specified
                "È necessario specificare un destinatario per la notifica"); // Error message
        Assert.isTrue(!(requiredRequest.getUserId() != null && requiredRequest.getTeamId() != null), // Ensure single recipient type
                "Una notifica può essere destinata a un utente o a un team, non a entrambi"); // Error message

        Notification notification; // Placeholder for constructed notification
        if (requiredRequest.getUserId() != null) { // Targeting a user
            User user = requireUser(requiredRequest.getUserId()); // Ensure user exists
            notification = Objects.requireNonNull(Notification.forUser(user.getId(), requiredRequest.getTitle(), // Create user notification
                    requiredRequest.getMessage(), Instant.now(clock)), "notification must not be null"); // Validate creation
        } else { // Targeting a team
            Long teamId = Objects.requireNonNull(requiredRequest.getTeamId(), "teamId must not be null"); // Validate team id
            Team team = teamRepository.findById(teamId) // Retrieve team
                    .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + requiredRequest.getTeamId())); // Error if missing
            notification = Objects.requireNonNull(Notification.forTeam(team.getId(), requiredRequest.getTitle(), // Create team notification
                    requiredRequest.getMessage(), Instant.now(clock)), "notification must not be null"); // Validate creation
        } // End recipient selection

        Notification saved = Objects.requireNonNull(notificationRepository.save(notification), "notification must not be null"); // Persist notification
        publisher.publish(saved); // Publish to listeners
        return toDto(saved); // Return DTO
    } // End createNotification

    public Optional<NotificationDTO> updateNotification(Long id, NotificationDTO request) { // Update an existing notification
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        NotificationDTO requiredRequest = Objects.requireNonNull(request, "notification must not be null"); // Validate request

        return notificationRepository.findById(requiredId) // Find notification
                .map(existing -> { // Map when found
                    Assert.isTrue(existing.getUserId() != null || existing.getTeamId() != null, // Ensure recipient exists
                            "La notifica deve avere un destinatario"); // Error message

                    String title = Optional.ofNullable(requiredRequest.getTitle()).orElse(existing.getTitle()); // Determine title
                    String message = Optional.ofNullable(requiredRequest.getMessage()).orElse(existing.getMessage()); // Determine message
                    Assert.hasText(title, "Il titolo è obbligatorio"); // Validate title
                    Assert.hasText(message, "Il messaggio è obbligatorio"); // Validate message

                    Notification updated = Objects.requireNonNull(existing // Start from existing notification
                            .withTitle(title) // Update title
                            .withMessage(message) // Update message
                            .withRead(Optional.ofNullable(requiredRequest.getRead()).orElse(existing.isRead())), // Update read flag
                            "notification must not be null"); // Validate result

                    Notification saved = Objects.requireNonNull(notificationRepository.save(updated), // Save changes
                            "notification must not be null"); // Validate save result
                    return toDto(saved); // Return DTO
                }); // End mapping
    } // End updateNotification

    public void deleteNotification(Long id) { // Delete notification by id
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        if (!notificationRepository.existsById(requiredId)) { // Check existence
            throw new IllegalArgumentException("Notifica non trovata: " + requiredId); // Throw if missing
        } // End existence check
        notificationRepository.deleteById(requiredId); // Delete notification
    } // End deleteNotification

    public void registerSubscriber(Long userId, DeferredResult<List<NotificationDTO>> deferredResult) { // Register long-polling subscriber
        DeferredResult<List<NotificationDTO>> requiredDeferredResult = Objects.requireNonNull(deferredResult, // Validate deferred result
                "deferredResult must not be null"); // Error message
        User user = requireUser(userId); // Ensure user exists
        List<NotificationPublisher.Subscription> subscriptions = new ArrayList<>(); // Track created subscriptions

        var listener = new java.util.concurrent.atomic.AtomicBoolean(true); // Flag to ensure single response
        java.util.function.Consumer<Notification> consumer = notification -> { // Consumer invoked on notification
            if (listener.getAndSet(false)) { // Respond only once
                NotificationDTO response = toDto(Objects.requireNonNull(notification, "notification must not be null")); // Map notification to DTO
                List<NotificationDTO> result = Objects.requireNonNull(List.of(response), "notification list must not be null"); // Build single-element list
                requiredDeferredResult.setResult(result); // Complete deferred result
            } // End first-notification check
        }; // End consumer definition

        subscriptions.add(publisher.subscribeToUser(user.getId(), consumer)); // Subscribe to user notifications
        if (user.getTeamId() != null) { // If user belongs to a team
            subscriptions.add(publisher.subscribeToTeam(user.getTeamId(), consumer)); // Subscribe to team notifications
        } // End team subscription check

        Runnable cancelAction = () -> subscriptions.forEach(NotificationPublisher.Subscription::cancel); // Action to cancel subscriptions
        requiredDeferredResult.onCompletion(cancelAction); // Cancel on completion
        requiredDeferredResult.onTimeout(() -> { // On timeout
            List<NotificationDTO> emptyResult = Objects.requireNonNull(List.of(), "result list must not be null"); // Empty response
            requiredDeferredResult.setResult(emptyResult); // Complete with empty list
            cancelAction.run(); // Cancel subscriptions
        }); // End timeout handler
    } // End registerSubscriber

    private User requireUser(Long userId) { // Ensure user exists by id
        Long requiredUserId = Objects.requireNonNull(userId, "userId must not be null"); // Validate id
        return userRepository.findById(requiredUserId) // Find user
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredUserId)); // Throw if missing
    } // End requireUser

    private NotificationDTO toDto(Notification notification) { // Map Notification entity to DTO
        NotificationDTO dto = new NotificationDTO(); // Create DTO
        dto.setId(notification.getId()); // Set id
        dto.setUserId(notification.getUserId()); // Set user id
        dto.setTeamId(notification.getTeamId()); // Set team id
        dto.setTitle(notification.getTitle()); // Set title
        dto.setMessage(notification.getMessage()); // Set message
        dto.setRead(notification.isRead()); // Set read flag
        dto.setCreatedAt(notification.getCreatedAt()); // Set creation time
        return dto; // Return DTO
    } // End toDto
} // End NotificationService class
