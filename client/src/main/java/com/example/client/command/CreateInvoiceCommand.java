package com.example.client.command; // Package dove risiede il pattern Command lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate HTTP al backend.
import com.example.common.dto.DocumentHistoryDTO; // DTO che rappresenta voci dello storico documentale.
import com.example.common.dto.InvoiceDTO; // DTO che rappresenta una fattura.
import com.example.common.enums.DocumentType; // Enum per identificare la tipologia di documento (INVOICE).

import java.util.List; // API Java per lavorare con liste.

/**
 * Comando responsabile della creazione di una nuova fattura.
 * Implementa il pattern Command lato client, eseguendo:
 * - invio al backend della fattura da creare
 * - recupero dello storico aggiornato relativo alla fattura creata
 */
public class CreateInvoiceCommand implements ClientCommand<InvoiceDTO> { // Il comando produce un InvoiceDTO come
                                                                         // risultato.

    private final InvoiceDTO invoice; // Fattura da creare.

    public CreateInvoiceCommand(InvoiceDTO invoice) { // Costruttore che accetta il DTO della fattura.
        this.invoice = invoice; // Memorizza il DTO per uso futuro.
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) { // Metodo principale del comando.
        InvoiceDTO created = gateway.createInvoice(invoice); // Invoca l’API per creare la nuova fattura.

        List<DocumentHistoryDTO> history = // Recupera lo storico documentale della fattura appena creata.
                gateway.invoiceHistory(created.getId());

        return CommandResult.withHistory( // Restituisce un risultato completo di storico associato.
                created, // - fattura creata
                created.getId(), // - id documento
                DocumentType.INVOICE, // - tipo documento
                history // - snapshot dello storico aggiornato
        );
    }

    @Override
    public String description() { // Descrizione del comando, usata dal Memento.
        return "Creazione fattura per " + invoice.getCustomerName(); // Testo leggibile basato sul nome cliente della
                                                                     // fattura.
    }
} // Fine classe CreateInvoiceCommand.
