// Definisce il package principale in cui vive l'applicazione server.
package com.example.server;

// Importa la classe che permette di avviare un'applicazione Spring Boot.
import org.springframework.boot.SpringApplication;
// Importa l'annotazione che abilita la configurazione automatica e la scansione dei componenti.
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Importa l'annotazione che consente di attivare il supporto alla cache nell'applicazione.
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

/**
 * Punto di ingresso dell'applicazione Spring Boot per il modulo server.
 * Ogni istruzione è accompagnata da un commento descrittivo per chiarirne lo scopo.
 */
@SpringBootApplication // Indica che questa classe è il punto di partenza dell'app Spring Boot.
@EnableCaching // Attiva la cache gestita da Spring per migliorare le prestazioni dove necessario.
@EnableJdbcAuditing // Abilita l'auditing per valorizzare automaticamente i campi di creazione/aggiornamento.
public class GestoreAgentiServerApplication { // Definisce la classe principale che avvia il server.

    // Metodo main invocato all'avvio del processo Java.
    public static void main(String[] args) {
        // Esegue l'applicazione Spring Boot utilizzando questa classe come configurazione iniziale.
        SpringApplication.run(GestoreAgentiServerApplication.class, args);
    }
}
