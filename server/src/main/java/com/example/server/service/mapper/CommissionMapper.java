package com.example.server.service.mapper; // Defines the package for commission mapping utilities

import com.example.common.dto.CommissionDTO; // Imports the DTO representation of a Commission
import com.example.server.domain.Commission; // Imports the entity representation of a Commission

public final class CommissionMapper { // Utility class to convert between Commission entity and DTO

    private CommissionMapper() { // Private constructor to prevent instantiation
    }

    public static CommissionDTO toDto(Commission commission) { // Converts a Commission entity to its DTO form
        if (commission == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new CommissionDTO( // Builds the DTO using entity values
                commission.getId(), // Maps the commission identifier
                commission.getAgentId(), // Maps the related agent identifier
                commission.getContractId(), // Maps the associated contract identifier
                commission.getTotalCommission(), // Maps the total commission amount
                commission.getPaidCommission(), // Maps the amount already paid
                commission.getPendingCommission(), // Maps the remaining amount pending
                commission.getLastUpdated() // Maps the last update timestamp
        );
    }

    public static Commission fromDto(CommissionDTO dto) { // Converts a CommissionDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Commission( // Builds the entity using DTO values
                dto.getId(), // Sets the commission identifier
                dto.getAgentId(), // Sets the related agent identifier
                dto.getContractId(), // Sets the associated contract identifier
                dto.getTotalCommission(), // Sets the total commission amount
                dto.getPaidCommission(), // Sets the amount already paid
                dto.getPendingCommission(), // Sets the remaining amount pending
                dto.getLastUpdated() // Sets the last update timestamp
        );
    }
}
