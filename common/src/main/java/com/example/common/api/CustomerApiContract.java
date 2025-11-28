package com.example.common.api;                        // Package che contiene i contratti API condivisi tra client e server.

import java.util.List;             // DTO che rappresenta un cliente nell’anagrafica del sistema.
import java.util.Optional;                                 // Usato per restituire liste di risultati.

import com.example.common.dto.CustomerDTO;                             // Usato per rappresentare risultati opzionali (entità non certa).

/**
 * Contratto API condiviso per la gestione dell'anagrafica clienti.
 * Definisce le operazioni CRUD fondamentali per la gestione dei clienti.
 */
public interface CustomerApiContract {                 // Interfaccia che espone le operazioni di gestione clienti.

    List<CustomerDTO> listCustomers();                 // Restituisce la lista completa dei clienti presenti nell’anagrafica.

    Optional<CustomerDTO> findById(Long id);           // Recupera un cliente tramite identificatore, se presente.

    CustomerDTO create(CustomerDTO customer);          // Crea un nuovo cliente utilizzando i dati forniti nel DTO.

    CustomerDTO update(Long id, CustomerDTO customer); // Aggiorna i dati del cliente con l’ID specificato.

    void delete(Long id);                              // Elimina il cliente associato all’ID indicato.
}                                                      // Fine dell’interfaccia CustomerApiContract.
