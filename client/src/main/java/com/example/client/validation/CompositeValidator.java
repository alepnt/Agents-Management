package com.example.client.validation;
// Package dedicato alle strategie di validazione lato client.

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
// Utilità Java per elenchi dinamici e ritorni opzionali.

/**
 * Implementa il pattern "Composite" applicato alla validazione.
 *
 * Un CompositeValidator contiene una lista di strategie di validazione.
 * Ogni strategia è un oggetto che implementa ValidationStrategy
 * (ad es.: EmailValidationStrategy, PasswordValidationStrategy, ecc.).
 *
 * L'obiettivo:
 * - eseguire le strategie in ordine
 * - restituire il primo errore riscontrato
 * - se nessuna fallisce → validazione OK
 *
 * Questo permette di comporre facilmente pipeline di validazione.
 */
public class CompositeValidator {

    private final List<ValidationStrategy> strategies = new ArrayList<>();
    // Lista delle strategie applicate in sequenza.

    /**
     * Aggiunge una strategia alla pipeline.
     * Restituisce il validator stesso per consentire chaining fluente.
     *
     * Esempio:
     * new CompositeValidator()
     * .addStrategy(new EmailValidationStrategy())
     * .addStrategy(new PasswordValidationStrategy());
     */
    public CompositeValidator addStrategy(ValidationStrategy strategy) {
        strategies.add(strategy);
        return this; // Fluent API
    }

    /**
     * Esegue tutte le strategie nell'ordine in cui sono state aggiunte.
     *
     * @param value valore da validare (può essere null).
     * @return Optional.empty() se tutti i controlli passano;
     *         Optional<String> contenente il messaggio di errore
     *         della prima strategia che fallisce.
     */
    public Optional<String> validate(String value) {
        for (ValidationStrategy strategy : strategies) {
            Optional<String> result = strategy.validate(value);

            // Se la strategia ha fallito → ritorna subito quell’errore
            if (result.isPresent()) {
                return result;
            }
        }

        // Nessun errore → validazione OK
        return Optional.empty();
    }
}
