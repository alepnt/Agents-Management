package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Optional;                        // DTO che rappresenta il contratto da eliminare.

import org.springframework.lang.NonNull;                                        // Usato per restituire un valore opzionale dopo la cancellazione.

import com.example.common.dto.ContractDTO;                          // Annotazione che indica parametri e ritorni non null.

/**
 * Cancella un contratto esistente dal contesto.
 * Restituisce il contratto rimosso incapsulato in un Optional.
 */
public class DeleteContractCommand implements Command<Optional<ContractDTO>> { 
    // Comando che rimuove un contratto e ritorna il risultato opzionale.

    private final Long id;                                        // Identificatore del contratto da eliminare.

    public DeleteContractCommand(Long id) {                       // Costruttore che riceve l'ID del contratto da cancellare.
        this.id = id;                                             // Assegna il valore senza validazione (potrebbe essere null).
    }

    @Override
    public @NonNull Optional<ContractDTO> execute(@NonNull CommandContext context) { 
        // Esegue la cancellazione usando il contesto non nullo.

        return Optional.ofNullable(context.getContracts().remove(id)); 
        // Rimuove il contratto dalla mappa; 
        // se esiste, lo restituisce dentro un Optional, altrimenti Optional.empty().
    }
}                                                                  // Fine della classe DeleteContractCommand.
