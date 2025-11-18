package com.example.server.service;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentHistoryQueryTest {

    @Test
    void shouldBuildQueryWithDefaultsWhenNoFilters() {
        DocumentHistoryQuery query = DocumentHistoryQuery.builder()
                .size(0)
                .build();

        assertEquals(25, query.getSize(), "size should default to 25 when no filters are provided");
        assertEquals(0, query.getPage(), "page should default to zero");
        assertTrue(query.isPaginated());
        assertEquals(0, query.offset());
        assertEquals("*|*|*|*|*|*|0|25", query.cacheKey());
    }

    @Test
    void shouldNormalizeSearchTextAndPagination() {
        DocumentHistoryQuery query = DocumentHistoryQuery.builder()
                .searchText("  prova  ")
                .page(-2)
                .size(-5)
                .build();

        assertEquals("prova", query.getSearchText());
        assertEquals(0, query.getPage());
        assertEquals(0, query.getSize());
        assertFalse(query.isPaginated());
        assertEquals(0, query.offset());
    }

    @Test
    void shouldCreateCacheKeyWithSortedActionsAndPlaceholders() {
        Instant from = Instant.parse("2024-01-01T10:15:30Z");
        Instant to = Instant.parse("2024-02-01T10:15:30Z");

        DocumentHistoryQuery query = DocumentHistoryQuery.builder()
                .documentType(DocumentType.INVOICE)
                .documentId(42L)
                .actions(List.of(DocumentAction.UPDATED, DocumentAction.CREATED))
                .from(from)
                .to(to)
                .searchText("ricerca")
                .page(2)
                .size(5)
                .build();

        assertEquals("INVOICE|42|CREATED,UPDATED|2024-01-01T10:15:30Z|2024-02-01T10:15:30Z|ricerca|2|5", query.cacheKey());
    }

    @Test
    void shouldCopyQueryWithoutPagination() {
        DocumentHistoryQuery original = DocumentHistoryQuery.builder()
                .documentType(DocumentType.CONTRACT)
                .documentId(7L)
                .actions(List.of(DocumentAction.DELETED))
                .searchText("termine")
                .page(1)
                .size(10)
                .build();

        DocumentHistoryQuery withoutPagination = original.withoutPagination();

        assertEquals(original.getDocumentType(), withoutPagination.getDocumentType());
        assertEquals(original.getDocumentId(), withoutPagination.getDocumentId());
        assertEquals(original.getActions(), withoutPagination.getActions());
        assertEquals(original.getSearchText(), withoutPagination.getSearchText());
        assertEquals(0, withoutPagination.getPage());
        assertEquals(0, withoutPagination.getSize());
        assertFalse(withoutPagination.isPaginated());
    }
}

