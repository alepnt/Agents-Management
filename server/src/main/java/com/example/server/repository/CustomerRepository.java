package com.example.server.repository; // Package che raccoglie i repository Spring del server.

import com.example.server.domain.Customer; // Importa l'entità Customer gestita dal repository.
import org.springframework.data.repository.CrudRepository; // Fornisce le operazioni CRUD di base.
import org.springframework.stereotype.Repository; // Indica a Spring che l'interfaccia è un componente repository.

import java.util.List; // Supporta i metodi che restituiscono elenchi di clienti.
import java.util.Optional; // Gestisce risultati opzionali per ricerche specifiche.

@Repository // Abilita il rilevamento automatico del repository da parte di Spring.
public interface CustomerRepository extends CrudRepository<Customer, Long> { // Estende CrudRepository per gestire Customer con ID Long.

    List<Customer> findAllByOrderByNameAsc(); // Restituisce tutti i clienti ordinati alfabeticamente per nome.

    Optional<Customer> findByEmailIgnoreCase(String email); // Cerca un cliente tramite email ignorando maiuscole e minuscole.

    Optional<Customer> findByVatNumberIgnoreCase(String vatNumber); // Recupera un cliente tramite partita IVA senza distinzione di maiuscole.
}
