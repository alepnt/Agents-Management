package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Objects;                         // DTO che rappresenta la fattura da aggiornare.
import java.util.Optional;                                         // Utility per validazioni di non-nullità.

import org.springframework.lang.NonNull;                                        // Usato per restituire un risultato opzionale.

import com.example.common.dto.InvoiceDTO;                          // Annotazione che impone parametri e ritorni non null.

/**
 * Aggiorna una fattura esistente.
 * Restituisce la fattura aggiornata, se presente, incapsulata in un Optional.
 */
public class UpdateInvoiceCommand implements Command<Optional<InvoiceDTO>> { 
    // Comando che applica la logica di aggiornamento su fatture esistenti.

    private final Long id;                                        // Identificatore della fattura da aggiornare.
    private final InvoiceDTO invoice;                             // Nuovi dati della fattura.

    public UpdateInvoiceCommand(Long id, InvoiceDTO invoice) {    // Costruttore del comando.
        this.id = Objects.requireNonNull(id, "id");               // Controllo: l'ID non può essere null.
        this.invoice = Objects.requireNonNull(invoice, "invoice"); 
        // Controllo: il DTO non può essere null.
    }

    @Override
    public @NonNull Optional<InvoiceDTO> execute(@NonNull CommandContext context) { 
        // Applica l’aggiornamento usando un contesto non nullo.

        return Optional.ofNullable(
                context.getInvoices().computeIfPresent(id, (key, existing) -> {
                    // computeIfPresent aggiorna solo se la fattura esiste nella mappa.

                    invoice.setId(id);                            // Garantisce che l’ID del DTO coincida con quello richiesto.
                    return invoice;                               // Sostituisce la fattura esistente con quella aggiornata.
                })
        );
        // Se la fattura non esiste, viene restituito Optional.empty().
    }
}                                                                  // Fine della classe UpdateInvoiceCommand.
