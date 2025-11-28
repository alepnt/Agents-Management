package com.example.client.command; // Package dei comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate REST verso il backend.
import com.example.common.dto.ArticleDTO; // DTO che rappresenta un articolo del catalogo.

/**
 * Comando che aggiorna un articolo esistente.
 * Non produce storico documentale perché gli articoli non generano history.
 */
public class UpdateArticleCommand implements ClientCommand<ArticleDTO> { // Il comando restituisce l'articolo
                                                                         // aggiornato.

    private final Long id; // Identificativo dell’articolo da aggiornare.
    private final ArticleDTO article; // Nuovi dati da applicare all’articolo.

    public UpdateArticleCommand(Long id, ArticleDTO article) { // Costruttore del comando.
        this.id = id; // Memorizza l'id dell’articolo.
        this.article = article; // Memorizza il DTO contenente i nuovi valori.
    }

    @Override
    public CommandResult<ArticleDTO> execute(BackendGateway gateway) { // Esecuzione del comando.
        ArticleDTO updated = gateway.updateArticle(id, article); // Invoca il backend per aggiornare l’articolo.
        return CommandResult.withoutHistory(updated); // Nessuno storico associato → ritorna senza history.
    }

    @Override
    public String description() { // Descrizione leggibile per il Memento.
        return "Aggiornamento articolo #" + id; // Etichetta dell’operazione eseguita.
    }
} // Fine classe UpdateArticleCommand.
