package com.example.client.command; // Package dedicato ai comandi lato client.

import com.example.client.service.BackendGateway; // Gateway REST che media le chiamate verso il backend.
import com.example.common.dto.ContractDTO; // DTO che rappresenta un contratto commerciale.
import com.example.common.dto.DocumentHistoryDTO; // DTO per elementi dello storico documentale.
import com.example.common.enums.DocumentType; // Enum per identificare la tipologia di documento (CONTRACT).
import java.util.List; // API Java per gestire liste.

/**
 * Comando che crea un nuovo contratto commerciale.
 * Implementa il pattern Command lato client e incapsula la logica di:
 * - invio al backend del nuovo contratto
 * - recupero dello storico associato dopo la creazione
 */
public class CreateContractCommand implements ClientCommand<ContractDTO> { // Il comando restituisce un ContractDTO.

    private final ContractDTO contract; // Contratto da creare, fornito dal chiamante.

    public CreateContractCommand(ContractDTO contract) { // Costruttore.
        this.contract = contract; // Memorizza il contratto da inviare al backend.
    }

    @Override
    public CommandResult<ContractDTO> execute(BackendGateway gateway) { // Esecuzione del comando.
        ContractDTO created = gateway.createContract(contract); // Invoca la POST verso il backend per creare il
                                                                // contratto.

        List<DocumentHistoryDTO> history = // Recupera lo storico aggiornato del contratto creato.
                gateway.contractHistory(created.getId());

        return CommandResult.withHistory( // Ritorna un risultato contenente:
                created, // - il contratto creato
                created.getId(), // - id del documento coinvolto
                DocumentType.CONTRACT, // - tipo documento (CONTRACT)
                history // - snapshot dello storico
        );
    }

    @Override
    public String description() { // Descrizione del comando usata dal Memento.
        return "Creazione contratto per " + contract.getCustomerName(); // Testo leggibile legato al contenuto
                                                                        // dell’operazione.
    }
} // Fine classe CreateContractCommand.
