package com.example.common.command;                          // Package che contiene le classi condivise del pattern Command.

import java.util.Objects;                     // Annotazione che specifica parametri e ritorni non null.

import org.springframework.lang.NonNull;                                    // Utility per controlli di nullità e validazioni.

/**
 * Semplice dispatcher per l'esecuzione dei comandi condiviso fra moduli.
 * Permette di centralizzare l'invocazione dei Command utilizzando un contesto comune.
 */
public class CommandBus {                                    // Classe responsabile della dispatch dei comandi.

    private final @NonNull CommandContext context;            // Contesto condiviso da passare a ogni comando.

    public CommandBus(@NonNull CommandContext context) {      // Costruttore che richiede un contesto obbligatorio.
        this.context = Objects.requireNonNull(context, "context"); // Validazione immediata del contesto.
    }

    public @NonNull <R> R dispatch(@NonNull Command<R> command) { // Metodo generico per eseguire un comando.
        Objects.requireNonNull(command, "command");           // Validazione del comando prima dell'esecuzione.
        return Objects.requireNonNull(command.execute(context), "command result"); 
        // Esegue il comando con il contesto condiviso e garantisce che il risultato non sia null.
    }
}                                                             // Fine della classe CommandBus.
