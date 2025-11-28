package com.example.client.command; // Package dedicato ai comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che esegue le chiamate REST verso il backend.
import com.example.common.dto.InvoiceDTO; // DTO che rappresenta una fattura.

import java.util.List; // API Java per liste di elementi.

/**
 * Comando che carica l’elenco completo delle fatture dal backend.
 * L’operazione è di sola lettura, quindi non produce storico documentale.
 */
public class LoadInvoicesCommand implements ClientCommand<List<InvoiceDTO>> { // Il comando restituisce una lista di
                                                                              // InvoiceDTO.

    @Override
    public CommandResult<List<InvoiceDTO>> execute(BackendGateway gateway) { // Esecuzione del comando.
        return CommandResult.withoutHistory( // Nessuno storico per il caricamento.
                gateway.listInvoices() // Recupera tutte le fatture dal backend.
        );
    }

    @Override
    public String description() { // Descrizione testuale per il Memento.
        return "Caricamento fatture"; // Etichetta leggibile dell'operazione eseguita.
    }
} // Fine della classe LoadInvoicesCommand.
