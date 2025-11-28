package com.example.common.api;                           // Package che contiene tutti i contratti API condivisi dell’applicazione.

import java.util.List;                    // DTO che rappresenta un ruolo utente nel sistema.
import java.util.Optional;                                    // Utilizzato per restituire liste di entità.

import com.example.common.dto.RoleDTO;                                // Supporta risultati opzionali (ruolo non garantito).

/**
 * Contratto API condiviso per la gestione dei ruoli.
 * Definisce operazioni CRUD per configurare e amministrare i ruoli utente.
 */
public interface RoleApiContract {                        // Interfaccia che espone i metodi di gestione dei ruoli.

    List<RoleDTO> listRoles();                            // Restituisce l’elenco completo dei ruoli configurati nel sistema.

    Optional<RoleDTO> findById(Long id);                  // Recupera un ruolo tramite il suo identificatore, se presente.

    RoleDTO create(RoleDTO role);                         // Crea un nuovo ruolo con i dati forniti nel DTO.

    RoleDTO update(Long id,                               // Aggiorna i dati del ruolo identificato dall’ID specificato,
                    RoleDTO role);                        // sostituendo i dettagli con quelli forniti nel DTO.

    void delete(Long id);                                 // Elimina il ruolo associato all’ID passato.
}                                                         // Fine dell’interfaccia RoleApiContract.
