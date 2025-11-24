// Dichiarazione del package che contiene le interfacce repository.
package com.example.server.repository;

// Importazione dell'entità che rappresenta una riga di fattura.
import com.example.server.domain.InvoiceLine;
// Importazione di CrudRepository per fornire operazioni CRUD standard.
import org.springframework.data.repository.CrudRepository;
// Importazione dell'annotazione che definisce il bean come repository Spring.
import org.springframework.stereotype.Repository;

// Importazione di List per restituire collezioni ordinate di righe fattura.
import java.util.List;

// Annotazione che registra l'interfaccia come repository Spring.
@Repository
public interface InvoiceLineRepository extends CrudRepository<InvoiceLine, Long> {

    // Restituisce le righe di una fattura specifica ordinate per id.
    List<InvoiceLine> findByInvoiceIdOrderById(Long invoiceId);

    // Elimina tutte le righe collegate a una specifica fattura.
    void deleteByInvoiceId(Long invoiceId);
}
