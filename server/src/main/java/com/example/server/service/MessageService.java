package com.example.server.service;

import com.example.common.dto.MessageDTO;
import com.example.server.domain.Message;
import com.example.server.repository.MessageRepository;
import com.example.server.service.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final Clock clock;

    public MessageService(MessageRepository messageRepository, Clock clock) {
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    public List<MessageDTO> findAll() {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .map(MessageMapper::toDto)
                .sorted(Comparator.comparing(MessageDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public Optional<MessageDTO> findById(Long id) {
        return messageRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(MessageMapper::toDto);
    }

    @Transactional
    public MessageDTO create(MessageDTO message) {
        MessageDTO validated = Objects.requireNonNull(message, "message must not be null");
        validate(validated);
        Message toSave = Objects.requireNonNull(Message.create(
                normalize(validated.getConversationId()),
                validated.getSenderId(),
                validated.getTeamId(),
                normalize(validated.getBody()),
                Optional.ofNullable(validated.getCreatedAt()).orElseGet(() -> Instant.now(clock))
        ), "message must not be null");
        Message saved = messageRepository.save(toSave);
        return MessageMapper.toDto(saved);
    }

    @Transactional
    public Optional<MessageDTO> update(Long id, MessageDTO message) {
        MessageDTO validated = Objects.requireNonNull(message, "message must not be null");
        validate(validated);
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return messageRepository.findById(requiredId)
                .map(existing -> {
                    Instant createdAt = Optional.ofNullable(validated.getCreatedAt()).orElse(existing.getCreatedAt());
                    Message toSave = Objects.requireNonNull(new Message(existing.getId(),
                            normalize(validated.getConversationId()),
                            validated.getSenderId(),
                            validated.getTeamId(),
                            normalize(validated.getBody()),
                            createdAt), "message must not be null");
                    Message saved = messageRepository.save(toSave);
                    return MessageMapper.toDto(saved);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return messageRepository.findById(requiredId)
                .map(existing -> {
                    Message nonNullExisting = Objects.requireNonNull(existing, "message must not be null");
                    messageRepository.delete(nonNullExisting);
                    return true;
                })
                .orElse(false);
    }

    private void validate(MessageDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getConversationId())) {
            throw new IllegalArgumentException("La conversazione è obbligatoria");
        }
        if (dto.getSenderId() == null) {
            throw new IllegalArgumentException("Il mittente è obbligatorio");
        }
        if (!StringUtils.hasText(dto.getBody())) {
            throw new IllegalArgumentException("Il corpo del messaggio è obbligatorio");
        }
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }
}
