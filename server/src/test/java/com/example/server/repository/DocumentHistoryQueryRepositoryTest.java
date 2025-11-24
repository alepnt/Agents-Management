package com.example.server.repository;

import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import com.example.server.service.DocumentHistoryQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
class DocumentHistoryQueryRepositoryTest {

    @Autowired
    private DocumentHistoryQueryRepository repository;

    @Test
    void findShouldApplyFiltersPaginationAndOrdering() {
        DocumentHistoryQuery query = DocumentHistoryQuery.builder()
                .documentType(DocumentType.INVOICE)
                .documentId(10L)
                .actions(List.of(DocumentAction.CREATED, DocumentAction.UPDATED))
                .from(Instant.parse("2024-01-01T00:00:00Z"))
                .to(Instant.parse("2024-02-15T00:00:00Z"))
                .searchText("invoice")
                .page(0)
                .size(2)
                .build();

        DocumentHistoryQueryRepository.ResultPage page = repository.find(query);

        assertThat(page.items())
                .extracting(DocumentHistory::getId)
                .containsExactly(2L, 1L);
        assertThat(page.totalElements()).isEqualTo(2L);
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoRecordsMatch() {
        DocumentHistoryQuery query = DocumentHistoryQuery.builder()
                .documentType(DocumentType.CONTRACT)
                .documentId(99L)
                .searchText("missing")
                .from(Instant.parse("2024-01-01T00:00:00Z"))
                .to(Instant.parse("2024-02-01T00:00:00Z"))
                .build();

        List<DocumentHistory> results = repository.findAll(query);

        assertThat(results).isEmpty();
    }
}
