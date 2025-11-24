package com.example.server.repository; // Package che raggruppa i repository Spring dell'applicazione server.

import com.example.server.domain.Article; // Importa l'entità Article gestita dal repository.
import org.springframework.data.repository.CrudRepository; // Interfaccia base per operazioni CRUD.
import org.springframework.stereotype.Repository; // Stereotipo Spring che registra il bean come repository.

import java.util.List; // Supporta i metodi che restituiscono collezioni di articoli.
import java.util.Optional; // Avvolge risultati che potrebbero essere assenti.

@Repository // Consente la rilevazione automatica dell'interfaccia come componente Spring.
public interface ArticleRepository extends CrudRepository<Article, Long> { // Estende CrudRepository per lavorare con Article e ID Long.

    List<Article> findAllByOrderByNameAsc(); // Restituisce tutti gli articoli ordinati alfabeticamente per nome.

    Optional<Article> findByCodeIgnoreCase(String code); // Cerca un articolo in base al codice ignorando le differenze di maiuscole.
}
