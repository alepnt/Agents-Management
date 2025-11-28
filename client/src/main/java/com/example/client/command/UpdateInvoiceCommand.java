package com.example.client.command; // Package contenente i comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che espone le operazioni REST verso il backend.
import com.example.common.dto.DocumentHistoryDTO; // DTO che rappresenta le voci dello storico documentale.
import com.example.common.dto.InvoiceDTO; // DTO che rappresenta una fattura.
import com.example.common.enums.DocumentType; // Tipo documento (INVOICE), usato nel CommandResult.

import java.util.List; // API Java per la gestione di liste.

/**
 * Comando che aggiorna una fattura esistente.
 * L’operazione genera uno storico documentale, che viene recuperato dopo
 * l’update.
 */
public class UpdateInvoiceCommand implements ClientCommand<InvoiceDTO> { // Il comando produce un InvoiceDTO aggiornato.

    private final Long id; // Identificativo della fattura da aggiornare.
    private final InvoiceDTO invoice; // DTO contenente i nuovi dati da applicare.

    public UpdateInvoiceCommand(Long id, InvoiceDTO invoice) { // Costruttore del comando.
        this.id = id; // Memorizza l’id della fattura.
        this.invoice = invoice; // Salva i dati aggiornati.
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) { // Metodo invocato durante l'esecuzione del
                                                                       // comando.
        InvoiceDTO updated = gateway.updateInvoice(id, invoice); // Chiamata REST per aggiornare la fattura.

        List<DocumentHistoryDTO> history = // Recupera lo storico aggiornato.
                gateway.invoiceHistory(updated.getId());

        return CommandResult.withHistory( // Costruisce il CommandResult con:
                updated, // - fattura aggiornata
                updated.getId(), // - id documento
                DocumentType.INVOICE, // - tipo documento (fattura)
                history // - snapshot dello storico
        );
    }

    @Override
    public String description() { // Descrizione per il Memento.
        return "Aggiornamento fattura #" + id; // Etichetta leggibile dell’operazione svolta.
    }
} // Fine classe UpdateInvoiceCommand.
