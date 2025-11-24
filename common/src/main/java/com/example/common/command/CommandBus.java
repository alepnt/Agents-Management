package com.example.common.command;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Semplice dispatcher per l'esecuzione dei comandi condiviso fra moduli.
 */
public class CommandBus {

    private final @NonNull CommandContext context;

    public CommandBus(@NonNull CommandContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    public @NonNull <R> R dispatch(@NonNull Command<R> command) {
        Objects.requireNonNull(command, "command");
        return Objects.requireNonNull(command.execute(context), "command result");
    }
}
