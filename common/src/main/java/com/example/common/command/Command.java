package com.example.common.command;                   // Package che contiene le astrazioni del pattern Command condivise tra client e server.

import org.springframework.lang.NonNull;              // Annotazione che impone la non-nullità dei parametri e dei valori restituiti.

/**
 * Interfaccia base del pattern Command utilizzabile da client e server per le operazioni CRUD.
 * <p>
 * Il contesto passato all'esecuzione non può essere {@code null} per evitare propagazioni di
 * valori mancanti lungo la catena di invocazione.
 */
public interface Command<R> {                         // Interfaccia generica per un comando che restituisce un risultato di tipo R.

    @NonNull                                          // Garantisce che il risultato non sia mai null.
    R execute(@NonNull CommandContext context);       // Esegue il comando utilizzando un contesto obbligatorio e restituisce il risultato.
}                                                     // Fine dell’interfaccia Command.
