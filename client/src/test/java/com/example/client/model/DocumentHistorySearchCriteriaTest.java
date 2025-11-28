package com.example.client.model;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentHistorySearchCriteriaTest {

    @Test
    void cacheKeyBuildsDeterministicAndOrderedRepresentation() {
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();
        criteria.setDocumentType(DocumentType.INVOICE);
        criteria.setDocumentId(42L);
        criteria.setActions(List.of(DocumentAction.UPDATED, DocumentAction.CREATED));
        criteria.setFrom(Instant.parse("2024-02-01T10:15:30.00Z"));
        criteria.setTo(Instant.parse("2024-02-28T10:15:30.00Z"));
        criteria.setSearchText("delta");

        String cacheKey = criteria.cacheKey(1, 25);

        assertEquals(
                "INVOICE|42|CREATED,UPDATED|2024-02-01T10:15:30Z|2024-02-28T10:15:30Z|delta|1|25",
                cacheKey,
                "La cacheKey deve includere tutti i campi in ordine stabile"
        );
    }

    @Test
    void cacheKeyHandlesMissingFilters() {
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();

        String cacheKey = criteria.cacheKey(0, 20);

        assertEquals("*|*|*|*|*|*|0|20", cacheKey, "I campi non impostati devono essere marcati con asterischi");
    }

    @Test
    void actionsListIsCopiedAndReturnedAsUnmodifiable() {
        List<DocumentAction> provided = new ArrayList<>(List.of(DocumentAction.DELETED));
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();

        criteria.setActions(provided);
        provided.add(DocumentAction.CREATED);

        assertEquals(List.of(DocumentAction.DELETED), criteria.getActions(), "Le modifiche esterne non devono riflettersi internamente");
        assertThrows(UnsupportedOperationException.class, () -> criteria.getActions().add(DocumentAction.UPDATED));
        assertTrue(criteria.getActions().contains(DocumentAction.DELETED));
    }
}
