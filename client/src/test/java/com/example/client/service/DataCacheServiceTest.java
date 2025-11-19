package com.example.client.service;

import com.example.client.command.CommandHistoryCaretaker;
import com.example.client.command.CommandMemento;
import com.example.client.command.CommandResult;
import com.example.client.model.DataChangeEvent;
import com.example.client.session.SessionStore;
import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.DocumentHistoryPageDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.common.observer.NotificationCenter;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataCacheServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void clearSessionDataResetsCachesAndObservers() throws Exception {
        SessionStore sessionStore = new SessionStore(tempDir);
        sessionStore.save(UiTestSessions.newSession("azure-1"));

        DataCacheService dataCacheService = DataCacheService.create(sessionStore);

        Map<Integer, AgentStatisticsDTO> agentCache = extractMap(dataCacheService, "agentStatsCache");
        Map<Integer, TeamStatisticsDTO> teamCache = extractMap(dataCacheService, "teamStatsCache");
        Map<String, DocumentHistoryPageDTO> historyCache = extractMap(dataCacheService, "historyCache");

        agentCache.put(2024, new AgentStatisticsDTO(2024, List.of(2024), List.of(), List.of()));
        teamCache.put(2024, new TeamStatisticsDTO(2024, List.of(2024), List.of()));
        historyCache.put("criteria-1", new DocumentHistoryPageDTO(List.<DocumentHistoryDTO>of(), 10, 0, 25));

        AtomicInteger historyNotifications = new AtomicInteger();
        CommandHistoryCaretaker caretaker = dataCacheService.getCaretaker();
        caretaker.subscribe(m -> historyNotifications.incrementAndGet());
        caretaker.addMemento(new CommandMemento("test", CommandResult.withHistory(null,
                1L,
                DocumentType.INVOICE,
                List.of(new DocumentHistoryDTO(1L,
                        DocumentType.INVOICE,
                        1L,
                        DocumentAction.CREATED,
                        "created",
                        Instant.now())))));

        NotificationCenter<DataChangeEvent> dataChangeCenter = extractDataChangeCenter(dataCacheService);
        dataCacheService.subscribeDataChanges(event -> { });
        assertEquals(1, dataChangeCenter.getObservers().size(), "Pre-condizione: observer registrato");
        assertTrue(caretaker.history().size() > 0, "Pre-condizione: history popolata");

        dataCacheService.clearSessionData();

        assertTrue(agentCache.isEmpty(), "La cache agenti deve essere svuotata");
        assertTrue(teamCache.isEmpty(), "La cache team deve essere svuotata");
        assertTrue(historyCache.isEmpty(), "La cache dello storico deve essere svuotata");
        assertTrue(caretaker.history().isEmpty(), "La history dei comandi deve essere svuotata");
        assertEquals(0, dataChangeCenter.getObservers().size(), "Gli observer dei cambi dati devono essere rimossi");

        caretaker.addMemento(new CommandMemento("another", CommandResult.withHistory(null,
                2L,
                DocumentType.INVOICE,
                List.<DocumentHistoryDTO>of())));
        assertEquals(1, historyNotifications.get(), "Gli observer rimossi non devono ricevere nuovi eventi");
    }

    @SuppressWarnings("unchecked")
    private <K, V> Map<K, V> extractMap(DataCacheService service, String fieldName) throws Exception {
        Field field = DataCacheService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Map<K, V>) field.get(service);
    }

    @SuppressWarnings("unchecked")
    private NotificationCenter<DataChangeEvent> extractDataChangeCenter(DataCacheService service) throws Exception {
        Field field = DataCacheService.class.getDeclaredField("dataChangeCenter");
        field.setAccessible(true);
        return (NotificationCenter<DataChangeEvent>) field.get(service);
    }

    private static class UiTestSessions {
        private static AuthSession newSession(String azureId) {
            return new AuthSession(
                    "token-" + azureId,
                    "Bearer",
                    Instant.now().plusSeconds(3600),
                    new UserSummary(1L, azureId + "@example.com", "User " + azureId, azureId, 1L, 1L),
                    null,
                    null
            );
        }
    }
}
