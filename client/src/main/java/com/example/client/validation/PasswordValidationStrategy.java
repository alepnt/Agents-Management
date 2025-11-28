package com.example.client.validation;
// Package dedicato alle strategie di validazione lato client.

import java.util.Optional;
// Optional permette di restituire un errore o un valore vuoto (validazione ok).

/**
 * Strategia di validazione della password.
 *
 * Le regole implementate sono:
 * - obbligatorietà della password
 * - lunghezza minima di 8 caratteri
 * - almeno una lettera maiuscola
 * - almeno una lettera minuscola
 * - almeno un numero
 *
 * Implementa ValidationStrategy per essere utilizzata all’interno
 * di CompositeValidator.
 */
public class PasswordValidationStrategy implements ValidationStrategy {

    /**
     * Esegue la validazione della password in più passaggi.
     *
     * @param value valore della password da validare
     * @return Optional<String> contenente un messaggio di errore
     *         oppure Optional.empty() se tutte le regole sono rispettate.
     */
    @Override
    public Optional<String> validate(String value) {

        // 1. Non deve essere null o vuota.
        if (value == null || value.isBlank()) {
            return Optional.of("La password è obbligatoria");
        }

        // 2. Lunghezza minima.
        if (value.length() < 8) {
            return Optional.of("La password deve contenere almeno 8 caratteri");
        }

        // 3. Deve contenere almeno una maiuscola.
        if (!value.matches(".*[A-Z].*")) {
            return Optional.of("La password deve contenere una lettera maiuscola");
        }

        // 4. Deve contenere almeno una minuscola.
        if (!value.matches(".*[a-z].*")) {
            return Optional.of("La password deve contenere una lettera minuscola");
        }

        // 5. Deve contenere almeno un numero.
        if (!value.matches(".*\\d.*")) {
            return Optional.of("La password deve contenere un numero");
        }

        // Tutte le condizioni sono soddisfatte → validazione OK.
        return Optional.empty();
    }
}
