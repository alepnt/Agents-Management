package com.example.client.command; // Package dedicato ai comandi lato client.

import com.example.client.service.BackendGateway; // Gateway per invocare gli endpoint del backend.
import com.example.common.dto.ArticleDTO; // DTO che rappresenta un articolo.

/**
 * Comando che crea un nuovo articolo nel backend.
 * Implementa il pattern Command lato client, incapsulando l'operazione
 * e delegando l'esecuzione al BackendGateway.
 */
public class CreateArticleCommand implements ClientCommand<ArticleDTO> { // Tipo di ritorno del comando: ArticleDTO.

    private final ArticleDTO article; // Articolo da creare, passato via costruttore.

    public CreateArticleCommand(ArticleDTO article) { // Costruttore del comando.
        this.article = article; // Memorizza l'articolo da inviare al backend.
    }

    @Override
    public CommandResult<ArticleDTO> execute(BackendGateway gateway) { // Invocazione del comando.
        ArticleDTO created = gateway.createArticle(article); // Effettua la chiamata REST verso il backend.
        return CommandResult.withoutHistory(created); // Ritorna il risultato senza storico documentale.
    }

    @Override
    public String description() { // Descrizione testuale usata nei Memento.
        return "Creazione articolo " + article.getName(); // Fornisce un nome significativo dell'operazione.
    }
} // Fine classe CreateArticleCommand.
