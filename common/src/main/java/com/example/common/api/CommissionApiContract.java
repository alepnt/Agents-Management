package com.example.common.api;

import com.example.common.dto.CommissionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione delle commissioni.
 */
public interface CommissionApiContract {

    List<CommissionDTO> listCommissions();

    Optional<CommissionDTO> findById(Long id);

    CommissionDTO create(CommissionDTO commissionDTO);

    CommissionDTO update(Long id, CommissionDTO commissionDTO);

    void delete(Long id);
}
