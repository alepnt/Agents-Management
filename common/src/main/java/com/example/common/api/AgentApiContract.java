package com.example.common.api;                  // Package delle API condivise tra client e server.

import java.util.List;          // DTO che rappresenta un agente nel livello di comunicazione.
import java.util.Optional;                           // Supporto per liste di oggetti.

import com.example.common.dto.AgentDTO;                       // Usato per rappresentare risultati potenzialmente assenti.

/**
 * Contratto API condiviso per la gestione degli agenti.
 * Definisce le operazioni CRUD che devono essere implementate dal server
 * e consumate dal client.
 */
public interface AgentApiContract {               // Interfaccia che espone le operazioni disponibili sugli agenti.

    List<AgentDTO> listAgents();                  // Restituisce la lista completa degli agenti presenti nel sistema.

    Optional<AgentDTO> findById(Long id);         // Recupera un agente tramite il suo identificatore, se esiste.

    AgentDTO create(AgentDTO agent);              // Crea un nuovo agente utilizzando i dati contenuti nel DTO fornito.

    AgentDTO update(Long id, AgentDTO agent);     // Aggiorna i dati dell’agente con l’ID specificato.

    void delete(Long id);                         // Elimina l’agente con l’ID specificato dal sistema.
}                                                 // Fine dell’interfaccia AgentApiContract.
