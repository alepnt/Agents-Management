package com.example.server.service.mapper; // Defines the package for contract mapping utilities

import com.example.common.dto.ContractDTO; // Imports the DTO representation of a Contract
import com.example.server.domain.Contract; // Imports the entity representation of a Contract

public final class ContractMapper { // Utility class to convert between Contract entity and DTO

    private ContractMapper() { // Private constructor to prevent instantiation
    }

    public static ContractDTO toDto(Contract contract) { // Converts a Contract entity to its DTO form
        if (contract == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new ContractDTO( // Builds the DTO using entity values
                contract.getId(), // Maps the contract identifier
                contract.getAgentId(), // Maps the associated agent identifier
                contract.getCustomerName(), // Maps the customer name
                contract.getDescription(), // Maps the contract description
                contract.getStartDate(), // Maps the contract start date
                contract.getEndDate(), // Maps the contract end date
                contract.getTotalValue(), // Maps the total value of the contract
                contract.getStatus() // Maps the contract status
        );
    }

    public static Contract fromDto(ContractDTO dto) { // Converts a ContractDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Contract( // Builds the entity using DTO values
                dto.getId(), // Sets the contract identifier
                dto.getAgentId(), // Sets the associated agent identifier
                dto.getCustomerName(), // Sets the customer name
                dto.getDescription(), // Sets the contract description
                dto.getStartDate(), // Sets the contract start date
                dto.getEndDate(), // Sets the contract end date
                dto.getTotalValue(), // Sets the total value of the contract
                dto.getStatus() // Sets the contract status
        );
    }
}
