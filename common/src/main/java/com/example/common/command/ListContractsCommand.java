package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Collection;

import org.springframework.lang.NonNull;

/**
 * Restituisce tutti i contratti presenti nel contesto.
 */
public class ListContractsCommand implements Command<Collection<ContractDTO>> {

    @Override
    public @NonNull Collection<ContractDTO> execute(@NonNull CommandContext context) {
        return context.contractValues();
    }
}
