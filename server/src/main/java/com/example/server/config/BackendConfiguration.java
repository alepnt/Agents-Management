package com.example.server.config; // Dichiara il package che ospita le configurazioni del server.

import org.springframework.context.annotation.Bean; // Importa l'annotazione per esporre un bean Spring.
import org.springframework.context.annotation.Configuration; // Importa l'annotazione che marca una classe di configurazione.

import java.time.Clock; // Importa l'API Java per rappresentare un orologio di sistema.

@Configuration // Indica che questa classe fornisce definizioni di bean Spring.
public class BackendConfiguration { // Definisce la classe di configurazione applicativa.

    @Bean // Espone il metodo come bean gestito da Spring.
    public Clock systemClock() { // Restituisce un orologio da iniettare dove serve il tempo corrente.
        return Clock.systemUTC(); // Fornisce un Clock basato sul fuso UTC per coerenza temporale.
    }
} // Chiude la definizione della classe di configurazione.
