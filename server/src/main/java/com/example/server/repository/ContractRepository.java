package com.example.server.repository; // Package dedicato ai repository Spring dell'applicazione server.

import com.example.server.domain.Contract; // Importa l'entità Contract su cui opera il repository.
import org.springframework.data.repository.CrudRepository; // Fornisce le operazioni CRUD generiche.
import org.springframework.stereotype.Repository; // Stereotipo che contrassegna il componente come repository Spring.

import java.util.List; // Consente di restituire raccolte di contratti.

@Repository // Abilita la rilevazione automatica del repository da parte di Spring.
public interface ContractRepository extends CrudRepository<Contract, Long> { // Estende CrudRepository per gestire Contract con ID Long.

    List<Contract> findAllByOrderByStartDateDesc(); // Ritorna tutti i contratti ordinati per data di inizio decrescente.
}
