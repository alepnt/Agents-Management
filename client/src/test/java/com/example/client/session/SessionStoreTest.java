package com.example.client.session;

import com.example.client.service.AuthSession;
import com.example.client.service.UserSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadRoundtripPersistSession() throws Exception {
        SessionStore store = new SessionStore(tempDir);
        AuthSession session = activeSession(Instant.now().plusSeconds(3600));

        store.save(session);

        assertTrue(Files.exists(store.getSessionsDirectory()), "La directory delle sessioni deve essere creata");
        Optional<AuthSession> loaded = store.load();

        assertTrue(loaded.isPresent(), "La sessione salvata deve essere caricata");
        assertEquals(session.accessToken(), loaded.get().accessToken());
        assertEquals(session.user().azureId(), store.currentSession().map(s -> s.user().azureId()).orElse(null));
        assertTrue(store.currentToken().isPresent(), "Il token corrente deve essere disponibile");
    }

    @Test
    void loadSkipsExpiredSessionAndCleansFiles() throws Exception {
        SessionStore store = new SessionStore(tempDir);
        AuthSession expired = activeSession(Instant.now().minusSeconds(5));

        Files.createDirectories(store.getSessionsDirectory());
        Path sessionFile = store.getSessionsDirectory().resolve(expired.user().azureId() + ".json");
        Files.writeString(sessionFile, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(expired));
        Files.writeString(tempDir.resolve("last-session"), expired.user().azureId());

        Optional<AuthSession> loaded = store.load();

        assertTrue(loaded.isEmpty(), "Una sessione scaduta non deve essere caricata");
        assertFalse(Files.exists(sessionFile), "I file di sessione scaduti devono essere rimossi");
        assertTrue(store.currentSession().isEmpty(), "La sessione corrente deve risultare assente");
    }

    @Test
    void clearRemovesSessionAndLastUser() throws Exception {
        SessionStore store = new SessionStore(tempDir);
        AuthSession session = activeSession(Instant.now().plusSeconds(3600));
        store.save(session);

        store.clear();

        assertTrue(store.currentSession().isEmpty(), "Dopo la clear non deve esserci una sessione attiva");
        assertFalse(store.currentToken().isPresent(), "Non deve essere disponibile alcun token dopo il clear");
        assertFalse(Files.exists(tempDir.resolve("last-session")), "Il file last-session deve essere eliminato");
    }

    private AuthSession activeSession(Instant expiresAt) {
        return new AuthSession(
                "token-test",
                "Bearer",
                expiresAt,
                new UserSummary(1L, "user@example.com", "User", "azure-1", 1L, 1L)
        );
    }
}
