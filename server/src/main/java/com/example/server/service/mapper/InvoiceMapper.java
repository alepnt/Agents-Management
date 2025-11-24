package com.example.server.service.mapper; // Defines the package for invoice mapping utilities

import com.example.common.dto.InvoiceDTO; // Imports the DTO representation of an Invoice
import com.example.server.domain.InvoiceLine; // Imports the entity representation of an InvoiceLine
import com.example.server.domain.Invoice; // Imports the entity representation of an Invoice

import java.util.List; // Imports List for handling collections of invoice lines

public final class InvoiceMapper { // Utility class to convert between Invoice entity and DTO

    private InvoiceMapper() { // Private constructor to prevent instantiation
    }

    public static InvoiceDTO toDto(Invoice invoice, List<InvoiceLine> lines) { // Converts an Invoice entity and its lines to a DTO
        if (invoice == null) { // Returns null when no entity is provided
            return null; // Avoids NullPointerException during mapping
        }
        return new InvoiceDTO( // Builds the DTO using entity values
                invoice.getId(), // Maps the invoice identifier
                invoice.getNumber(), // Maps the invoice number
                invoice.getContractId(), // Maps the associated contract identifier
                invoice.getCustomerId(), // Maps the customer identifier
                invoice.getCustomerName(), // Maps the customer name
                invoice.getAmount(), // Maps the invoice amount
                invoice.getIssueDate(), // Maps the issue date
                invoice.getDueDate(), // Maps the due date
                invoice.getStatus(), // Maps the invoice status
                invoice.getPaymentDate(), // Maps the payment date
                invoice.getNotes(), // Maps any invoice notes
                lines != null ? lines.stream().map(InvoiceLineMapper::toDto).toList() : List.of() // Maps each line to its DTO or returns an empty list
        );
    }

    public static Invoice fromDto(InvoiceDTO dto) { // Converts an InvoiceDTO to the entity form
        if (dto == null) { // Returns null when no DTO is provided
            return null; // Avoids creating an entity from a null source
        }
        return new Invoice( // Builds the entity using DTO values
                dto.getId(), // Sets the invoice identifier
                dto.getContractId(), // Sets the associated contract identifier
                dto.getNumber(), // Sets the invoice number
                dto.getCustomerId(), // Sets the customer identifier
                dto.getCustomerName(), // Sets the customer name
                dto.getAmount(), // Sets the invoice amount
                dto.getIssueDate(), // Sets the issue date
                dto.getDueDate(), // Sets the due date
                dto.getStatus(), // Sets the invoice status
                dto.getPaymentDate(), // Sets the payment date
                dto.getNotes(), // Sets any invoice notes
                null, // Leaves the line collection unset
                null // Leaves the timestamps unset
        );
    }
}
