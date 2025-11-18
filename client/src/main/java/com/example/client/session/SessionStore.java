package com.example.client.session;

import com.example.client.service.AuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SessionStore {

    private final ObjectMapper mapper;
    private final Path sessionFile;
    private AuthSession currentSession;

    public SessionStore() {
        this(Path.of(System.getProperty("user.home"), ".gestore-agenti-session.json"));
    }

    public SessionStore(Path sessionFile) {
        this.sessionFile = sessionFile;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public synchronized Optional<AuthSession> load() {
        if (!Files.exists(sessionFile)) {
            return Optional.empty();
        }
        try {
            byte[] content = Files.readAllBytes(sessionFile);
            AuthSession session = mapper.readValue(content, AuthSession.class);
            currentSession = session;
            return Optional.ofNullable(session);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public synchronized Optional<AuthSession> currentSession() {
        Optional<AuthSession> session = Optional.ofNullable(currentSession);
        if (session.isEmpty()) {
            session = load();
        }
        if (session.isPresent() && session.get().isExpired()) {
            clearSilently();
            currentSession = null;
            return Optional.empty();
        }
        return session;
    }

    public synchronized Optional<String> currentToken() {
        return currentSession().map(AuthSession::accessToken);
    }

    public void save(AuthSession session) throws IOException {
        if (sessionFile.getParent() != null) {
            Files.createDirectories(sessionFile.getParent());
        }
        byte[] payload = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(session);
        Files.write(sessionFile, payload);
        this.currentSession = session;
    }

    public synchronized void clear() throws IOException {
        Files.deleteIfExists(sessionFile);
        currentSession = null;
    }

    public Path getSessionFile() {
        return sessionFile;
    }

    private void clearSilently() {
        try {
            clear();
        } catch (IOException ignored) {
            // Ignora eventuali errori durante la pulizia della sessione
        }
    }
}
