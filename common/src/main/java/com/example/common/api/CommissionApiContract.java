package com.example.common.api;                       // Package che contiene i contratti API condivisi tra client e server.

import java.util.List;          // DTO che rappresenta una commissione nel sistema.
import java.util.Optional;                                // Supporto per liste di elementi.

import com.example.common.dto.CommissionDTO;                            // Utilizzato per rappresentare risultati non garantiti.

/**
 * Contratto API condiviso per la gestione delle commissioni.
 * Definisce le operazioni CRUD che il backend deve implementare
 * e che il client può invocare.
 */
public interface CommissionApiContract {              // Interfaccia che espone i metodi per operare sulle commissioni.

    List<CommissionDTO> listCommissions();            // Restituisce la lista completa delle commissioni registrate.

    Optional<CommissionDTO> findById(Long id);        // Recupera una commissione tramite il suo ID, se presente.

    CommissionDTO create(CommissionDTO commissionDTO);// Crea una nuova commissione utilizzando il DTO fornito.

    CommissionDTO update(Long id,                     // Aggiorna la commissione identificata dall’ID fornito,
                         CommissionDTO commissionDTO);// sostituendone i dati con quelli nel DTO.

    void delete(Long id);                             // Elimina la commissione associata all’ID indicato.
}                                                     // Fine dell’interfaccia CommissionApiContract.
