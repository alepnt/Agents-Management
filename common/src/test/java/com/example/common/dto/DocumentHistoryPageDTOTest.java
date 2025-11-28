package com.example.common.dto; // Package dei DTO condivisi, incluso il relativo test.

import org.junit.jupiter.api.Test; // Annotazione JUnit 5 per definire un test.

import java.util.List; // Utilizzato per creare liste di elementi di test.

import static org.assertj.core.api.Assertions.assertThat; // API AssertJ per asserzioni fluenti.

/**
 * Test di unità per la classe DocumentHistoryPageDTO.
 * Verifica comportamento di paginazione, gestione liste e uguaglianza.
 */
class DocumentHistoryPageDTOTest { // Classe di test JUnit 5.

    @Test
    void shouldCalculatePaginationData() { // Verifica corretta gestione di paginazione e flag.
        DocumentHistoryDTO history = new DocumentHistoryDTO(); // Crea elemento fittizio di storico.
        history.setId(42L); // Imposta ID per garantirne identità.
        DocumentHistoryPageDTO page = new DocumentHistoryPageDTO(List.of(history), 3, 0, 2);
        // Lista con 1 elemento, totale 3 elementi, pagina 0, size 2.

        assertThat(page.getItems()).containsExactly(history); // Verifica che la lista interna sia popolata
                                                              // correttamente.
        assertThat(page.getTotalElements()).isEqualTo(3); // Totale elementi disponibile.
        assertThat(page.getTotalPages()).isEqualTo(2); // 3 elementi / size 2 = 2 pagine.
        assertThat(page.hasNext()).isTrue(); // Essendo pagina 0, esiste pagina successiva.
        assertThat(page.hasPrevious()).isFalse(); // Prima pagina → nessuna pagina precedente.
    }

    @Test
    void shouldHandleEmptyOrNullCollections() { // Test sulla robustezza delle liste interne.
        DocumentHistoryPageDTO emptyPage = new DocumentHistoryPageDTO();
        // Costruttore vuoto: items = lista vuota.

        assertThat(emptyPage.getItems()).isEmpty(); // La lista deve essere vuota.
        assertThat(emptyPage.getTotalPages()).isEqualTo(1); // Pagine minime = 1.
        assertThat(emptyPage.hasNext()).isFalse(); // Nessuna pagina successiva.
        assertThat(emptyPage.hasPrevious()).isFalse(); // Nessuna pagina precedente.

        DocumentHistoryPageDTO page = new DocumentHistoryPageDTO();
        page.setItems(null); // Imposta lista a null → deve diventare vuota.
        assertThat(page.getItems()).isEmpty(); // Conferma comportamento corretto.
    }

    @Test
    void equalsShouldConsiderPaginationFields() { // Verifica equals/hashCode nella DTO.
        DocumentHistoryDTO history = new DocumentHistoryDTO();
        history.setId(1L); // Unico elemento condiviso per confronti.

        DocumentHistoryPageDTO first = new DocumentHistoryPageDTO(List.of(history), 5, 1, 2);
        DocumentHistoryPageDTO second = new DocumentHistoryPageDTO(List.of(history), 5, 1, 2);
        DocumentHistoryPageDTO different = new DocumentHistoryPageDTO(List.of(history), 4, 1, 2);
        // Differisce solo per totalElements → equals deve fallire.

        assertThat(first).isEqualTo(second); // I due oggetti sono equivalenti.
        assertThat(first).isNotEqualTo(different); // Cambiando il totale, non sono più uguali.
        assertThat(first.hashCode()).isEqualTo(second.hashCode()); // hashCode coerente con equals.
    }
} // Fine classe di test DocumentHistoryPageDTOTest.
