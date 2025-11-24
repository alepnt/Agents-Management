package com.example.server.service.mapper; // Defines the package for message mapping utilities

import com.example.common.dto.MessageDTO; // Imports the DTO representation of a Message
import com.example.server.domain.Message; // Imports the entity representation of a Message

public final class MessageMapper { // Utility class to convert between Message entity and DTO

    private MessageMapper() { // Private constructor to prevent instantiation
    }

    public static MessageDTO toDto(Message message) { // Converts a Message entity to its DTO form
        if (message == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new MessageDTO(message.getId(), // Maps the message identifier
                message.getConversationId(), // Maps the conversation identifier
                message.getSenderId(), // Maps the sender identifier
                message.getTeamId(), // Maps the team identifier
                message.getBody(), // Maps the message body content
                message.getCreatedAt()); // Maps the creation timestamp
    }

    public static Message fromDto(MessageDTO dto) { // Converts a MessageDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Message(dto.getId(), // Sets the message identifier
                dto.getConversationId(), // Sets the conversation identifier
                dto.getSenderId(), // Sets the sender identifier
                dto.getTeamId(), // Sets the team identifier
                dto.getBody(), // Sets the message body content
                dto.getCreatedAt()); // Sets the creation timestamp
    }
}
