package com.example.client.validation;
// Package dedicato alle strategie di validazione lato client.

import java.util.Optional;
import java.util.regex.Pattern;
// Utilizzo di Optional per ritornare errori e Pattern per la regex email.

/**
 * Strategia di validazione dedicata al controllo della correttezza dell'email.
 * Implementa l'interfaccia ValidationStrategy, quindi può essere inserita
 * all'interno del CompositeValidator.
 */
public class EmailValidationStrategy implements ValidationStrategy {

    /**
     * Regex semplificata per validare una struttura email standard.
     *
     * Regole:
     * - parte locale: lettere, numeri e simboli comuni +_.-
     * - chiocciola obbligatoria
     * - dominio con lettere, numeri e punti
     *
     * Non è una regex perfetta (RFC è molto più complessa) ma ottima per uso UI.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Valida il valore passato secondo due regole:
     *
     * 1. Non deve essere null o vuoto.
     * 2. Deve rispettare il pattern email.
     *
     * @param value email da validare
     * @return Optional contenente il messaggio di errore oppure empty() se valida
     */
    @Override
    public Optional<String> validate(String value) {

        // Regola 1: email obbligatoria
        if (value == null || value.isBlank()) {
            return Optional.of("L'email è obbligatoria");
        }

        // Regola 2: controllo sintattico tramite regex
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            return Optional.of("Formato email non valido");
        }

        // Se entrambe le regole sono soddisfatte → OK
        return Optional.empty();
    }
}
