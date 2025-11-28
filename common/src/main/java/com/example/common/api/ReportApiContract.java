package com.example.common.api;                                   // Package che contiene i contratti API condivisi tra client e server.

import java.time.LocalDate;      // Permette il parsing automatico delle date nei parametri REST.

import org.springframework.format.annotation.DateTimeFormat;                   // Wrapper HTTP utilizzato per restituire risposte binarie.
import org.springframework.http.ResponseEntity;                                       // Rappresenta una data senza informazioni di fuso.

/**
 * Contratto API per la generazione di report.
 * Espone operazioni che producono documenti binari (es. PDF, Excel).
 */
public interface ReportApiContract {                              // Interfaccia che definisce i metodi di generazione report.

    ResponseEntity<byte[]> closedInvoices(                        // Restituisce un file binario contenente il report delle fatture chiuse.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)        // Richiede una data in formato ISO (yyyy-MM-dd) per corretta deserializzazione.
            LocalDate from,                                       // Data di inizio del periodo da includere nel report.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)        // Anche la data finale deve rispettare il formato ISO.
            LocalDate to,                                         // Data di fine del periodo da includere nel report.
            Long agentId                                          // Identificativo dell’agente da filtrare (opzionale).
    );
}                                                                 // Fine dell’interfaccia ReportApiContract.
