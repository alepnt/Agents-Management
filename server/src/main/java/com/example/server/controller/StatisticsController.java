package com.example.server.controller; // Package del controller

import com.example.common.api.StatisticsApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.AgentStatisticsDTO; // Import delle dipendenze necessarie
import com.example.common.dto.TeamStatisticsDTO; // Import delle dipendenze necessarie
import com.example.server.service.StatisticsService; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/stats") // Imposta il percorso base degli endpoint
public class StatisticsController implements StatisticsApiContract { // Dichiarazione della classe controller

    private final StatisticsService statisticsService; // Definizione di una dipendenza iniettata

    public StatisticsController(StatisticsService statisticsService) { // Inizio di un metodo esposto dal controller
        this.statisticsService = statisticsService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/agent") // Mapping per una richiesta GET
    public AgentStatisticsDTO agentStatistics(@RequestParam(value = "year", required = false) Integer year) { // Inizio di un metodo esposto dal controller
        return statisticsService.agentStatistics(year); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/team") // Mapping per una richiesta GET
    public TeamStatisticsDTO teamStatistics(@RequestParam(value = "year", required = false) Integer year) { // Inizio di un metodo esposto dal controller
        return statisticsService.teamStatistics(year); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
