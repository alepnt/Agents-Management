// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'enum che identifica il tipo di documento.
import com.example.common.enums.DocumentType;
// Importazione dell'entità che rappresenta la cronologia dei documenti.
import com.example.server.domain.DocumentHistory;
// Importazione di CrudRepository per le operazioni di persistenza standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che contrassegna il componente come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione della classe List per restituire collezioni ordinate.
import java.util.List;

// Annotazione che registra l'interfaccia come bean di repository.
@Repository
public interface DocumentHistoryRepository extends CrudRepository<DocumentHistory, Long> {

    // Recupera la cronologia di un documento filtrando per tipo e id, ordinata dalla più recente.
    List<DocumentHistory> findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(DocumentType documentType, Long documentId);
}
