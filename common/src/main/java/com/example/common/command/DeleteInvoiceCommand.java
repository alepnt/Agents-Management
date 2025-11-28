package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Optional;                         // DTO che rappresenta la fattura da rimuovere.

import org.springframework.lang.NonNull;                                        // Utilizzato per restituire un valore opzionale dopo l'eliminazione.

import com.example.common.dto.InvoiceDTO;                          // Annotazione che richiede parametri e ritorni non null.

/**
 * Cancella una fattura esistente dal contesto.
 * Restituisce la fattura eliminata all'interno di un Optional.
 */
public class DeleteInvoiceCommand implements Command<Optional<InvoiceDTO>> { 
    // Comando che effettua la cancellazione di una fattura individuata per ID.

    private final Long id;                                        // Identificativo della fattura da cancellare.

    public DeleteInvoiceCommand(Long id) {                        // Costruttore del comando.
        this.id = id;                                             // Salva l'ID senza validazione (può essere null).
    }

    @Override
    public @NonNull Optional<InvoiceDTO> execute(@NonNull CommandContext context) { 
        // Esegue la cancellazione utilizzando il contesto condiviso, non nullo.

        return Optional.ofNullable(context.getInvoices().remove(id)); 
        // Rimuove la fattura dalla mappa; restituisce un Optional contenente la fattura eliminata
        // oppure Optional.empty() se non era presente.
    }
}                                                                  // Fine della classe DeleteInvoiceCommand.
