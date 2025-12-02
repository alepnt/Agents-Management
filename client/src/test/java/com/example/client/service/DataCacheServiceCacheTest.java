package com.example.client.service;

import com.example.client.model.DocumentHistorySearchCriteria;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.client.session.SessionStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DataCacheServiceCacheTest {

    @TempDir
    Path tempDir;

    @Test
    void searchDocumentHistoryCachesResultsPerCriteria() throws Exception {
        StubGateway stubGateway = new StubGateway();
        DataCacheService dataCacheService = createServiceWithGateway(stubGateway);
        DocumentHistorySearchCriteria criteria = new DocumentHistorySearchCriteria();
        criteria.setDocumentType(DocumentType.INVOICE);

        DocumentHistoryPageDTO firstPage = dataCacheService.searchDocumentHistory(criteria, 0, 10);
        DocumentHistoryPageDTO secondPage = dataCacheService.searchDocumentHistory(criteria, 0, 10);

        assertSame(firstPage, secondPage, "La stessa pagina deve essere restituita dal cache store");
        assertEquals(1, stubGateway.historyCalls.get(), "Il backend deve essere invocato una sola volta per criteri identici");
    }

    @Test
    void getAgentStatisticsUsesCachePerYear() throws Exception {
        StubGateway stubGateway = new StubGateway();
        DataCacheService dataCacheService = createServiceWithGateway(stubGateway);

        AgentStatisticsDTO first = dataCacheService.getAgentStatistics(2024, null, null, null);
        AgentStatisticsDTO second = dataCacheService.getAgentStatistics(2024, null, null, null);

        assertSame(first, second, "La statistica deve essere letta dalla cache per lo stesso anno");
        assertEquals(1, stubGateway.agentStatsCalls.get(), "La chiamata al backend deve essere eseguita solo una volta per anno");
    }

    @Test
    void exportDocumentHistorySkipsBackendWhenCriteriaMissing() throws Exception {
        StubGateway stubGateway = new StubGateway();
        DataCacheService dataCacheService = createServiceWithGateway(stubGateway);

        byte[] exported = dataCacheService.exportDocumentHistory(null);

        assertArrayEquals(new byte[0], exported, "Senza criteri non devono essere scaricati dati");
        assertEquals(0, stubGateway.exportCalls.get(), "Il backend non deve essere invocato quando i criteri sono null");
    }

    private DataCacheService createServiceWithGateway(StubGateway gateway) throws Exception {
        SessionStore sessionStore = new SessionStore(tempDir);
        sessionStore.save(new AuthSession(
                "token-test",
                "Bearer",
                Instant.now().plusSeconds(3600),
                new UserSummary(1L, "user@example.com", "User", "azure-1", 1L, 1L)
        ));

        DataCacheService service = DataCacheService.create(sessionStore);
        injectField(service, "backendGateway", gateway);
        return service;
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static class StubGateway extends BackendGateway {
        private final AtomicInteger historyCalls = new AtomicInteger();
        private final AtomicInteger agentStatsCalls = new AtomicInteger();
        private final AtomicInteger exportCalls = new AtomicInteger();
        private final DocumentHistoryPageDTO historyPage = new DocumentHistoryPageDTO(
                List.of(new DocumentHistoryDTO(
                        1L,
                        DocumentType.INVOICE,
                        99L,
                        DocumentAction.CREATED,
                        "Creato",
                        Instant.parse("2024-01-01T00:00:00Z"))),
                1,
                0,
                10
        );

        @Override
        public DocumentHistoryPageDTO searchDocumentHistory(DocumentType documentType,
                                                            Long documentId,
                                                            List<DocumentAction> actions,
                                                            Instant from,
                                                            Instant to,
                                                            String search,
                                                            int page,
                                                            int size) {
            historyCalls.incrementAndGet();
            return historyPage;
        }

        @Override
        public AgentStatisticsDTO agentStatistics(Integer year, java.time.LocalDate from, java.time.LocalDate to, Long roleId) {
            agentStatsCalls.incrementAndGet();
            int resolvedYear = year != null ? year : 2025;
            return new AgentStatisticsDTO(resolvedYear, List.of(resolvedYear), List.of(), List.of());
        }

        @Override
        public byte[] exportDocumentHistory(DocumentType documentType,
                                            Long documentId,
                                            List<DocumentAction> actions,
                                            Instant from,
                                            Instant to,
                                            String search) {
            exportCalls.incrementAndGet();
            return "export".getBytes();
        }
    }
}
