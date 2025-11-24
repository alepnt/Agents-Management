package com.example.server.service; // Definisce il package che contiene le classi di servizio.

import com.example.common.enums.DocumentAction; // Importa l'enum che rappresenta le azioni sui documenti.
import com.example.common.enums.DocumentType; // Importa l'enum che rappresenta i tipi di documento.

import java.time.Instant; // Importa la classe per gestire istanti temporali.
import java.util.ArrayList; // Importa l'implementazione di lista dinamica.
import java.util.Comparator; // Importa il comparatore per ordinamenti personalizzati.
import java.util.List; // Importa l'interfaccia List.
import java.util.Objects; // Importa utility per confronti e controlli null-safe.
import java.util.StringJoiner; // Importa la classe per concatenare stringhe con separatori.

/**
 * Oggetto immutabile che rappresenta i criteri di ricerca per lo storico documentale.
 */
public final class DocumentHistoryQuery { // Definisce una classe immutabile per la ricerca dello storico documenti.

    private final DocumentType documentType; // Tipo di documento da filtrare.
    private final Long documentId; // Identificativo del documento da filtrare.
    private final List<DocumentAction> actions; // Elenco delle azioni da includere nella ricerca.
    private final Instant from; // Data di inizio per il filtro temporale.
    private final Instant to; // Data di fine per il filtro temporale.
    private final String searchText; // Testo libero da cercare nella descrizione.
    private final int page; // Numero di pagina richiesto.
    private final int size; // Dimensione della pagina.

    private DocumentHistoryQuery(Builder builder) { // Costruttore privato che riceve il builder.
        this.documentType = builder.documentType; // Assegna il tipo di documento dal builder.
        this.documentId = builder.documentId; // Assegna l'id del documento dal builder.
        this.actions = builder.actions; // Assegna la lista di azioni dal builder.
        this.from = builder.from; // Assegna la data di inizio dal builder.
        this.to = builder.to; // Assegna la data di fine dal builder.
        this.searchText = builder.searchText; // Assegna il testo di ricerca dal builder.
        this.page = builder.page; // Assegna il numero di pagina dal builder.
        this.size = builder.size; // Assegna la dimensione pagina dal builder.
    }

    public static Builder builder() { // Factory method per ottenere un nuovo builder.
        return new Builder(); // Restituisce un builder vuoto.
    }

    public DocumentType getDocumentType() { // Restituisce il tipo di documento filtrato.
        return documentType; // Ritorna il valore immutabile.
    }

    public Long getDocumentId() { // Restituisce l'id del documento filtrato.
        return documentId; // Ritorna l'identificativo.
    }

    public List<DocumentAction> getActions() { // Restituisce le azioni filtrate.
        return actions; // Ritorna la lista di azioni.
    }

    public Instant getFrom() { // Restituisce la data di inizio filtro.
        return from; // Ritorna l'istante iniziale.
    }

    public Instant getTo() { // Restituisce la data di fine filtro.
        return to; // Ritorna l'istante finale.
    }

    public String getSearchText() { // Restituisce il testo libero di ricerca.
        return searchText; // Ritorna la stringa impostata.
    }

    public int getPage() { // Restituisce il numero della pagina richiesta.
        return page; // Ritorna l'indice di pagina.
    }

    public int getSize() { // Restituisce la dimensione della pagina.
        return size; // Ritorna il numero di elementi per pagina.
    }

    public boolean isPaginated() { // Indica se la ricerca prevede paginazione.
        return size > 0; // True se la dimensione della pagina è maggiore di zero.
    }

    public int offset() { // Calcola l'offset di partenza per la query.
        return Math.max(page, 0) * Math.max(size, 0); // Moltiplica pagina e dimensione assicurandosi che non siano negativi.
    }

    public DocumentHistoryQuery withoutPagination() { // Restituisce una copia senza paginazione.
        return builder() // Avvia un nuovo builder.
                .documentType(documentType) // Copia il tipo di documento.
                .documentId(documentId) // Copia l'id del documento.
                .actions(actions) // Copia la lista delle azioni.
                .from(from) // Copia la data di inizio.
                .to(to) // Copia la data di fine.
                .searchText(searchText) // Copia il testo di ricerca.
                .page(0) // Imposta la pagina a zero.
                .size(0) // Imposta la dimensione a zero per disabilitare la paginazione.
                .build(); // Costruisce la nuova istanza.
    }

    public String cacheKey() { // Costruisce una chiave per la cache basata sui criteri di ricerca.
        StringJoiner joiner = new StringJoiner("|"); // Crea un joiner con separatore pipe.
        joiner.add(documentType != null ? documentType.name() : "*"); // Aggiunge il tipo di documento o wildcard.
        joiner.add(documentId != null ? documentId.toString() : "*"); // Aggiunge l'id documento o wildcard.
        if (!actions.isEmpty()) { // Verifica se sono presenti azioni specifiche.
            List<String> sorted = actions.stream() // Converte le azioni in stream.
                    .map(DocumentAction::name) // Mappa ogni azione sul suo nome.
                    .sorted() // Ordina alfabeticamente.
                    .toList(); // Colleziona in una lista.
            joiner.add(String.join(",", sorted)); // Aggiunge l'elenco ordinato alla chiave.
        } else { // Se non ci sono azioni specificate.
            joiner.add("*"); // Aggiunge wildcard.
        }
        joiner.add(from != null ? from.toString() : "*"); // Aggiunge la data di inizio o wildcard.
        joiner.add(to != null ? to.toString() : "*"); // Aggiunge la data di fine o wildcard.
        joiner.add(searchText != null ? searchText : "*"); // Aggiunge il testo di ricerca o wildcard.
        joiner.add(Integer.toString(page)); // Aggiunge il numero di pagina.
        joiner.add(Integer.toString(size)); // Aggiunge la dimensione pagina.
        return joiner.toString(); // Restituisce la chiave finale.
    }

