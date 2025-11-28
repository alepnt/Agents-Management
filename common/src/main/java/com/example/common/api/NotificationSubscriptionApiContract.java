package com.example.common.api;                                   // Package che raccoglie i contratti API condivisi dell'applicazione.

import java.util.List;       // DTO che rappresenta una sottoscrizione alle notifiche.
import java.util.Optional;                                           // Necessario per restituire liste di risultati.

import com.example.common.dto.NotificationSubscriptionDTO;                                       // Utilizzato per risultati opzionali non garantiti.

/**
 * Contratto API per la gestione delle sottoscrizioni alle notifiche.
 * Definisce operazioni CRUD e la consultazione delle sottoscrizioni associate a un utente.
 */
public interface NotificationSubscriptionApiContract {           // Interfaccia principale per la gestione delle subscription di notifica.

    List<NotificationSubscriptionDTO> listSubscriptions(         // Restituisce le sottoscrizioni associate all’utente indicato.
            Long userId                                          // Identificativo dell’utente di cui recuperare le subscription.
    );

    Optional<NotificationSubscriptionDTO> findById(Long id);     // Recupera una specifica sottoscrizione tramite il suo ID.

    NotificationSubscriptionDTO create(                          // Crea una nuova sottoscrizione notifiche
            NotificationSubscriptionDTO subscription             // partendo dai dati del DTO fornito.
    );

    NotificationSubscriptionDTO update(Long id,                  // Aggiorna la subscription identificata dall’ID,
                                       NotificationSubscriptionDTO subscription); // usando i nuovi dettagli forniti.

    void delete(Long id);                                        // Elimina la sottoscrizione associata all’ID specificato.
}                                                                 // Fine dell’interfaccia NotificationSubscriptionApiContract.
