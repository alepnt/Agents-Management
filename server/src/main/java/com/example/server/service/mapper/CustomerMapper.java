package com.example.server.service.mapper; // Defines the package for customer mapping utilities

import com.example.common.dto.CustomerDTO; // Imports the DTO representation of a Customer
import com.example.server.domain.Customer; // Imports the entity representation of a Customer

public final class CustomerMapper { // Utility class to convert between Customer entity and DTO

    private CustomerMapper() { // Private constructor to prevent instantiation
    }

    public static CustomerDTO toDto(Customer customer) { // Converts a Customer entity to its DTO form
        if (customer == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException when mapping
        }
        return new CustomerDTO( // Builds the DTO using entity values
                customer.getId(), // Maps the customer identifier
                customer.getName(), // Maps the customer name
                customer.getVatNumber(), // Maps the VAT number
                customer.getTaxCode(), // Maps the tax code
                customer.getEmail(), // Maps the email address
                customer.getPhone(), // Maps the phone number
                customer.getAddress(), // Maps the physical address
                customer.getCreatedAt(), // Maps the creation timestamp
                customer.getUpdatedAt() // Maps the last update timestamp
        );
    }

    public static Customer fromDto(CustomerDTO dto) { // Converts a CustomerDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Customer( // Builds the entity using DTO values
                dto.getId(), // Sets the customer identifier
                dto.getName(), // Sets the customer name
                dto.getVatNumber(), // Sets the VAT number
                dto.getTaxCode(), // Sets the tax code
                dto.getEmail(), // Sets the email address
                dto.getPhone(), // Sets the phone number
                dto.getAddress(), // Sets the physical address
                dto.getCreatedAt(), // Sets the creation timestamp
                dto.getUpdatedAt() // Sets the last update timestamp
        );
    }
}
