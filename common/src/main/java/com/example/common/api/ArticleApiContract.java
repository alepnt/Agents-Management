package com.example.common.api;                   // Package che contiene i contratti API condivisi tra client e server.

import java.util.List;         // DTO che rappresenta un articolo nel sistema di catalogo.
import java.util.Optional;                            // Necessario per restituire collezioni di oggetti.

import com.example.common.dto.ArticleDTO;                        // Utilizzato per risultati opzionali (entità non garantita).

/**
 * Contratto API per la gestione del catalogo articoli.
 * Definisce le operazioni CRUD che devono essere implementate dal backend.
 */
public interface ArticleApiContract {              // Interfaccia che espone le funzionalità principali sugli articoli.

    List<ArticleDTO> listArticles();               // Restituisce la lista completa degli articoli presenti nel catalogo.

    Optional<ArticleDTO> findById(Long id);        // Recupera un singolo articolo in base al suo identificatore, se esiste.

    ArticleDTO create(ArticleDTO article);         // Crea un nuovo articolo utilizzando i dati contenuti nel DTO fornito.

    ArticleDTO update(Long id, ArticleDTO article);// Aggiorna l'articolo identificato tramite ID con i nuovi dati forniti.

    void delete(Long id);                          // Elimina l'articolo corrispondente all'ID specificato.
}                                                  // Fine dell’interfaccia ArticleApiContract.
