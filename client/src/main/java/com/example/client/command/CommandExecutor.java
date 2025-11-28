package com.example.client.command; // Package dedicato all’implementazione del Command pattern lato client.

import com.example.client.service.BackendGateway; // Gateway per comunicare con il backend (REST API).

/**
 * Esegue i comandi e registra i relativi memento nello storico.
 * Fa da orchestratore tra:
 * - il comando (ClientCommand)
 * - il gateway che comunica col backend
 * - il caretaker che mantiene la storia delle esecuzioni
 */
public class CommandExecutor {

    private final BackendGateway backendGateway; // Gateway utilizzato per eseguire operazioni verso il server.
    private final CommandHistoryCaretaker caretaker; // Gestore dello storico (pattern Memento).

    /**
     * Costruttore: inizializza l'executor con gateway e caretaker.
     *
     * @param backendGateway gateway per la comunicazione col backend
     * @param caretaker      caretaker che salva i memento generati dai comandi
     */
    public CommandExecutor(BackendGateway backendGateway, CommandHistoryCaretaker caretaker) {
        this.backendGateway = backendGateway;
        this.caretaker = caretaker;
    }

    /**
     * Esegue un comando lato client, registra il relativo memento e restituisce il
     * risultato.
     *
     * @param command comando da eseguire
     * @param <T>     tipo del risultato prodotto dal comando
     * @return risultato incapsulato in un CommandResult (o null se il comando non
     *         produce risultato)
     */
    public <T> CommandResult<T> execute(ClientCommand<T> command) {
        CommandResult<T> result = command.execute(backendGateway); // Invoca il comando passando il gateway.

        if (result != null) { // Se il comando produce un risultato…
            caretaker.addMemento(new CommandMemento( // …crea un memento e lo registra nello storico:
                    command.description(), // - descrizione del comando
                    result // - risultato ottenuto
            ));
        }

        return result; // Restituisce il risultato al chiamante.
    }
} // Fine della classe CommandExecutor.
