package com.example.server.controller; // Package del controller

import com.example.common.api.ReportApiContract; // Import delle dipendenze necessarie
import com.example.server.service.ReportService; // Import delle dipendenze necessarie
import org.springframework.format.annotation.DateTimeFormat; // Import delle dipendenze necessarie
import org.springframework.http.HttpHeaders; // Import delle dipendenze necessarie
import org.springframework.http.MediaType; // Import delle dipendenze necessarie
import org.springframework.http.ResponseEntity; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie

import java.time.LocalDate; // Import delle dipendenze necessarie
import java.util.Objects; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/reports") // Imposta il percorso base degli endpoint
public class ReportController implements ReportApiContract { // Dichiarazione della classe controller

    private final ReportService reportService; // Definizione di una dipendenza iniettata

    public ReportController(ReportService reportService) { // Inizio di un metodo esposto dal controller
        this.reportService = reportService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/closed-invoices") // Mapping per una richiesta GET
    public ResponseEntity<byte[]> closedInvoices(@RequestParam(value = "from", required = false) // Firma di un metodo del controller
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, // Istruzione di gestione del controller
                                                 @RequestParam(value = "to", required = false) // Istruzione di gestione del controller
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, // Istruzione di gestione del controller
                                                 @RequestParam(value = "agentId", required = false) Long agentId) { // Istruzione di gestione del controller
        byte[] pdf = Objects.requireNonNull(reportService.generateClosedInvoicesReport(from, to, agentId), // Istruzione di gestione del controller
                "generated report must not be null"); // Istruzione di gestione del controller
        String filename = "report-fatture-chiuse-" + System.currentTimeMillis() + ".pdf"; // Istruzione di gestione del controller
        return ResponseEntity.ok() // Restituisce il risultato dell operazione
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename) // Istruzione di gestione del controller
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_PDF, "mediaType must not be null")) // Istruzione di gestione del controller
                .body(pdf); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
