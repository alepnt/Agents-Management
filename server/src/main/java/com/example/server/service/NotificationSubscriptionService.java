package com.example.server.service;

import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.domain.NotificationSubscription;
import com.example.server.domain.User;
import com.example.server.repository.NotificationSubscriptionRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.mapper.NotificationSubscriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class NotificationSubscriptionService {

    private final NotificationSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public NotificationSubscriptionService(NotificationSubscriptionRepository subscriptionRepository,
                                           UserRepository userRepository,
                                           Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public List<NotificationSubscriptionDTO> list(Long userId) {
        Stream<NotificationSubscription> stream = Optional.ofNullable(userId)
                .map(this::requireUser)
                .map(user -> subscriptionRepository.findByUserId(user.getId()).stream())
                .orElseGet(() -> StreamSupport.stream(subscriptionRepository.findAll().spliterator(), false));

        return stream
                .sorted(Comparator.comparing(NotificationSubscription::getCreatedAt))
                .map(NotificationSubscriptionMapper::toDto)
                .toList();
    }

    public Optional<NotificationSubscriptionDTO> findById(Long id) {
        return subscriptionRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(NotificationSubscriptionMapper::toDto);
    }

    @Transactional
    public NotificationSubscriptionDTO create(NotificationSubscriptionDTO subscription) {
        NotificationSubscriptionDTO validated = Objects.requireNonNull(subscription, "subscription must not be null");
        validate(validated);
        Long userId = requireUser(validated.getUserId()).getId();
        NotificationSubscription toSave = Objects.requireNonNull(
                NotificationSubscription.create(userId,
                        normalize(validated.getChannel()),
                        Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> Instant.now(clock))),
                "subscription must not be null");
        NotificationSubscription saved = subscriptionRepository.save(toSave);
        return NotificationSubscriptionMapper.toDto(saved);
    }

    @Transactional
    public Optional<NotificationSubscriptionDTO> update(Long id, NotificationSubscriptionDTO subscription) {
        NotificationSubscriptionDTO validated = Objects.requireNonNull(subscription, "subscription must not be null");
        validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return subscriptionRepository.findById(requiredId)
                .map(existing -> {
                    Instant createdAt = Optional.ofNullable(validated.getCreatedAt()).orElse(existing.getCreatedAt());
                    NotificationSubscription toSave = new NotificationSubscription(existing.getId(),
                            requireUser(validated.getUserId()).getId(),
                            normalize(validated.getChannel()),
                            createdAt);
                    NotificationSubscription saved = subscriptionRepository.save(toSave);
                    return NotificationSubscriptionMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return subscriptionRepository.findById(requiredId)
                .map(existing -> {
                    NotificationSubscription nonNullExisting = Objects.requireNonNull(existing,
                            "subscription must not be null");
                    subscriptionRepository.delete(nonNullExisting);
                    return true;
                })
                .orElse(false);
    }

    private void validate(NotificationSubscriptionDTO dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("L'utente è obbligatorio");
        }
        if (!StringUtils.hasText(dto.getChannel())) {
            throw new IllegalArgumentException("Il canale è obbligatorio");
        }
    }

    private User requireUser(Long userId) {
        Long requiredUserId = Objects.requireNonNull(userId, "userId must not be null");
        return userRepository.findById(requiredUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + requiredUserId));
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
