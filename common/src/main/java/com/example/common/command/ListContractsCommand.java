package com.example.common.command;                               // Package che contiene le implementazioni del pattern Command.

import java.util.Collection;                        // DTO che rappresenta un contratto registrato nel contesto.

import org.springframework.lang.NonNull;                                      // Tipo restituito per collezioni di contratti.

import com.example.common.dto.ContractDTO;                          // Annotazione che impone la non-nullità dei ritorni e parametri.

/**
 * Restituisce tutti i contratti presenti nel contesto.
 * Comando read-only che non modifica lo stato del CommandContext.
 */
public class ListContractsCommand implements Command<Collection<ContractDTO>> { 
    // Comando che produce la collezione di contratti correnti.

    @Override
    public @NonNull Collection<ContractDTO> execute(@NonNull CommandContext context) { 
        // Esegue il comando utilizzando un contesto valido e non nullo.

        return context.contractValues();                           // Restituisce la collection di tutti i contratti memorizzati.
    }
}                                                                  // Fine della classe ListContractsCommand.
