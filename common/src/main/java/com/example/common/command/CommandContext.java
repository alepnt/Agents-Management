package com.example.common.command;

import com.example.common.dto.ContractDTO;
import com.example.common.dto.InvoiceDTO;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contesto generico per l'esecuzione dei comandi CRUD.
 */
public class CommandContext {

    private final @NonNull Map<Long, InvoiceDTO> invoices = new ConcurrentHashMap<>();
    private final @NonNull Map<Long, ContractDTO> contracts = new ConcurrentHashMap<>();
    private final AtomicLong invoiceSequence = new AtomicLong();
    private final AtomicLong contractSequence = new AtomicLong();

    @NonNull
    public Map<Long, InvoiceDTO> getInvoices() {
        return invoices;
    }

    @NonNull
    public Map<Long, ContractDTO> getContracts() {
        return contracts;
    }

    @NonNull
    public Collection<InvoiceDTO> invoiceValues() {
        return invoices.values();
    }

    @NonNull
    public Collection<ContractDTO> contractValues() {
        return contracts.values();
    }

    public long nextInvoiceId() {
        return invoiceSequence.incrementAndGet();
    }

    public long nextContractId() {
        return contractSequence.incrementAndGet();
    }
}
