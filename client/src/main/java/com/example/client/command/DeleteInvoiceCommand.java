package com.example.client.command; // Package che contiene tutte le implementazioni del pattern Command lato client.

import com.example.client.service.BackendGateway; // Gateway REST che espone i servizi del backend.
import com.example.common.dto.DocumentHistoryDTO; // DTO che rappresenta singole voci dello storico documentale.
import com.example.common.enums.DocumentType; // Enum che identifica il tipo di documento (INVOICE).

import java.util.List; // API Java per la gestione di liste.

/**
 * Comando per eliminare una fattura esistente.
 * Dopo l’eliminazione recupera lo storico documentale aggiornato.
 */
public class DeleteInvoiceCommand implements ClientCommand<Void> { // Il comando non produce un payload → tipo di
                                                                   // ritorno Void.

    private final Long id; // Identificativo della fattura da eliminare.

    public DeleteInvoiceCommand(Long id) { // Costruttore del comando.
        this.id = id; // Salva l'id della fattura da eliminare.
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) { // Metodo invocato dal CommandExecutor.
        gateway.deleteInvoice(id); // Invoca il backend per eliminare la fattura.

        List<DocumentHistoryDTO> history = // Recupera lo storico aggiornato del documento eliminato.
                gateway.invoiceHistory(id);

        return CommandResult.withHistory( // Crea un CommandResult includendo:
                null, // - nessun valore restituito
                id, // - id della fattura coinvolta
                DocumentType.INVOICE, // - tipo documento
                history // - snapshot dello storico
        );
    }

    @Override
    public String description() { // Descrizione leggibile del comando.
        return "Eliminazione fattura #" + id; // Utilizzata per i memento nella history.
    }
} // Fine classe DeleteInvoiceCommand.
