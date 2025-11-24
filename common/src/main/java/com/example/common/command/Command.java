package com.example.common.command;

import org.springframework.lang.NonNull;

/**
 * Interfaccia base del pattern Command utilizzabile da client e server per le operazioni CRUD.
 * <p>
 * Il contesto passato all'esecuzione non può essere {@code null} per evitare propagazioni di
 * valori mancanti lungo la catena di invocazione.
 */
public interface Command<R> {

    @NonNull
    R execute(@NonNull CommandContext context);
}
