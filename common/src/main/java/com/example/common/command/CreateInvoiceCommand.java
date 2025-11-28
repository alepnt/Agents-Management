package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Objects;                         // DTO che rappresenta la fattura da creare.

import org.springframework.lang.NonNull;                                         // Utility per i controlli di nullità.

import com.example.common.dto.InvoiceDTO;                          // Annotazione per indicare parametri e ritorni non null.

/**
 * Comando per la creazione di una fattura.
 * Implementa la logica CRUD in-memory sfruttando il CommandContext.
 */
public class CreateInvoiceCommand implements Command<InvoiceDTO> { // Classe che realizza il comando di creazione fattura.

    private final InvoiceDTO invoice;                             // DTO contenente i dati della fattura da creare.

    public CreateInvoiceCommand(InvoiceDTO invoice) {             // Costruttore del comando.
        this.invoice = Objects.requireNonNull(invoice, "invoice"); 
        // Validazione: la fattura non può essere null.
    }

    @Override
    public @NonNull InvoiceDTO execute(@NonNull CommandContext context) { 
        // Esegue il comando usando un contesto non nullo e restituisce la fattura creata.

        if (invoice.getId() == null) {                            // Se la fattura non ha un ID, lo genera automaticamente.
            invoice.setId(context.nextInvoiceId());               // Assegna ID incrementale tramite sequence atomica.
        }

        context.getInvoices().put(invoice.getId(), invoice);      // Inserisce la fattura nella mappa delle fatture nel contesto.
        return invoice;                                           // Restituisce il DTO della fattura creata.
    }
}                                                                   // Fine della classe CreateInvoiceCommand.
