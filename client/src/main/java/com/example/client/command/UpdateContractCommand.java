package com.example.client.command; // Package dei comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate REST verso il backend.
import com.example.common.dto.ContractDTO; // DTO che rappresenta un contratto commerciale.
import com.example.common.dto.DocumentHistoryDTO; // DTO per le voci dello storico documentale.
import com.example.common.enums.DocumentType; // Tipo documento (CONTRACT) utilizzato nei risultati.

import java.util.List; // API Java per la gestione di liste.

/**
 * Comando che aggiorna un contratto esistente.
 * L'operazione produce uno storico documentale, che viene recuperato
 * dopo l’aggiornamento.
 */
public class UpdateContractCommand implements ClientCommand<ContractDTO> { // Il comando restituisce il contratto
                                                                           // aggiornato.

    private final Long id; // Identificativo del contratto da aggiornare.
    private final ContractDTO contract; // Dati aggiornati del contratto.

    public UpdateContractCommand(Long id, ContractDTO contract) { // Costruttore del comando.
        this.id = id; // Memorizza l'id del contratto.
        this.contract = contract; // Memorizza il DTO con le modifiche.
    }

    @Override
    public CommandResult<ContractDTO> execute(BackendGateway gateway) { // Metodo eseguito dal CommandExecutor.
        ContractDTO updated = gateway.updateContract(id, contract); // Invoca il backend per aggiornare il contratto.

        List<DocumentHistoryDTO> history = // Recupera lo storico aggiornato del contratto.
                gateway.contractHistory(updated.getId());

        return CommandResult.withHistory( // Costruisce un risultato completo di storico:
                updated, // - contratto aggiornato
                updated.getId(), // - identificativo del documento
                DocumentType.CONTRACT, // - tipo del documento coinvolto
                history // - snapshot dello storico aggiornato
        );
    }

    @Override
    public String description() { // Descrizione del comando, usata dal Memento.
        return "Aggiornamento contratto #" + id; // Etichetta testuale leggibile.
    }
} // Fine classe UpdateContractCommand.
