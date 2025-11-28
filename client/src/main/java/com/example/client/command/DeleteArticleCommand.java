package com.example.client.command; // Package del pattern Command lato client.

import com.example.client.service.BackendGateway; // Gateway che espone le operazioni REST verso il backend.

/**
 * Comando che elimina un articolo dal catalogo.
 * Produce un risultato privo di payload (Void) e senza storico documentale.
 */
public class DeleteArticleCommand implements ClientCommand<Void> { // Il comando non restituisce dati → tipo di ritorno
                                                                   // Void.

    private final Long id; // Identificativo dell’articolo da eliminare.

    public DeleteArticleCommand(Long id) { // Costruttore che accetta l’ID dell’articolo.
        this.id = id; // Memorizza l’ID per l’esecuzione.
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) { // Invoca l’operazione lato backend.
        gateway.deleteArticle(id); // Chiamata REST DELETE verso il backend.
        return CommandResult.withoutHistory(null); // Nessuno storico associato agli articoli → ritorna senza history.
    }

    @Override
    public String description() { // Descrizione del comando usata dal Memento.
        return "Eliminazione articolo #" + id; // Testo leggibile con riferimento all'articolo eliminato.
    }
} // Fine classe DeleteArticleCommand.
