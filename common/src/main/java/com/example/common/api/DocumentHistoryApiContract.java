package com.example.common.api;                                         // Package che contiene i contratti API condivisi a livello applicativo.

import java.time.OffsetDateTime;                   // DTO che rappresenta una pagina di risultati dello storico documentale.
import java.util.List;                         // Enum che indica il tipo di azione registrata nello storico.

import org.springframework.format.annotation.DateTimeFormat;                           // Enum che indica il tipo di documento (es. contratto, fattura).
import org.springframework.http.ResponseEntity;            // Permette la corretta deserializzazione delle date nei parametri REST.

import com.example.common.dto.DocumentHistoryPageDTO;                         // Wrapper HTTP usato per restituire file binari.
import com.example.common.enums.DocumentAction;                                        // Rappresenta un timestamp con fuso integrato.
import com.example.common.enums.DocumentType;                                                  // Necessario per liste di azioni o altri filtri.

/**
 * Contratto API per la consultazione dello storico documentale.
 * Consente ricerche filtrate e l’esportazione dei risultati.
 */
public interface DocumentHistoryApiContract {                            // Interfaccia principale dedicata alle operazioni sullo storico.

    DocumentHistoryPageDTO search(                                       // Restituisce una pagina di risultati filtrati dello storico.
            DocumentType documentType,                                   // Tipo di documento da filtrare.
            Long documentId,                                             // ID del documento a cui lo storico appartiene.
            List<DocumentAction> actions,                                // Lista di azioni da includere nel filtro (CREATED, UPDATED, ecc.).
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)          // Richiede il formato ISO 8601 nella richiesta REST.
            OffsetDateTime from,                                         // Timestamp iniziale del filtro temporale.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)          // Richiede il formato ISO 8601 nella richiesta REST.
            OffsetDateTime to,                                           // Timestamp finale del filtro temporale.
            String search,                                               // Termine di ricerca testuale libera (utente, descrizione, ecc.).
            int page,                                                    // Numero della pagina richiesta (paginazione).
            int size                                                     // Dimensione della pagina.
    );

    ResponseEntity<byte[]> export(                                       // Esporta lo storico filtrato in formato binario (PDF/CSV/Excel).
            DocumentType documentType,                                   // Tipo documento da esportare.
            Long documentId,                                             // ID del documento collegato allo storico.
            List<DocumentAction> actions,                                // Filtro sulle azioni incluse nell'esportazione.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)          // Parsing automatico timestamp in formato ISO.
            OffsetDateTime from,                                         // Intervallo temporale: inizio.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)          // Parsing automatico timestamp in formato ISO.
            OffsetDateTime to,                                           // Intervallo temporale: fine.
            String search                                                // Filtro di ricerca testuale opzionale.
    );
}                                                                         // Fine dell'interfaccia DocumentHistoryApiContract.
