package com.example.server.service; // Package for service layer components

import com.example.common.dto.NotificationSubscriptionDTO; // DTO representing a notification subscription
import com.example.server.domain.NotificationSubscription; // Domain entity for notification subscriptions
import com.example.server.domain.User; // Domain entity for users
import com.example.server.repository.NotificationSubscriptionRepository; // Repository for subscriptions
import com.example.server.repository.UserRepository; // Repository for users
import com.example.server.service.mapper.NotificationSubscriptionMapper; // Mapper between entity and DTO
import org.springframework.stereotype.Service; // Annotation marking a service
import org.springframework.transaction.annotation.Transactional; // Annotation for transactional behavior
import org.springframework.util.StringUtils; // Utility for string checks

import java.time.Clock; // Clock abstraction
import java.time.Instant; // Instant timestamp
import java.util.Comparator; // Comparator utility
import java.util.List; // List interface
import java.util.Objects; // Utility for null checks
import java.util.Optional; // Optional wrapper
import java.util.stream.Stream; // Stream API
import java.util.stream.StreamSupport; // Support for streams from Iterable

@Service // Marks the class as a Spring service
public class NotificationSubscriptionService { // Service managing notification subscriptions

    private final NotificationSubscriptionRepository subscriptionRepository; // Repository dependency
    private final UserRepository userRepository; // Repository for users
    private final Clock clock; // Clock used for timestamps

    public NotificationSubscriptionService(NotificationSubscriptionRepository subscriptionRepository, // Constructor injecting repository
                                           UserRepository userRepository, // Constructor injecting user repository
                                           Clock clock) { // Constructor injecting clock
        this.subscriptionRepository = subscriptionRepository; // Assign subscription repository
        this.userRepository = userRepository; // Assign user repository
        this.clock = clock; // Assign clock
    } // End constructor

    public List<NotificationSubscriptionDTO> list(Long userId) { // List subscriptions optionally filtered by user
        Stream<NotificationSubscription> stream = Optional.ofNullable(userId) // Optional user filter
                .map(this::requireUser) // Ensure user exists
                .map(user -> subscriptionRepository.findByUserId(user.getId()).stream()) // Stream user subscriptions
                .orElseGet(() -> StreamSupport.stream(subscriptionRepository.findAll().spliterator(), false)); // Otherwise stream all subscriptions

        return stream // Stream of subscriptions
                .sorted(Comparator.comparing(NotificationSubscription::getCreatedAt)) // Sort by creation time
                .map(NotificationSubscriptionMapper::toDto) // Map to DTOs
                .map(this::normalizeChannel) // Normalize channel value
                .toList(); // Collect to list
    } // End list

    public Optional<NotificationSubscriptionDTO> findById(Long id) { // Find subscription by id
        return subscriptionRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Lookup by id
                .map(NotificationSubscriptionMapper::toDto); // Map to DTO
    } // End findById

    @Transactional // Execute within transaction
    public NotificationSubscriptionDTO create(NotificationSubscriptionDTO subscription) { // Create a new subscription
        NotificationSubscriptionDTO validated = Objects.requireNonNull(subscription, "subscription must not be null"); // Validate DTO
        validate(validated); // Validate fields
        Long userId = requireUser(validated.getUserId()).getId(); // Ensure user exists and get id
        NotificationSubscription toSave = Objects.requireNonNull( // Build subscription entity
                NotificationSubscription.create(userId, // Target user id
                        normalize(validated.getChannel()), // Normalized channel
                        Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> Instant.now(clock))), // Creation timestamp
                "subscription must not be null"); // Ensure entity not null
        NotificationSubscription saved = subscriptionRepository.save(toSave); // Persist subscription
        return NotificationSubscriptionMapper.toDto(saved); // Return DTO
    } // End create

    @Transactional // Execute within transaction
    public Optional<NotificationSubscriptionDTO> update(Long id, NotificationSubscriptionDTO subscription) { // Update subscription
        NotificationSubscriptionDTO validated = Objects.requireNonNull(subscription, "subscription must not be null"); // Validate DTO
        validate(validated); // Validate fields
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        return subscriptionRepository.findById(requiredId) // Find existing subscription
                .map(existing -> { // Map when found
                    Instant createdAt = Optional.ofNullable(validated.getCreatedAt()).orElse(existing.getCreatedAt()); // Determine creation time
                    NotificationSubscription toSave = new NotificationSubscription(existing.getId(), // Preserve id
                            requireUser(validated.getUserId()).getId(), // Validate and set user id
                            normalize(validated.getChannel()), // Normalize channel
                            createdAt); // Set creation time
                    NotificationSubscription saved = subscriptionRepository.save(toSave); // Persist changes
                    return NotificationSubscriptionMapper.toDto(saved); // Return DTO
                }); // End mapping
    } // End update

    @Transactional // Execute within transaction
    public boolean delete(Long id) { // Delete subscription by id
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        return subscriptionRepository.findById(requiredId) // Find subscription
                .map(existing -> { // If found
                    NotificationSubscription nonNullExisting = Objects.requireNonNull(existing, // Ensure entity not null
                            "subscription must not be null"); // Error message
                    subscriptionRepository.delete(nonNullExisting); // Delete entity
                    return true; // Indicate success
                }) // End map
                .orElse(false); // Return false if not found
    } // End delete

    private void validate(NotificationSubscriptionDTO dto) { // Validate subscription DTO
        if (dto.getUserId() == null) { // User required
            throw new IllegalArgumentException("L'utente è obbligatorio"); // Throw error
        } // End user check
        if (!StringUtils.hasText(dto.getChannel())) { // Channel required
            throw new IllegalArgumentException("Il canale è obbligatorio"); // Throw error
        } // End channel check
    } // End validate

    private User requireUser(Long userId) { // Ensure user exists
        Long requiredUserId = Objects.requireNonNull(userId, "userId must not be null"); // Validate id
        return userRepository.findById(requiredUserId) // Find user
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredUserId)); // Throw if missing
    } // End requireUser

    private String normalize(String value) { // Normalize strings
        return value != null ? value.trim() : null; // Trim or return null
    } // End normalize

    private NotificationSubscriptionDTO normalizeChannel(NotificationSubscriptionDTO dto) { // Normalize channel on DTO
        if (dto == null) { // Handle null DTO
            return null; // Preserve null
        } // End null check
        dto.setChannel(normalize(dto.getChannel())); // Trim channel value
        return dto; // Return normalized DTO
    } // End normalizeChannel
} // End NotificationSubscriptionService class
