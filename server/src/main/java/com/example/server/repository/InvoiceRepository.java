// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'entità che rappresenta una fattura.
import com.example.server.domain.Invoice;
// Importazione di CrudRepository per fornire operazioni CRUD standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che registra il bean come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione di List per restituire collezioni di fatture ordinate.
import java.util.List;

// Annotazione che definisce l'interfaccia come repository Spring.
@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

    // Recupera tutte le fatture ordinate in modo decrescente per data di emissione.
    List<Invoice> findAllByOrderByIssueDateDesc();
}
