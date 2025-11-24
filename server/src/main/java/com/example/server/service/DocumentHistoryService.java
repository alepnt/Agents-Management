package com.example.server.service; // Definisce il package per i servizi di gestione dello storico documentale.

import com.example.common.dto.DocumentHistoryDTO; // Importa il DTO per esporre le voci di storico.
import com.example.common.dto.DocumentHistoryPageDTO; // Importa il DTO che rappresenta una pagina di risultati.
import com.example.common.enums.DocumentAction; // Importa l'enum per le azioni sui documenti.
import com.example.common.enums.DocumentType; // Importa l'enum per i tipi di documento.
import com.example.server.domain.DocumentHistory; // Importa l'entità di storico documentale.
import com.example.server.repository.DocumentHistoryQueryRepository; // Importa il repository per ricerche complesse.
import com.example.server.repository.DocumentHistoryRepository; // Importa il repository per operazioni CRUD di base.
import com.example.server.service.mapper.DocumentHistoryMapper; // Importa il mapper tra entità e DTO di storico.
import org.springframework.cache.annotation.CacheEvict; // Importa l'annotazione per invalidare la cache.
import org.springframework.cache.annotation.Cacheable; // Importa l'annotazione per abilitare la cache.
import org.springframework.stereotype.Service; // Importa l'annotazione Service di Spring.

import java.time.Clock; // Importa Clock per gestire l'ora corrente in modo testabile.
import java.time.Instant; // Importa Instant per gli istanti temporali.
import java.time.format.DateTimeFormatter; // Importa il formattatore per date in CSV.
import java.util.List; // Importa l'interfaccia List.
import java.util.Objects; // Importa utility per controlli null-safe.
import java.util.stream.Collectors; // Importa Collectors per convertire stream.

import static java.nio.charset.StandardCharsets.UTF_8; // Importa la costante di charset UTF-8.

@Service // Indica che la classe è un servizio Spring.
public class DocumentHistoryService { // Gestisce lo storico dei documenti e le relative esportazioni.

    private static final int DEFAULT_PAGE_SIZE = 25; // Dimensione di pagina di default.
    private static final int MAX_PAGE_SIZE = 200; // Dimensione massima consentita per le pagine.
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT; // Formattatore per le date nel CSV.

    private final DocumentHistoryRepository repository; // Repository per operazioni di persistenza di base.
    private final DocumentHistoryQueryRepository queryRepository; // Repository per query avanzate con criteri.
    private final Clock clock; // Sorgente di tempo per generare timestamp.

    public DocumentHistoryService(DocumentHistoryRepository repository, // Costruttore con dependency injection.
                                  DocumentHistoryQueryRepository queryRepository, // Riceve il repository per ricerche.
                                  Clock clock) { // Riceve l'orologio di sistema o mock.
        this.repository = repository; // Inizializza il repository di base.
        this.queryRepository = queryRepository; // Inizializza il repository per le query complesse.
        this.clock = clock; // Inizializza la sorgente di tempo.
    }

    @CacheEvict(cacheNames = "documentHistorySearch", allEntries = true) // Invalida la cache delle ricerche quando si scrive.
    public DocumentHistory log(DocumentType type, Long documentId, DocumentAction action, String description) { // Registra una nuova voce di storico.
        DocumentHistory history = Objects.requireNonNull( // Crea l'oggetto di storico e verifica che non sia null.
                DocumentHistory.create(type, documentId, action, description, Instant.now(clock)), // Costruisce la voce con timestamp corrente.
                "document history must not be null"); // Messaggio di errore se la creazione fallisce.
        return repository.save(history); // Salva e restituisce la voce di storico.
    }

    public List<DocumentHistory> list(DocumentType type, Long documentId) { // Restituisce tutte le voci di storico per un documento.
        DocumentType requiredType = Objects.requireNonNull(type, "type must not be null"); // Valida il tipo di documento.
        Long requiredDocumentId = Objects.requireNonNull(documentId, "documentId must not be null"); // Valida l'id del documento.
        return repository.findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(requiredType, requiredDocumentId); // Recupera lo storico ordinato per data.
    }

