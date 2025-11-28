package com.example.common.api;                         // Package che contiene i contratti API condivisi del progetto.

import java.util.List;              // DTO che rappresenta un contratto nel sistema.
import java.util.Optional;       // DTO che rappresenta una voce dello storico dei documenti associati.

import com.example.common.dto.ContractDTO;                                  // Necessario per operazioni che restituiscono collezioni.
import com.example.common.dto.DocumentHistoryDTO;                              // Supporta la gestione di risultati non garantiti.

/**
 * Contratto API condiviso per la gestione dei contratti.
 * Contiene tutte le operazioni CRUD e le interrogazioni
 * relative allo storico documentale dei contratti.
 */
public interface ContractApiContract {                  // Interfaccia che definisce il set di operazioni disponibili lato contratto.

    List<ContractDTO> listContracts();                  // Restituisce l'elenco completo dei contratti presenti nel sistema.

    Optional<ContractDTO> findById(Long id);            // Recupera un contratto tramite il suo ID, se esiste.

    ContractDTO create(ContractDTO contractDTO);        // Crea un nuovo contratto con i dati forniti nel DTO.

    ContractDTO update(Long id,                         // Aggiorna il contratto identificato dall'ID,
                        ContractDTO contractDTO);       // sovrascrivendo i dati con quelli del DTO passato.

    void delete(Long id);                               // Elimina definitivamente il contratto associato all'ID indicato.

    List<DocumentHistoryDTO> history(Long id);          // Restituisce lo storico documentale collegato al contratto indicato.
}                                                       // Fine dell’interfaccia ContractApiContract.
