package com.example.server.service.mapper; // Defines the package for invoice line mapping utilities

import com.example.common.dto.InvoiceLineDTO; // Imports the DTO representation of an InvoiceLine
import com.example.server.domain.InvoiceLine; // Imports the entity representation of an InvoiceLine

public final class InvoiceLineMapper { // Utility class to convert between InvoiceLine entity and DTO

    private InvoiceLineMapper() { // Private constructor to prevent instantiation
    }

    public static InvoiceLineDTO toDto(InvoiceLine line) { // Converts an InvoiceLine entity to its DTO form
        if (line == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new InvoiceLineDTO( // Builds the DTO using entity values
                line.getId(), // Maps the invoice line identifier
                line.getInvoiceId(), // Maps the parent invoice identifier
                line.getArticleId(), // Maps the linked article identifier
                line.getArticleCode(), // Maps the article code
                line.getDescription(), // Maps the line description
                line.getQuantity(), // Maps the quantity sold
                line.getUnitPrice(), // Maps the price per unit
                line.getVatRate(), // Maps the VAT rate applied
                line.getTotal() // Maps the total line amount
        );
    }

    public static InvoiceLine fromDto(Long invoiceId, InvoiceLineDTO dto) { // Converts a DTO to an entity while injecting an invoice id
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new InvoiceLine( // Builds the entity using DTO values and provided invoice id
                dto.getId(), // Sets the invoice line identifier
                invoiceId, // Overrides the invoice id with the provided value
                dto.getArticleId(), // Sets the linked article identifier
                dto.getArticleCode(), // Sets the article code
                dto.getDescription(), // Sets the line description
                dto.getQuantity(), // Sets the quantity sold
                dto.getUnitPrice(), // Sets the price per unit
                dto.getVatRate(), // Sets the VAT rate applied
                dto.getTotal() // Sets the total line amount
        );
    }

    public static InvoiceLine fromDto(InvoiceLineDTO dto) { // Converts an InvoiceLineDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new InvoiceLine( // Builds the entity using DTO values
                dto.getId(), // Sets the invoice line identifier
                dto.getInvoiceId(), // Sets the parent invoice identifier
                dto.getArticleId(), // Sets the linked article identifier
                dto.getArticleCode(), // Sets the article code
                dto.getDescription(), // Sets the line description
                dto.getQuantity(), // Sets the quantity sold
                dto.getUnitPrice(), // Sets the price per unit
                dto.getVatRate(), // Sets the VAT rate applied
                dto.getTotal() // Sets the total line amount
        );
    }
}