    @Cacheable(cacheNames = "documentHistorySearch", key = "#query.cacheKey()") // Abilita la cache per i risultati di ricerca.
    public DocumentHistoryPageDTO search(DocumentHistoryQuery query) { // Esegue una ricerca paginata dello storico.
        DocumentHistoryQuery normalized = normalize(Objects.requireNonNull(query, "query must not be null"), true); // Normalizza la query e la valida.
        DocumentHistoryQueryRepository.ResultPage resultPage = queryRepository.find(normalized); // Esegue la ricerca tramite repository specializzato.
        List<DocumentHistoryDTO> items = resultPage.items().stream() // Ottiene gli elementi della pagina come stream.
                .map(DocumentHistoryMapper::toDto) // Converte ogni entità in DTO.
                .collect(Collectors.toList()); // Colleziona i DTO in una lista.
        return new DocumentHistoryPageDTO(items, resultPage.totalElements(), normalized.getPage(), normalized.getSize()); // Crea il DTO paginato di risposta.
    }

    public byte[] exportCsv(DocumentHistoryQuery query) { // Esporta i risultati della ricerca in formato CSV.
        DocumentHistoryQuery normalized = normalize(Objects.requireNonNull(query, "query must not be null"), false) // Normalizza la query senza imporre paginazione.
                .withoutPagination(); // Disabilita la paginazione per esportare tutti i risultati.
        List<DocumentHistory> results = queryRepository.findAll(normalized); // Recupera tutte le voci corrispondenti.
        StringBuilder builder = new StringBuilder(); // Crea un builder per il contenuto CSV.
        builder.append("id;documentType;documentId;action;description;createdAt\n"); // Aggiunge l'intestazione del CSV.
        for (DocumentHistory entry : results) { // Itera su ogni voce di storico.
            builder.append(valueOf(entry.getId())) // Scrive l'id della voce.
                    .append(';') // Aggiunge il separatore.
                    .append(enumValue(entry.getDocumentType())) // Scrive il tipo di documento.
                    .append(';') // Aggiunge il separatore.
                    .append(valueOf(entry.getDocumentId())) // Scrive l'id del documento.
                    .append(';') // Aggiunge il separatore.
                    .append(enumValue(entry.getAction())) // Scrive l'azione effettuata.
                    .append(';') // Aggiunge il separatore.
                    .append(escape(entry.getDescription())) // Scrive la descrizione escapata.
                    .append(';') // Aggiunge il separatore.
                    .append(entry.getCreatedAt() != null ? CSV_DATE_FORMATTER.format(entry.getCreatedAt()) : "") // Scrive la data formattata se presente.
                    .append('\n'); // Termina la riga del CSV.
        }
        return builder.toString().getBytes(UTF_8); // Converte il contenuto in array di byte UTF-8.
    }

    private DocumentHistoryQuery normalize(DocumentHistoryQuery query, boolean enforcePagination) { // Normalizza la query applicando limiti e default.
        DocumentHistoryQuery.Builder builder = DocumentHistoryQuery.builder() // Crea un nuovo builder a partire dai criteri.
                .documentType(query.getDocumentType()) // Copia il tipo di documento.
                .documentId(query.getDocumentId()) // Copia l'id del documento.
                .actions(query.getActions()) // Copia le azioni richieste.
                .from(query.getFrom()) // Copia la data iniziale.
                .to(query.getTo()) // Copia la data finale.
                .searchText(query.getSearchText()); // Copia il testo di ricerca.
        if (enforcePagination) { // Se occorre forzare la paginazione.
            int size = query.getSize() > 0 ? Math.min(query.getSize(), MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE; // Calcola la dimensione di pagina limitandola.
            builder.page(query.getPage()).size(size); // Imposta pagina e dimensione normalizzate.
        } else { // Se la paginazione non è richiesta.
            builder.page(0).size(0); // Disabilita paginazione.
        }
        return builder.build(); // Restituisce la query normalizzata.
    }

    private String valueOf(Object value) { // Converte un oggetto in stringa gestendo i null.
        return value != null ? value.toString() : ""; // Restituisce la stringa oppure vuoto.
    }

    private String enumValue(Enum<?> value) { // Restituisce il nome di un enum gestendo i null.
        return value != null ? value.name() : ""; // Restituisce il nome o stringa vuota.
    }

    private String escape(String value) { // Escapa i campi testuali per il CSV.
        if (value == null) { // Controlla se la stringa è null.
            return ""; // Restituisce vuoto in caso di null.
        }
        String escaped = value.replace("\"", "\"\""); // Raddoppia gli apici doppi per il CSV.
        return '"' + escaped + '"'; // Racchiude il testo tra apici doppi.
    }
}
