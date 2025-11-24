package com.example.common.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentHistoryPageDTOTest {

    @Test
    void shouldCalculatePaginationData() {
        DocumentHistoryDTO history = new DocumentHistoryDTO();
        history.setId(42L);
        DocumentHistoryPageDTO page = new DocumentHistoryPageDTO(List.of(history), 3, 0, 2);

        assertThat(page.getItems()).containsExactly(history);
        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
    }

    @Test
    void shouldHandleEmptyOrNullCollections() {
        DocumentHistoryPageDTO emptyPage = new DocumentHistoryPageDTO();
        assertThat(emptyPage.getItems()).isEmpty();
        assertThat(emptyPage.getTotalPages()).isEqualTo(1);
        assertThat(emptyPage.hasNext()).isFalse();
        assertThat(emptyPage.hasPrevious()).isFalse();

        DocumentHistoryPageDTO page = new DocumentHistoryPageDTO();
        page.setItems(null);
        assertThat(page.getItems()).isEmpty();
    }

    @Test
    void equalsShouldConsiderPaginationFields() {
        DocumentHistoryDTO history = new DocumentHistoryDTO();
        history.setId(1L);
        DocumentHistoryPageDTO first = new DocumentHistoryPageDTO(List.of(history), 5, 1, 2);
        DocumentHistoryPageDTO second = new DocumentHistoryPageDTO(List.of(history), 5, 1, 2);
        DocumentHistoryPageDTO different = new DocumentHistoryPageDTO(List.of(history), 4, 1, 2);

        assertThat(first).isEqualTo(second);
        assertThat(first).isNotEqualTo(different);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}
