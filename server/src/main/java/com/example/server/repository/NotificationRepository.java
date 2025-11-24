// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'entità che rappresenta una notifica.
import com.example.server.domain.Notification;
// Importazione di CrudRepository per le operazioni CRUD standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che registra l'interfaccia come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione di Instant per filtrare le notifiche in base alla data di creazione.
import java.time.Instant;
// Importazione di List per restituire collezioni di notifiche ordinate.
import java.util.List;

// Annotazione che contrassegna l'interfaccia come componente di persistenza.
@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    // Recupera le notifiche di un utente ordinate dalla più recente alla meno recente.
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Recupera le notifiche di un team ordinate dalla più recente alla meno recente.
    List<Notification> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    // Recupera le notifiche di un utente successive a un istante specifico, in ordine decrescente di creazione.
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, Instant createdAfter);

    // Recupera le notifiche di un team successive a un istante specifico, in ordine decrescente di creazione.
    List<Notification> findByTeamIdAndCreatedAtAfterOrderByCreatedAtDesc(Long teamId, Instant createdAfter);
}
