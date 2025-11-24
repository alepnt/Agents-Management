package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Optional;

import org.springframework.lang.NonNull;

/**
 * Cancella un contratto esistente dal contesto.
 */
public class DeleteContractCommand implements Command<Optional<ContractDTO>> {

    private final Long id;

    public DeleteContractCommand(Long id) {
        this.id = id;
    }

    @Override
    public @NonNull Optional<ContractDTO> execute(@NonNull CommandContext context) {
        return Optional.ofNullable(context.getContracts().remove(id));
    }
}
