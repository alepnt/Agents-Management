package com.example.server.config; 
// Package di configurazione del modulo server. Contiene i bean e le configurazioni
// condivise necessarie all'inizializzazione del contesto Spring.

import org.springframework.context.annotation.Bean; 
// Permette la dichiarazione esplicita di bean gestiti da Spring.

import org.springframework.context.annotation.Configuration; 
// Indica che la classe contiene definizioni di configurazione Spring.

import org.springframework.data.auditing.DateTimeProvider; 
// Interfaccia che fornisce una sorgente temporale per l'auditing
// (creazione e aggiornamento automatico dei campi temporali).

import java.time.Instant; 
// Fornisce l'implementazione temporale istantanea in UTC.

import java.util.Optional; 
// Utilizzato per incapsulare il valore temporale restituito dal provider.

/**
 * Configurazione che abilita e centralizza la gestione dell'auditing temporale.
 * Serve a popolare automaticamente i campi annotati con @CreatedDate e @LastModifiedDate.
 */
@Configuration // Indica a Spring di trattare la classe come configurazione.
public class AuditingConfig {

    /**
     * Definizione del bean DateTimeProvider.
     * Usato da Spring Data per ottenere il timestamp corrente ogni volta che serve.
     */
    @Bean // Espone il metodo come bean Spring nel contesto applicativo.
    public DateTimeProvider dateTimeProvider() {

        // Restituisce un provider che produce sempre Instant.now(),
        // cioè il timestamp in UTC al momento della chiamata.
        return () -> Optional.of(Instant.now());
    }
}
