package com.example.server.service;

import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.server.domain.DocumentHistory;
import com.example.server.repository.DocumentHistoryQueryRepository;
import com.example.server.repository.DocumentHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentHistoryServiceTest {

    private static final Instant NOW = Instant.parse("2024-03-10T12:00:00Z");

    @Mock
    private DocumentHistoryRepository repository;

    @Mock
    private DocumentHistoryQueryRepository queryRepository;

    private DocumentHistoryService service;

    @BeforeEach
    void setUp() {
        service = new DocumentHistoryService(repository, queryRepository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void shouldPersistLoggedEntryWithTimestamp() {
        DocumentHistory created = DocumentHistory.create(DocumentType.CONTRACT, 9L, DocumentAction.CREATED, "desc", NOW);
        when(repository.save(any())).thenReturn(created);

        DocumentHistory stored = service.log(DocumentType.CONTRACT, 9L, DocumentAction.CREATED, "desc");

        ArgumentCaptor<DocumentHistory> captor = ArgumentCaptor.forClass(DocumentHistory.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isEqualTo(NOW);
        assertThat(stored).isEqualTo(created);
    }

    @Test
    void shouldListEntriesForDocumentOrderedByDate() {
        List<DocumentHistory> entries = List.of(
                new DocumentHistory(1L, DocumentType.INVOICE, 4L, DocumentAction.CREATED, "first", NOW),
                new DocumentHistory(2L, DocumentType.INVOICE, 4L, DocumentAction.UPDATED, "second", NOW.plusSeconds(10))
        );
        when(repository.findByDocumentTypeAndDocumentIdOrderByCreatedAtDesc(DocumentType.INVOICE, 4L))
                .thenReturn(entries);

        List<DocumentHistory> result = service.list(DocumentType.INVOICE, 4L);

        assertThat(result).containsExactlyElementsOf(entries);
    }

    @Test
    void shouldSearchAndNormalizePagination() {
        DocumentHistoryQuery query = DocumentHistoryQuery.builder().size(500).page(1).build();
        DocumentHistoryQueryRepository.ResultPage emptyPage = new DocumentHistoryQueryRepository.ResultPage(List.of(), 0);
        when(queryRepository.find(any())).thenReturn(emptyPage);

        DocumentHistoryPageDTO page = service.search(query);

        ArgumentCaptor<DocumentHistoryQuery> normalized = ArgumentCaptor.forClass(DocumentHistoryQuery.class);
        verify(queryRepository).find(normalized.capture());
        assertThat(normalized.getValue().getSize()).isEqualTo(200);
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    void shouldExportCsvWithEscapedValues() {
        List<DocumentHistory> entries = List.of(
                new DocumentHistory(10L, DocumentType.CONTRACT, 3L, DocumentAction.DELETED, "descrizione \"con\" apici", NOW),
                new DocumentHistory(11L, DocumentType.CONTRACT, 3L, DocumentAction.CREATED, null, null)
        );
        DocumentHistoryQuery query = DocumentHistoryQuery.builder().documentType(DocumentType.CONTRACT).documentId(3L).build();
        when(queryRepository.findAll(any())).thenReturn(entries);

        String csv = new String(service.exportCsv(query));

        String[] lines = csv.split("\n");
        assertThat(lines[0]).isEqualTo("id;documentType;documentId;action;description;createdAt");
        assertThat(lines[1]).contains("10;CONTRACT;3;DELETED;\"descrizione \"\"con\"\" apici\";2024-03-10T12:00:00Z");
        assertThat(lines[2]).isEqualTo("11;CONTRACT;3;CREATED;\"\";");
    }
}
