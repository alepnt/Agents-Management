package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Objects;                        // DTO che rappresenta il contratto da aggiornare.
import java.util.Optional;                                         // Utility per controlli di nullità.

import org.springframework.lang.NonNull;                                        // Usato per restituire risultati opzionali dell’operazione.

import com.example.common.dto.ContractDTO;                          // Annotazione che impone la non-nullità dei valori.

/**
 * Aggiorna un contratto esistente.
 * Restituisce il contratto aggiornato, se presente, incapsulato in un Optional.
 */
public class UpdateContractCommand implements Command<Optional<ContractDTO>> { 
    // Comando che aggiorna un contratto già memorizzato nel contesto.

    private final Long id;                                        // Identificatore del contratto da aggiornare.
    private final ContractDTO contract;                           // Dati aggiornati del contratto.

    public UpdateContractCommand(Long id, ContractDTO contract) { // Costruttore del comando.
        this.id = Objects.requireNonNull(id, "id");               // L'ID non può essere null; validazione immediata.
        this.contract = Objects.requireNonNull(contract, "contract"); 
        // Il DTO deve essere non nullo per evitare errori durante l'aggiornamento.
    }

    @Override
    public @NonNull Optional<ContractDTO> execute(@NonNull CommandContext context) { 
        // Esegue l’aggiornamento usando un contesto non nullo.

        return Optional.ofNullable(                               
                context.getContracts().computeIfPresent(id, (key, existing) -> {
                    // computeIfPresent aggiorna solo se l’ID esiste nella mappa.

                    contract.setId(id);                           // Garantisce che l'ID del DTO sia coerente con quello passato.
                    return contract;                              // Sostituisce il contratto esistente con quello aggiornato.
                })
        );
        // Restituisce Optional.empty() se il contratto non era presente.
    }
}                                                                  // Fine della classe UpdateContractCommand.
