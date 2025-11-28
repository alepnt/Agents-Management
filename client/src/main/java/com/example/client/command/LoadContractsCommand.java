package com.example.client.command; // Package del pattern Command lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate HTTP verso il backend.
import com.example.common.dto.ContractDTO; // DTO che rappresenta un contratto commerciale.

import java.util.List; // API Java per liste di oggetti.

/**
 * Comando che richiede al backend l’elenco completo dei contratti.
 * Non include storico documentale, perché si tratta di un’operazione di sola
 * lettura.
 */
public class LoadContractsCommand implements ClientCommand<List<ContractDTO>> { // Il comando restituisce una lista di
                                                                                // ContractDTO.

    @Override
    public CommandResult<List<ContractDTO>> execute(BackendGateway gateway) { // Esecuzione del comando.
        return CommandResult.withoutHistory( // Nessuno storico → usa factory senza history.
                gateway.listContracts() // Recupera dal backend tutti i contratti.
        );
    }

    @Override
    public String description() { // Descrizione utilizzata per il Memento.
        return "Caricamento contratti"; // Testo leggibile della specifica operazione.
    }
} // Fine classe LoadContractsCommand.
