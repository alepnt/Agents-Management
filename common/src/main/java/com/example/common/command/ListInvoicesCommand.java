package com.example.common.command;

import com.example.common.dto.InvoiceDTO;

import java.util.Collection;

import org.springframework.lang.NonNull;

/**
 * Restituisce tutte le fatture presenti nel contesto.
 */
public class ListInvoicesCommand implements Command<Collection<InvoiceDTO>> {

    @Override
    public @NonNull Collection<InvoiceDTO> execute(@NonNull CommandContext context) {
        return context.invoiceValues();
    }
}
