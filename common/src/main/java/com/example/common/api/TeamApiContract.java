package com.example.common.api;                           // Package che contiene i contratti API condivisi a livello applicativo.

import java.util.List;                    // DTO che rappresenta un team organizzativo del sistema.
import java.util.Optional;                                    // Necessario per restituire elenchi di team.

import com.example.common.dto.TeamDTO;                                // Usato per risultati opzionali (team non garantito).

/**
 * Contratto API condiviso per la gestione dei team.
 * Definisce le operazioni CRUD fondamentali per l’amministrazione dei team aziendali.
 */
public interface TeamApiContract {                        // Interfaccia che espone le operazioni di gestione dei team.

    List<TeamDTO> listTeams();                            // Restituisce l’elenco completo dei team presenti nel sistema.

    Optional<TeamDTO> findById(Long id);                  // Recupera un team tramite il suo identificatore, se presente.

    TeamDTO create(TeamDTO team);                         // Crea un nuovo team utilizzando i dati forniti nel DTO.

    TeamDTO update(Long id,                               // Aggiorna il team identificato dall’ID fornito,
                    TeamDTO team);                        // sostituendo i dati con quelli presenti nel DTO.

    void delete(Long id);                                 // Elimina il team associato all’ID indicato.
}                                                         // Fine dell’interfaccia TeamApiContract.
