package com.example.client.command; // Package che contiene l’implementazione del pattern Command/Memento lato client.

import java.time.Instant; // Utilizzato per registrare l’istante di esecuzione del comando.

/**
 * Memento che conserva lo stato significativo di un comando già eseguito.
 * 
 * Il memento salva:
 * - la descrizione del comando
 * - il risultato restituito dal comando
 * - il timestamp di esecuzione
 *
 * È immutabile per garantire coerenza storica.
 */
public class CommandMemento {

    private final String description; // Descrizione leggibile del comando eseguito.
    private final Instant executedAt; // Timestamp di esecuzione generato automaticamente.
    private final CommandResult<?> result; // Risultato del comando, tipizzato ma trattato come wildcard.

    /**
     * Costruisce un nuovo memento per un comando eseguito.
     *
     * @param description descrizione del comando eseguito
     * @param result      risultato restituito dal comando
     */
    public CommandMemento(String description, CommandResult<?> result) {
        this.description = description; // Salva la descrizione del comando.
        this.result = result; // Salva il risultato così come fornito dal comando.
        this.executedAt = Instant.now(); // Registra l'istante di creazione del memento.
    }

    /**
     * @return descrizione testuale del comando eseguito
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return istante in cui il comando è stato eseguito
     */
    public Instant getExecutedAt() {
        return executedAt;
    }

    /**
     * @return risultato del comando incapsulato nel memento
     */
    public CommandResult<?> getResult() {
        return result;
    }
} // Fine classe CommandMemento.
