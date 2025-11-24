package com.example.server.repository; // Package che contiene i repository e le classi di query personalizzate.

import com.example.common.enums.DocumentAction; // Importa l'enum che descrive le azioni sui documenti.
import com.example.common.enums.DocumentType; // Importa l'enum che identifica i tipi di documento supportati.
import com.example.server.domain.DocumentHistory; // Importa l'entità che rappresenta una riga dello storico documentale.
import com.example.server.service.DocumentHistoryQuery; // Importa l'oggetto di query con i filtri richiesti.
import org.springframework.jdbc.core.RowMapper; // RowMapper per convertire le righe del ResultSet in oggetti dominio.
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource; // Gestisce i parametri nominati della query SQL.
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // Template JDBC che supporta parametri nominati.
import org.springframework.stereotype.Repository; // Stereotipo Spring per la classe repository personalizzata.
import org.springframework.util.StringUtils; // Utility per la gestione sicura delle stringhe.

import java.time.Instant; // Rappresenta timestamp utilizzati nei record dello storico.
import java.util.Collections; // Fornisce collezioni immutabili di fallback.
import java.util.List; // Gestisce liste di risultati della query.
import java.util.Objects; // Utility per controllare valori null o costruire requisiti.

/**
 * Repository custom per interrogare lo storico documentale con filtri dinamici e paginazione.
 */
@Repository // Rende la classe rilevabile come componente repository da Spring.
public class DocumentHistoryQueryRepository { // Implementa query personalizzate sullo storico dei documenti.

    private final NamedParameterJdbcTemplate jdbcTemplate; // Template JDBC usato per eseguire query con parametri nominati.

    private static final RowMapper<DocumentHistory> ROW_MAPPER = (rs, rowNum) -> { // Converte ogni riga del ResultSet in un DocumentHistory.
        Long id = rs.getLong("id"); // Legge l'identificativo del record.
        String documentType = rs.getString("document_type"); // Recupera il tipo di documento come stringa.
        Long documentId = rs.getObject("document_id") != null ? rs.getLong("document_id") : null; // Preleva l'ID documento gestendo i null.
        String action = rs.getString("action"); // Ottiene l'azione eseguita sul documento.
        String description = rs.getString("description"); // Acquisisce la descrizione associata all'evento.
        Instant createdAt = rs.getTimestamp("created_at").toInstant(); // Converte il timestamp SQL in Instant.
        return new DocumentHistory( // Crea un nuovo oggetto DocumentHistory popolato.
                id, // Imposta l'ID del record storico.
                documentType != null ? DocumentType.valueOf(documentType) : null, // Converte il tipo documento in enum se presente.
                documentId, // Imposta l'ID del documento correlato.
                action != null ? DocumentAction.valueOf(action) : null, // Converte l'azione in enum se presente.
                description, // Imposta la descrizione dell'azione.
                createdAt // Imposta la data di creazione dell'evento.
        );
    }; // Conclusione del RowMapper statico.

    public DocumentHistoryQueryRepository(NamedParameterJdbcTemplate jdbcTemplate) { // Costruttore che riceve il template JDBC.
        this.jdbcTemplate = jdbcTemplate; // Assegna il template al campo finale.
    }

    public ResultPage find(DocumentHistoryQuery query) { // Esegue una ricerca paginata sullo storico in base ai filtri ricevuti.
        QueryParts parts = buildQuery(query); // Costruisce dinamicamente la clausola FROM/WHERE e i parametri.
        String fromClause = Objects.requireNonNull(parts.fromClause(), "fromClause must not be null"); // Verifica che la clausola SQL sia presente.
        MapSqlParameterSource parameters = Objects.requireNonNull(parts.parameters(), "parameters must not be null"); // Recupera i parametri sicuri.
        StringBuilder sql = new StringBuilder("SELECT id, document_type, document_id, action, description, created_at ") // Avvia la query di selezione.
                .append(fromClause) // Aggiunge il blocco FROM e i filtri.
                .append(" ORDER BY created_at DESC"); // Ordina i risultati dalla voce più recente.
        if (query.isPaginated()) { // Applica la paginazione solo se richiesta.
            sql.append(" OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY"); // Aggiunge clausola di offset/limit compatibile con il database.
            parameters.addValue("offset", query.offset()); // Imposta l'offset calcolato dalla query.
            parameters.addValue("limit", query.getSize()); // Imposta il numero di elementi da recuperare.
        }
        List<DocumentHistory> items = jdbcTemplate.query(sql.toString(), parameters, // Esegue la query e mappa i risultati.
                Objects.requireNonNull(ROW_MAPPER, "rowMapper must not be null")); // Utilizza il RowMapper definito per creare gli oggetti dominio.
        Long totalCount = query.isPaginated() // Calcola il numero totale solo quando la paginazione è attiva.
                ? Objects.requireNonNull(jdbcTemplate.queryForObject("SELECT COUNT(*) " + fromClause, parameters, Long.class), // Esegue query di conteggio.
                "totalCount must not be null") // Garantisce che il conteggio non sia nullo.
                : null; // In assenza di paginazione non calcola il totale.
        long total = totalCount != null ? totalCount : items.size(); // Determina il numero totale di risultati.
        return new ResultPage(items, total); // Restituisce i risultati e il totale incapsulati in ResultPage.
    }

