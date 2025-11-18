package com.example.server.service.mapper;

import com.example.common.dto.MessageDTO;
import com.example.server.domain.Message;

public final class MessageMapper {

    private MessageMapper() {
    }

    public static MessageDTO toDto(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDTO(message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getTeamId(),
                message.getBody(),
                message.getCreatedAt());
    }

    public static Message fromDto(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Message(dto.getId(),
                dto.getConversationId(),
                dto.getSenderId(),
                dto.getTeamId(),
                dto.getBody(),
                dto.getCreatedAt());
    }
}
