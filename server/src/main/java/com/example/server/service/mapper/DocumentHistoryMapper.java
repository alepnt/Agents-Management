package com.example.server.service.mapper; // Defines the package for document history mapping utilities

import com.example.common.dto.DocumentHistoryDTO; // Imports the DTO representation of a DocumentHistory entry
import com.example.server.domain.DocumentHistory; // Imports the entity representation of a DocumentHistory entry

public final class DocumentHistoryMapper { // Utility class to convert between DocumentHistory entity and DTO

    private DocumentHistoryMapper() { // Private constructor to prevent instantiation
    }

    public static DocumentHistoryDTO toDto(DocumentHistory history) { // Converts a DocumentHistory entity to its DTO form
        if (history == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new DocumentHistoryDTO( // Builds the DTO using entity values
                history.getId(), // Maps the history record identifier
                history.getDocumentType(), // Maps the document type referenced
                history.getDocumentId(), // Maps the identifier of the related document
                history.getAction(), // Maps the action performed
                history.getDescription(), // Maps the description of the change
                history.getCreatedAt() // Maps the timestamp of when the record was created
        );
    }
}
