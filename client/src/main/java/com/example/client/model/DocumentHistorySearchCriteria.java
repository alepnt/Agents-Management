package com.example.client.model;
// Package che contiene i modelli e le strutture dati utilizzate dal client.

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
// Enum condivisi con il backend per tipo documento e tipo azione.

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
// Utility Java per timestamp, liste immutabili e costruzione stringhe.

/**
 * Rappresenta i filtri applicabili alla ricerca dello storico documentale.
 * È un oggetto di criteri lato client che verrà tradotto in una richiesta
 * al server tramite query parameters o payload.
 *
 * Permette di filtrare lo storico per:
 * - tipo documento
 * - ID documento
 * - azioni (create/update/delete ecc.)
 * - intervallo temporale
 * - testo libero
 * - paginazione (in cacheKey)
 */
public class DocumentHistorySearchCriteria {

    // Tipo di documento da filtrare (INVOICE, CONTRACT, ecc.)
    private DocumentType documentType;

    // ID del documento. Se vuoto, non si applica filtro su ID specifico.
    private Long documentId;

    // Lista di azioni da filtrare (CREATE, UPDATE, DELETE...)
    // Memorizzata internamente come lista mutabile.
    private List<DocumentAction> actions = new ArrayList<>();

    // Data/ora di inizio filtro temporale.
    private Instant from;

    // Data/ora di fine filtro temporale.
    private Instant to;

    // Testo libero da cercare su descrizioni, documentId, ecc.
    private String searchText;

    // ===========================
    // GETTER / SETTER
    // ===========================

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    /**
     * Restituisce una lista NON modificabile.
     * Garantisce immutabilità verso l’esterno del modello.
     */
    public List<DocumentAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Setta la lista di azioni.
     * Copia la lista ricevuta per evitare modifiche esterne.
     * Se null, crea una lista vuota.
     */
    public void setActions(List<DocumentAction> actions) {
        this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * Genera una chiave univoca da usare per caching.
     *
     * L’idea è:
     * - ogni filtro viene rappresentato come stringa
     * - se il filtro non è impostato → "*"
     * - gli array vengono ordinati (evita inconsistenze)
     * - si includono page e size per gestire la paginazione
     *
     * In output ottieni una stringa tipo:
     * INVOICE|123|CREATE,UPDATE|2024-01-01T00:00|2024-02-01T00:00|search|0|20
     */
    public String cacheKey(int page, int size) {
        StringJoiner joiner = new StringJoiner("|");

        // Tipo documento, "*" se null.
        joiner.add(documentType != null ? documentType.name() : "*");

        // ID documento, "*" se null.
        joiner.add(documentId != null ? documentId.toString() : "*");

        // Azioni, ordinate e unite con ",".
        if (!actions.isEmpty()) {
            joiner.add(
                    actions.stream()
                            .map(DocumentAction::name)
                            .sorted()
                            .reduce((a, b) -> a + "," + b)
                            .orElse(""));
        } else {
            joiner.add("*");
        }

        // Filtro data da / a.
        joiner.add(from != null ? from.toString() : "*");
        joiner.add(to != null ? to.toString() : "*");

        // Testo libero.
        joiner.add(searchText != null ? searchText : "*");

        // Parametri di paginazione.
        joiner.add(Integer.toString(page));
        joiner.add(Integer.toString(size));

        return joiner.toString();
    }
}
