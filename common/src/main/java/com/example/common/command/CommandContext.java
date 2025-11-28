package com.example.common.command;                                  // Package che contiene le classi del pattern Command condivise tra moduli.

import java.util.Collection;                           // DTO che rappresenta un contratto nel contesto dei comandi.
import java.util.Map;                            // DTO che rappresenta una fattura nel contesto dei comandi.
import java.util.concurrent.ConcurrentHashMap;                             // Annotazione per indicare valori obbligatori non null.
import java.util.concurrent.atomic.AtomicLong;                                         // Usata per restituire raccolte di DTO.

import org.springframework.lang.NonNull;                                                // Interfaccia base per strutture chiave-valore.

import com.example.common.dto.ContractDTO;                       // Implementazione thread-safe di Map.
import com.example.common.dto.InvoiceDTO;                       // Contatore atomico per generare ID incrementali.

/**
 * Contesto generico per l'esecuzione dei comandi CRUD.
 * Contiene store in-memory e generatori di ID, pensati per un ambiente isolato o di test.
 */
public class CommandContext {                                         // Classe che rappresenta lo stato condiviso fra i comandi.

    private final @NonNull Map<Long, InvoiceDTO> invoices = new ConcurrentHashMap<>();
    // Mappa thread-safe che contiene tutte le fatture indicizzate per ID.

    private final @NonNull Map<Long, ContractDTO> contracts = new ConcurrentHashMap<>();
    // Mappa thread-safe che contiene tutti i contratti indicizzati per ID.

    private final AtomicLong invoiceSequence = new AtomicLong();      
    // Generatore atomico di identificatori per le fatture.

    private final AtomicLong contractSequence = new AtomicLong();
    // Generatore atomico di identificatori per i contratti.

    @NonNull
    public Map<Long, InvoiceDTO> getInvoices() {                      // Restituisce la mappa completa delle fatture.
        return invoices;
    }

    @NonNull
    public Map<Long, ContractDTO> getContracts() {                    // Restituisce la mappa completa dei contratti.
        return contracts;
    }

    @NonNull
    public Collection<InvoiceDTO> invoiceValues() {                   // Restituisce la collection delle fatture senza le chiavi.
        return invoices.values();
    }

    @NonNull
    public Collection<ContractDTO> contractValues() {                 // Restituisce la collection dei contratti senza le chiavi.
        return contracts.values();
    }

    public long nextInvoiceId() {                                     // Restituisce un nuovo ID progressivo per le fatture.
        return invoiceSequence.incrementAndGet();
    }

    public long nextContractId() {                                    // Restituisce un nuovo ID progressivo per i contratti.
        return contractSequence.incrementAndGet();
    }
}                                                                      // Fine della classe CommandContext.
