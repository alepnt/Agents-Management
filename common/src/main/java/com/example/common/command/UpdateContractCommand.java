package com.example.common.command;

import com.example.common.dto.ContractDTO;

import java.util.Objects;
import java.util.Optional;

import org.springframework.lang.NonNull;

/**
 * Aggiorna un contratto esistente.
 */
public class UpdateContractCommand implements Command<Optional<ContractDTO>> {

    private final Long id;
    private final ContractDTO contract;

    public UpdateContractCommand(Long id, ContractDTO contract) {
        this.id = Objects.requireNonNull(id, "id");
        this.contract = Objects.requireNonNull(contract, "contract");
    }

    @Override
    public @NonNull Optional<ContractDTO> execute(@NonNull CommandContext context) {
        return Optional.ofNullable(context.getContracts().computeIfPresent(id, (key, existing) -> {
            contract.setId(id);
            return contract;
        }));
    }
}