    public List<DocumentHistory> findAll(DocumentHistoryQuery query) { // Recupera tutti i record che soddisfano i filtri senza paginazione.
        QueryParts parts = buildQuery(query.withoutPagination()); // Rimuove la paginazione e costruisce la clausola di ricerca.
        String fromClause = Objects.requireNonNull(parts.fromClause(), "fromClause must not be null"); // Garantisce la presenza del blocco FROM.
        MapSqlParameterSource parameters = Objects.requireNonNull(parts.parameters(), "parameters must not be null"); // Ottiene i parametri impostati.
        String sql = "SELECT id, document_type, document_id, action, description, created_at " // Stringa base della SELECT.
                + fromClause // Aggiunge la clausola dinamica costruita.
                + " ORDER BY created_at DESC"; // Ordina per data di creazione decrescente.
        return jdbcTemplate.query(sql, parameters, Objects.requireNonNull(ROW_MAPPER, "rowMapper must not be null")); // Esegue la query e mappa i risultati.
    }

    private QueryParts buildQuery(DocumentHistoryQuery query) { // Compone dinamicamente la clausola FROM/WHERE in base ai filtri.
        StringBuilder fromClause = new StringBuilder("FROM document_history WHERE 1 = 1"); // Base della query che consente di aggiungere filtri con AND.
        MapSqlParameterSource parameters = new MapSqlParameterSource(); // Contenitore per i parametri nominati.
        if (query.getDocumentType() != null) { // Filtra per tipo di documento se valorizzato.
            fromClause.append(" AND document_type = :documentType"); // Aggiunge la condizione sul tipo.
            parameters.addValue("documentType", query.getDocumentType().name()); // Imposta il parametro del tipo.
        }
        if (query.getDocumentId() != null) { // Filtra per ID documento se fornito.
            fromClause.append(" AND document_id = :documentId"); // Aggiunge condizione sull'ID documento.
            parameters.addValue("documentId", query.getDocumentId()); // Imposta il parametro dell'ID documento.
        }
        if (!query.getActions().isEmpty()) { // Applica filtro per una lista di azioni se presente.
            fromClause.append(" AND action IN (:actions)"); // Costruisce condizione con clausola IN.
            parameters.addValue("actions", query.getActions().stream().map(Enum::name).toList()); // Converte le azioni in stringhe enum e le passa come parametro.
        }
        if (query.getFrom() != null) { // Filtra a partire da una data/ora specifica se definita.
            fromClause.append(" AND created_at >= :from"); // Aggiunge limite inferiore sulla data di creazione.
            parameters.addValue("from", query.getFrom()); // Imposta il parametro di inizio intervallo.
        }
        if (query.getTo() != null) { // Filtra fino a una data/ora specifica se definita.
            fromClause.append(" AND created_at <= :to"); // Aggiunge limite superiore sulla data di creazione.
            parameters.addValue("to", query.getTo()); // Imposta il parametro di fine intervallo.
        }
        String searchText = query.getSearchText(); // Recupera il testo libero da cercare nella descrizione.
        if (StringUtils.hasText(searchText)) { // Verifica che il testo di ricerca contenga caratteri significativi.
            String normalizedSearch = searchText.trim(); // Normalizza la stringa eliminando spazi superflui.
            fromClause.append(" AND LOWER(description) LIKE :search"); // Aggiunge condizione LIKE sulla descrizione in minuscolo.
            parameters.addValue("search", "%" + normalizedSearch.toLowerCase() + "%"); // Imposta il parametro di ricerca con wildcard.
        }
        return new QueryParts(fromClause.toString(), parameters); // Restituisce le parti della query da usare nella SELECT principale.
    }

    public record ResultPage(List<DocumentHistory> items, long totalElements) { // Record che incapsula risultati e totale elementi.
        public ResultPage(List<DocumentHistory> items, long totalElements) { // Costruttore che normalizza la lista ricevuta.
            this.items = items != null ? items : Collections.emptyList(); // Usa lista vuota se gli elementi sono null.
            this.totalElements = totalElements; // Imposta il numero totale di elementi.
        }
    }

    private record QueryParts(String fromClause, MapSqlParameterSource parameters) { // Record interno che contiene SQL e parametri.
    }
}
