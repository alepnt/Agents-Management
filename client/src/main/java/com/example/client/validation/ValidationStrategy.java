package com.example.client.validation;
// Package dedicato alle strategie di validazione lato client.

import java.util.Optional;

/**
 * Interfaccia che rappresenta una singola strategia di validazione.
 *
 * Ogni implementazione definisce una regola o un insieme di regole
 * per validare un valore (tipicamente una stringa).
 *
 * Usata da CompositeValidator per costruire pipeline di validazione
 * componibili.
 */
public interface ValidationStrategy {

    /**
     * Valida il valore passato secondo la regola implementata.
     *
     * param value il valore da validare (può essere null)
     * return Optional<String>:
     * - empty() se la validazione è superata
     * - Optional.of("messaggio errore") se la validazione fallisce
     */
    Optional<String> validate(String value);
}
