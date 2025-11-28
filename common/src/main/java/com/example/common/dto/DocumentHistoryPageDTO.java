package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.util.ArrayList;                                      // Implementazione mutabile di lista, usata per la copia difensiva.
import java.util.List;                                           // Interfaccia per collezioni ordinate.
import java.util.Objects;                                        // Utility per equals(), hashCode() e confronti null-safe.

/**
 * DTO che rappresenta una pagina di risultati dello storico documentale.
 * Include elementi, informazioni di paginazione e supporti utili ai test.
 */
public class DocumentHistoryPageDTO {                            // DTO mutabile per la gestione delle pagine di storico documentale.

    private List<DocumentHistoryDTO> items = new ArrayList<>();  // Lista degli elementi della pagina corrente.
    private long totalElements;                                   // Numero totale di elementi disponibili per la query.
    private int page;                                             // Numero della pagina corrente (0-based).
    private int size;                                             // Dimensione della pagina.

    public DocumentHistoryPageDTO() {                             // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public DocumentHistoryPageDTO(List<DocumentHistoryDTO> items, 
                                  long totalElements,
                                  int page,
                                  int size) {                     // Costruttore completo per inizializzazione manuale.
        this.items = items != null ? new ArrayList<>(items)      // Copia difensiva per evitare modifiche esterne.
                                   : new ArrayList<>();
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<DocumentHistoryDTO> getItems() {                  // Restituisce la lista degli elementi della pagina.
        return items;
    }

    public void setItems(List<DocumentHistoryDTO> items) {        // Imposta la lista degli elementi, applicando copia difensiva.
        this.items = items != null ? new ArrayList<>(items)
                                   : new ArrayList<>();
    }

    public long getTotalElements() {                              // Restituisce il numero totale di elementi.
        return totalElements;
    }

    public void setTotalElements(long totalElements) {            // Imposta il numero totale di elementi disponibili.
        this.totalElements = totalElements;
    }

    /**
     * Convenience accessor aligned with record-style naming used in tests.
     *
     * @return total number of elements available across all pages
     */
    public long totalElements() {                                 // Alias di getTotalElements(), utile nei test.
        return totalElements;
    }

    public int getPage() {                                        // Restituisce il numero di pagina corrente.
        return page;
    }

    public void setPage(int page) {                               // Imposta il numero della pagina corrente.
        this.page = page;
    }

    public int getSize() {                                        // Restituisce la dimensione della pagina.
        return size;
    }

    public void setSize(int size) {                               // Imposta la dimensione della pagina.
        this.size = size;
    }

    public long getTotalPages() {                                 // Calcola il numero totale di pagine.
        if (size <= 0) {                                          // Se la size è non valida, assume una singola pagina.
            return 1;
        }
        long pages = totalElements / size;                        // Divisione intera per calcolare le pagine piene.
        if (totalElements % size != 0) {                          // Se ci sono elementi residui → aggiungi una pagina extra.
            pages += 1;
        }
        return Math.max(pages, 1);                                // Garantisce almeno 1 pagina.
    }

    public boolean hasNext() {                                    // Indica se esiste una pagina successiva.
        return page + 1 < getTotalPages();
    }

    public boolean hasPrevious() {                                // Indica se esiste una pagina precedente.
        return page > 0 && getTotalPages() > 0;
    }

    @Override
    public boolean equals(Object o) {                             // Confronto tra due pagine basato sugli stessi campi.
        if (this == o) {                                          // Stessa istanza → uguali.
            return true;
        }
        if (!(o instanceof DocumentHistoryPageDTO that)) {        // Se tipo diverso → non uguali.
            return false;
        }
        return page == that.page                                  // Confronto dei campi principali.
                && size == that.size
                && totalElements == that.totalElements
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {                                       // Calcolo hash coerente con equals().
        return Objects.hash(items, totalElements, page, size);
    }
}                                                                  // Fine della classe DocumentHistoryPageDTO.
