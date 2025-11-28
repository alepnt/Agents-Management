package com.example.client.command; // Package che contiene i comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che esegue le chiamate REST al backend.
import com.example.common.dto.DocumentHistoryDTO; // DTO che rappresenta una voce dello storico documentale.
import com.example.common.enums.DocumentType; // Enum che identifica la tipologia di documento (CONTRACT).

import java.util.List; // API Java per lavorare con liste.

/**
 * Comando che elimina un contratto esistente.
 * Dopo l'eliminazione recupera lo storico aggiornato relativo al documento.
 */
public class DeleteContractCommand implements ClientCommand<Void> { // Il comando non ritorna dati → tipo Void.

    private final Long id; // Identificativo del contratto da eliminare.

    public DeleteContractCommand(Long id) { // Costruttore del comando.
        this.id = id; // Memorizza l'id fornito dal chiamante.
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) { // Esecuzione dell'operazione.
        gateway.deleteContract(id); // Invoca il backend per eliminare il contratto.

        List<DocumentHistoryDTO> history = // Recupera lo storico aggiornato del contratto eliminato.
                gateway.contractHistory(id);

        return CommandResult.withHistory( // Restituisce un risultato con storico documentale.
                null, // Nessun payload → valore null.
                id, // Id del contratto coinvolto.
                DocumentType.CONTRACT, // Tipo di documento.
                history // Snapshot dello storico aggiornato.
        );
    }

    @Override
    public String description() { // Descrizione testuale usata nel Memento.
        return "Eliminazione contratto #" + id; // Messaggio leggibile contenente l'ID del contratto.
    }
} // Fine classe DeleteContractCommand.
