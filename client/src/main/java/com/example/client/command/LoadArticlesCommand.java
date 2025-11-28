package com.example.client.command; // Package dedicato ai comandi lato client.

import com.example.client.service.BackendGateway; // Componente che esegue le chiamate REST al backend.
import com.example.common.dto.ArticleDTO; // DTO che rappresenta un articolo del catalogo.

import java.util.List; // API Java per collezioni di oggetti.

/**
 * Comando che carica l'intero catalogo degli articoli dal backend.
 * Non produce storico documentale, perché gli articoli non generano history.
 */
public class LoadArticlesCommand implements ClientCommand<List<ArticleDTO>> { // Il comando restituisce una lista di
                                                                              // ArticleDTO.

    @Override
    public CommandResult<List<ArticleDTO>> execute(BackendGateway gateway) { // Metodo centrale del comando.
        return CommandResult.withoutHistory( // Nessuno storico associato agli articoli.
                gateway.listArticles() // Recupera la lista degli articoli dal backend.
        );
    }

    @Override
    public String description() { // Descrizione usata dal Memento.
        return "Caricamento catalogo articoli"; // Testo leggibile e descrittivo dell’operazione.
    }
} // Fine classe LoadArticlesCommand.
