package com.example.common.api;                               // Package che contiene i contratti API condivisi tra client e server.

import java.time.Instant;                // DTO che rappresenta una singola notifica applicativa.
import java.util.List;                                     // Timestamp utilizzato per filtrare notifiche più recenti di un istante.

import com.example.common.dto.NotificationDTO;                                        // Utilizzato per restituire collezioni di notifiche.

/**
 * Contratto API per la gestione delle notifiche utente.
 * Espone operazioni CRUD e un endpoint per ottenere le notifiche più recenti.
 */
public interface NotificationApiContract {                    // Interfaccia che definisce le operazioni sulle notifiche.

    List<NotificationDTO> listNotifications(                  // Restituisce l’elenco delle notifiche relative all’utente indicato.
            Long userId,                                      // Identificativo dell’utente a cui appartengono le notifiche.
            Instant since                                      // Restituisce solo le notifiche successive a questo timestamp.
    );

    NotificationDTO create(NotificationDTO notification);     // Crea una nuova notifica con i dati forniti nel DTO.

    NotificationDTO update(Long id,                           // Aggiorna la notifica corrispondente all'ID indicato,
                           NotificationDTO notification);     // usando i dati presenti nel DTO.

    void delete(Long id);                                     // Elimina definitivamente la notifica con l’ID specificato.
}                                                             // Fine dell’interfaccia NotificationApiContract.
