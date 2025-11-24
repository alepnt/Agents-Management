package com.example.server.controller; // Package del controller

import com.example.common.api.DocumentHistoryApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.DocumentHistoryPageDTO; // Import delle dipendenze necessarie
import com.example.common.enums.DocumentAction; // Import delle dipendenze necessarie
import com.example.common.enums.DocumentType; // Import delle dipendenze necessarie
import com.example.server.service.DocumentHistoryQuery; // Import delle dipendenze necessarie
import com.example.server.service.DocumentHistoryService; // Import delle dipendenze necessarie
import org.springframework.format.annotation.DateTimeFormat; // Import delle dipendenze necessarie
import org.springframework.http.HttpHeaders; // Import delle dipendenze necessarie
import org.springframework.http.MediaType; // Import delle dipendenze necessarie
import org.springframework.http.ResponseEntity; // Import delle dipendenze necessarie
import org.springframework.util.StringUtils; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie

import java.time.OffsetDateTime; // Import delle dipendenze necessarie
import java.util.Collections; // Import delle dipendenze necessarie
import java.util.List; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/history") // Imposta il percorso base degli endpoint
public class DocumentHistoryController implements DocumentHistoryApiContract { // Dichiarazione della classe controller

    private final DocumentHistoryService documentHistoryService; // Definizione di una dipendenza iniettata

    public DocumentHistoryController(DocumentHistoryService documentHistoryService) { // Inizio di un metodo esposto dal controller
        this.documentHistoryService = documentHistoryService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public DocumentHistoryPageDTO search(@RequestParam(value = "documentType", required = false) DocumentType documentType, // Firma di un metodo del controller
                                         @RequestParam(value = "documentId", required = false) Long documentId, // Istruzione di gestione del controller
                                         @RequestParam(value = "actions", required = false) List<DocumentAction> actions, // Istruzione di gestione del controller
                                         @RequestParam(value = "from", required = false) // Istruzione di gestione del controller
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from, // Istruzione di gestione del controller
                                         @RequestParam(value = "to", required = false) // Istruzione di gestione del controller
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to, // Istruzione di gestione del controller
                                         @RequestParam(value = "q", required = false) String search, // Istruzione di gestione del controller
                                         @RequestParam(value = "page", defaultValue = "0") int page, // Istruzione di gestione del controller
                                         @RequestParam(value = "size", defaultValue = "25") int size) { // Istruzione di gestione del controller
        DocumentHistoryQuery query = buildQuery(documentType, documentId, actions, from, to, search, page, size); // Istruzione di gestione del controller
        return documentHistoryService.search(query); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/export") // Mapping per una richiesta GET
    public ResponseEntity<byte[]> export(@RequestParam(value = "documentType", required = false) DocumentType documentType, // Firma di un metodo del controller
                                         @RequestParam(value = "documentId", required = false) Long documentId, // Istruzione di gestione del controller
                                         @RequestParam(value = "actions", required = false) List<DocumentAction> actions, // Istruzione di gestione del controller
                                         @RequestParam(value = "from", required = false) // Istruzione di gestione del controller
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from, // Istruzione di gestione del controller
                                         @RequestParam(value = "to", required = false) // Istruzione di gestione del controller
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to, // Istruzione di gestione del controller
                                         @RequestParam(value = "q", required = false) String search) { // Istruzione di gestione del controller
        DocumentHistoryQuery query = buildQuery(documentType, documentId, actions, from, to, search, 0, 0); // Istruzione di gestione del controller
        byte[] csv = documentHistoryService.exportCsv(query); // Istruzione di gestione del controller
        String filename = "document-history-" + System.currentTimeMillis() + ".csv"; // Istruzione di gestione del controller
        return ResponseEntity.ok() // Restituisce il risultato dell operazione
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename) // Istruzione di gestione del controller
                .contentType(MediaType.valueOf("text/csv")) // Istruzione di gestione del controller
                .body(csv); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    private DocumentHistoryQuery buildQuery(DocumentType documentType, // Istruzione di gestione del controller
                                            Long documentId, // Lettura di un identificativo o valore numerico
                                            List<DocumentAction> actions, // Gestione di una lista di valori
                                            OffsetDateTime from, // Istruzione di gestione del controller
                                            OffsetDateTime to, // Istruzione di gestione del controller
                                            String search, // Istruzione di gestione del controller
                                            int page, // Lettura di un identificativo o valore numerico
                                            int size) { // Lettura di un identificativo o valore numerico
        return DocumentHistoryQuery.builder() // Restituisce il risultato dell operazione
                .documentType(documentType) // Istruzione di gestione del controller
                .documentId(documentId) // Istruzione di gestione del controller
                .actions(actions != null ? actions : Collections.emptyList()) // Istruzione di gestione del controller
                .from(from != null ? from.toInstant() : null) // Istruzione di gestione del controller
                .to(to != null ? to.toInstant() : null) // Istruzione di gestione del controller
                .searchText(StringUtils.hasText(search) ? search : null) // Istruzione di gestione del controller
                .page(page) // Istruzione di gestione del controller
                .size(size) // Istruzione di gestione del controller
                .build(); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
