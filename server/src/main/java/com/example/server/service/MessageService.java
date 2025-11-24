package com.example.server.service; // Package declaration for server services

import com.example.common.dto.MessageDTO; // DTO representing a message
import com.example.server.domain.Message; // Domain entity for messages
import com.example.server.repository.MessageRepository; // Repository interface for messages
import com.example.server.service.mapper.MessageMapper; // Mapper between Message and DTO
import org.springframework.stereotype.Service; // Annotation to mark service components
import org.springframework.transaction.annotation.Transactional; // Annotation for transactional behavior
import org.springframework.util.StringUtils; // Utility for string checks

import java.time.Clock; // Clock abstraction for time
import java.time.Instant; // Instant timestamp
import java.util.Comparator; // Comparator utility
import java.util.List; // List interface
import java.util.Objects; // Utility for null checks
import java.util.Optional; // Optional wrapper
import java.util.stream.StreamSupport; // Stream support for Iterable

@Service // Marks the class as a Spring service
public class MessageService { // Service handling message operations

    private final MessageRepository messageRepository; // Repository dependency
    private final Clock clock; // Clock used for timestamps

    public MessageService(MessageRepository messageRepository, Clock clock) { // Constructor injecting dependencies
        this.messageRepository = messageRepository; // Assign message repository
        this.clock = clock; // Assign clock
    } // End constructor

    public List<MessageDTO> findAll() { // Retrieve all messages
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false) // Stream over all messages
                .map(MessageMapper::toDto) // Convert to DTOs
                .sorted(Comparator.comparing(MessageDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))) // Sort by creation date
                .toList(); // Collect to list
    } // End findAll

    public Optional<MessageDTO> findById(Long id) { // Find message by id
        return messageRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Look up by id
                .map(MessageMapper::toDto); // Map to DTO
    } // End findById

    @Transactional // Run within transaction
    public MessageDTO create(MessageDTO message) { // Create a new message
        MessageDTO validated = Objects.requireNonNull(message, "message must not be null"); // Ensure DTO not null
        validate(validated); // Validate required fields
        Message toSave = Objects.requireNonNull(Message.create( // Build message entity
                normalize(validated.getConversationId()), // Normalized conversation id
                validated.getSenderId(), // Sender id
                validated.getTeamId(), // Team id
                normalize(validated.getBody()), // Normalized body text
                Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> Instant.now(clock)) // Creation timestamp
        ), "message must not be null"); // Ensure entity not null
        Message saved = messageRepository.save(toSave); // Persist message
        return MessageMapper.toDto(saved); // Convert saved entity to DTO
    } // End create

    @Transactional // Run within transaction
    public Optional<MessageDTO> update(Long id, MessageDTO message) { // Update an existing message
        MessageDTO validated = Objects.requireNonNull(message, "message must not be null"); // Ensure DTO not null
        validate(validated); // Validate fields
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        return messageRepository.findById(requiredId) // Find existing message
                .map(existing -> { // Map when found
                    Instant createdAt = Optional.ofNullable(validated.getCreatedAt()).orElse(existing.getCreatedAt()); // Preserve or set created date
                    Message toSave = Objects.requireNonNull(new Message(existing.getId(), // Build updated entity
                            normalize(validated.getConversationId()), // Conversation id normalized
                            validated.getSenderId(), // Sender id
                            validated.getTeamId(), // Team id
                            normalize(validated.getBody()), // Body normalized
                            createdAt), "message must not be null"); // Validate entity
                    Message saved = messageRepository.save(toSave); // Persist changes
                    return MessageMapper.toDto(saved); // Return DTO
                }); // End mapping
    } // End update

    @Transactional // Run within transaction
    public boolean delete(Long id) { // Delete message by id
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        return messageRepository.findById(requiredId) // Find message
                .map(existing -> { // If found
                    Message nonNullExisting = Objects.requireNonNull(existing, "message must not be null"); // Ensure entity not null
                    messageRepository.delete(nonNullExisting); // Delete entity
                    return true; // Indicate success
                }) // End map
                .orElse(false); // Return false if not found
    } // End delete

    private void validate(MessageDTO dto) { // Validate message fields
        if (dto == null || !StringUtils.hasText(dto.getConversationId())) { // Check conversation id
            throw new IllegalArgumentException("La conversazione è obbligatoria"); // Throw error if missing
        } // End conversation check
        if (dto.getSenderId() == null) { // Check sender
            throw new IllegalArgumentException("Il mittente è obbligatorio"); // Throw error if missing
        } // End sender check
        if (!StringUtils.hasText(dto.getBody())) { // Check body
            throw new IllegalArgumentException("Il corpo del messaggio è obbligatorio"); // Throw error if missing
        } // End body check
    } // End validate

    private String normalize(String value) { // Normalize string values
        return value != null ? value.trim() : null; // Trim or return null
    } // End normalize
} // End MessageService class
