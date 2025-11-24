// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'entità che rappresenta un messaggio di conversazione.
import com.example.server.domain.Message;
// Importazione di CrudRepository per le operazioni CRUD standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che registra il bean come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione di Instant per filtrare i messaggi in base a un timestamp.
import java.time.Instant;
// Importazione di List per restituire collezioni di messaggi ordinate.
import java.util.List;

// Annotazione che dichiara l'interfaccia come repository Spring.
@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    // Recupera i messaggi di una conversazione ordinati dal più vecchio al più recente.
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    // Recupera i messaggi di una conversazione creati dopo un certo istante, ordinati in modo crescente.
    List<Message> findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(String conversationId, Instant createdAfter);
}
