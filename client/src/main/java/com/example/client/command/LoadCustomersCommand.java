package com.example.client.command; // Package che contiene i comandi lato client.

import com.example.client.service.BackendGateway; // Gateway responsabile delle chiamate REST verso il backend.
import com.example.common.dto.CustomerDTO; // DTO che rappresenta un cliente dell’anagrafica.

import java.util.List; // API Java per la gestione di liste di DTO.

/**
 * Comando che carica l'intera anagrafica clienti dal backend.
 * Trattandosi di un’operazione di lettura, non produce storico documentale.
 */
public class LoadCustomersCommand implements ClientCommand<List<CustomerDTO>> { // Il comando produce una lista di
                                                                                // CustomerDTO.

    @Override
    public CommandResult<List<CustomerDTO>> execute(BackendGateway gateway) { // Invocazione del comando.
        return CommandResult.withoutHistory( // Non esiste uno storico legato ai clienti.
                gateway.listCustomers() // Recupera l’elenco completo dei clienti dal backend.
        );
    }

    @Override
    public String description() { // Descrizione dell’operazione per il Memento.
        return "Caricamento anagrafica clienti"; // Testo leggibile che identifica l’azione eseguita.
    }
} // Fine classe LoadCustomersCommand.
