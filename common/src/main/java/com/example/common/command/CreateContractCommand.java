package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Objects;                        // DTO che rappresenta il contratto da creare.

import org.springframework.lang.NonNull;                                         // Utility per la validazione di valori non nulli.

import com.example.common.dto.ContractDTO;                          // Annotazione che richiede parametri e ritorni non null.

/**
 * Comando per la creazione di un contratto.
 * Implementa la logica CRUD in-memory tramite il CommandContext.
 */
public class CreateContractCommand implements Command<ContractDTO> { // Classe che realizza il comando di creazione contratto.

    private final ContractDTO contract;                            // Contratto da creare, validato nel costruttore.

    public CreateContractCommand(ContractDTO contract) {           // Costruttore del comando.
        this.contract = Objects.requireNonNull(contract, "contract"); 
        // Assicura che il DTO non sia null ed evita errori a runtime.
    }

    @Override
    public @NonNull ContractDTO execute(@NonNull CommandContext context) { 
        // Metodo principale del comando; riceve un contesto non nullo e restituisce il DTO creato.

        if (contract.getId() == null) {                            // Se il DTO non ha un ID assegnato, genera un nuovo identificatore.
            contract.setId(context.nextContractId());              // Assegna ID incrementale tramite il sequence interno del contesto.
        }

        context.getContracts().put(contract.getId(), contract);    // Inserisce il contratto nella mappa dei contratti.
        return contract;                                           // Restituisce il contratto creato.
    }
}                                                                   // Fine della classe CreateContractCommand.