    @Override
    public boolean equals(Object o) { // Confronta questa istanza con un altro oggetto.
        if (this == o) { // Verifica se il riferimento è lo stesso.
            return true; // Ritorna true per lo stesso oggetto.
        }
        if (!(o instanceof DocumentHistoryQuery that)) { // Controlla che l'altro oggetto sia della stessa classe.
            return false; // Ritorna false se non compatibile.
        }
        return page == that.page // Confronta il numero di pagina.
                && size == that.size // Confronta la dimensione pagina.
                && documentType == that.documentType // Confronta il tipo di documento.
                && Objects.equals(documentId, that.documentId) // Confronta l'id documento gestendo i null.
                && Objects.equals(actions, that.actions) // Confronta la lista di azioni.
                && Objects.equals(from, that.from) // Confronta la data di inizio.
                && Objects.equals(to, that.to) // Confronta la data di fine.
                && Objects.equals(searchText, that.searchText); // Confronta il testo di ricerca.
    }

    @Override
    public int hashCode() { // Calcola l'hash dell'istanza.
        return Objects.hash(documentType, documentId, actions, from, to, searchText, page, size); // Usa tutti i campi per l'hash.
    }

    public static final class Builder { // Builder per costruire DocumentHistoryQuery in modo fluente.
        private DocumentType documentType; // Tipo di documento da impostare.
        private Long documentId; // Id del documento da impostare.
        private List<DocumentAction> actions = new ArrayList<>(); // Lista di azioni inizializzata vuota.
        private Instant from; // Data di inizio da impostare.
        private Instant to; // Data di fine da impostare.
        private String searchText; // Testo di ricerca da impostare.
        private int page = 0; // Numero di pagina di default.
        private int size = 25; // Dimensione pagina di default.

        private Builder() { // Costruttore privato per impedire istanziazioni esterne.
        }

        private Builder(DocumentHistoryQuery query) { // Costruttore che copia i valori da una query esistente.
            this.documentType = query.documentType; // Copia il tipo di documento.
            this.documentId = query.documentId; // Copia l'id del documento.
            this.actions = new ArrayList<>(query.actions); // Copia la lista di azioni.
            this.from = query.from; // Copia la data di inizio.
            this.to = query.to; // Copia la data di fine.
            this.searchText = query.searchText; // Copia il testo di ricerca.
            this.page = query.page; // Copia la pagina.
            this.size = query.size; // Copia la dimensione pagina.
        }

        public Builder documentType(DocumentType documentType) { // Imposta il tipo di documento.
            this.documentType = documentType; // Memorizza il tipo indicato.
            return this; // Restituisce il builder per chaining.
        }

        public Builder documentId(Long documentId) { // Imposta l'id del documento.
            this.documentId = documentId; // Memorizza l'id indicato.
            return this; // Restituisce il builder per chaining.
        }

        public Builder actions(List<DocumentAction> actions) { // Imposta le azioni da filtrare.
            this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>(); // Copia le azioni o inizializza una lista vuota.
            return this; // Restituisce il builder per chaining.
        }

        public Builder from(Instant from) { // Imposta la data di inizio.
            this.from = from; // Memorizza l'istante di inizio.
            return this; // Restituisce il builder per chaining.
        }

        public Builder to(Instant to) { // Imposta la data di fine.
            this.to = to; // Memorizza l'istante di fine.
            return this; // Restituisce il builder per chaining.
        }

        public Builder searchText(String searchText) { // Imposta il testo di ricerca.
            this.searchText = searchText; // Memorizza il testo indicato.
            return this; // Restituisce il builder per chaining.
        }

        public Builder page(int page) { // Imposta il numero di pagina.
            this.page = Math.max(page, 0); // Usa il valore massimo tra pagina e zero per evitare negativi.
            return this; // Restituisce il builder per chaining.
        }

        public Builder size(int size) { // Imposta la dimensione della pagina.
            this.size = size; // Memorizza la dimensione indicata.
            return this; // Restituisce il builder per chaining.
        }

        public DocumentHistoryQuery build() { // Costruisce l'istanza finale.
            if (documentId == null && documentType == null && actions.isEmpty() && searchText == null && from == null && to == null) { // Verifica se non è stato impostato alcun filtro.
                // allow retrieving everything but make sure pagination is enabled to avoid huge responses
                if (size <= 0) { // Se la dimensione non è positiva.
                    size = 25; // Imposta una dimensione di default.
                }
            }
            actions.sort(Comparator.comparing(Enum::name)); // Ordina le azioni per nome per avere coerenza.
            if (searchText != null && !searchText.isBlank()) { // Se è presente un testo di ricerca valido.
                searchText = searchText.trim(); // Rimuove gli spazi superflui.
            } else { // Se non è presente testo utile.
                searchText = null; // Azzerra il testo per evitare ricerche vuote.
            }
            if (size < 0) { // Controlla che la dimensione non sia negativa.
                size = 0; // Forza la dimensione a zero se negativa.
            }
            return new DocumentHistoryQuery(this); // Crea l'istanza immutabile con i valori del builder.
        }

        public Builder copyOf(DocumentHistoryQuery query) { // Crea una copia del builder da una query esistente.
            return new Builder(query); // Restituisce un nuovo builder popolato.
        }
    }
}
