// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'entità che rappresenta una sottoscrizione alle notifiche.
import com.example.server.domain.NotificationSubscription;
// Importazione di CrudRepository per le operazioni CRUD standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che registra il bean come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione di List per restituire collezioni di sottoscrizioni.
import java.util.List;

// Annotazione che contrassegna l'interfaccia come repository Spring.
@Repository
public interface NotificationSubscriptionRepository extends CrudRepository<NotificationSubscription, Long> {

    // Recupera tutte le sottoscrizioni appartenenti a un utente specifico.
    List<NotificationSubscription> findByUserId(Long userId);
}
