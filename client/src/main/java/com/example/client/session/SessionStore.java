package com.example.client.session;

import com.example.client.service.AuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SessionStore {

    private final ObjectMapper mapper;
    private final Path baseDirectory;
    private final Path sessionsDirectory;
    private final Path lastSessionFile;
    private final java.util.Map<String, AuthSession> memoryCache = new java.util.HashMap<>();
    private AuthSession currentSession;
    private String currentAzureId;

    public SessionStore() {
        this(Path.of(System.getProperty("user.home"), ".gestore-agenti"));
    }

    public SessionStore(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.sessionsDirectory = baseDirectory.resolve("sessions");
        this.lastSessionFile = baseDirectory.resolve("last-session");
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public synchronized Optional<AuthSession> load() {
        if (currentSession != null && currentAzureId != null && !currentSession.isExpired()) {
            return Optional.of(currentSession);
        }
        Optional<String> lastAzureId = readLastAzureId();
        if (lastAzureId.isEmpty()) {
            return Optional.empty();
        }
        return loadForUser(lastAzureId.get());
    }

    public synchronized Optional<AuthSession> loadForUser(String azureId) {
        if (azureId == null || azureId.isBlank()) {
            return Optional.empty();
        }
        if (memoryCache.containsKey(azureId)) {
            AuthSession session = memoryCache.get(azureId);
            if (session != null && !session.isExpired()) {
                currentSession = session;
                currentAzureId = azureId;
                writeLastAzureIdSilently(azureId);
                return Optional.of(session);
            }
        }
        Path sessionFile = sessionFileFor(azureId);
        if (!Files.exists(sessionFile)) {
            return Optional.empty();
        }
        try {
            byte[] content = Files.readAllBytes(sessionFile);
            AuthSession session = mapper.readValue(content, AuthSession.class);
            if (session.isExpired()) {
                clearSilently(azureId);
                return Optional.empty();
            }
            memoryCache.put(azureId, session);
            currentSession = session;
            currentAzureId = azureId;
            writeLastAzureId(azureId);
            return Optional.of(session);
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
            currentAzureId = null;
            return Optional.empty();
        }
        return session;
    }

    public synchronized Optional<String> currentToken() {
        return currentSession().map(AuthSession::accessToken);
    }

    public synchronized void save(AuthSession session) throws IOException {
        if (session == null || session.user() == null || session.user().azureId() == null) {
            throw new IllegalArgumentException("Sessione non valida: azureId mancante");
        }
        saveForUser(session.user().azureId(), session);
    }

    public synchronized void saveForUser(String azureId, AuthSession session) throws IOException {
        if (session == null) {
            throw new IllegalArgumentException("Sessione non valida: session null");
        }
        if (azureId == null || azureId.isBlank()) {
            throw new IllegalArgumentException("azureId non valido per il salvataggio della sessione");
        }
        Files.createDirectories(sessionsDirectory);
        byte[] payload = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(session);
        Files.write(sessionFileFor(azureId), payload);
        memoryCache.put(azureId, session);
        writeLastAzureId(azureId);
        this.currentSession = session;
        this.currentAzureId = azureId;
    }

    public synchronized void clear() throws IOException {
        Optional<String> azureId = Optional.ofNullable(currentAzureId);
        if (azureId.isEmpty()) {
            azureId = readLastAzureId();
        }
        if (azureId.isEmpty()) {
            return;
        }
        clear(azureId.get());
    }

    public synchronized void clear(String azureId) throws IOException {
        if (azureId == null || azureId.isBlank()) {
            return;
        }
        Files.deleteIfExists(sessionFileFor(azureId));
        memoryCache.remove(azureId);
        if (azureId.equals(currentAzureId)) {
            currentSession = null;
            currentAzureId = null;
        }
        Optional<String> lastAzureId = readLastAzureId();
        if (lastAzureId.isPresent() && lastAzureId.get().equals(azureId)) {
            Files.deleteIfExists(lastSessionFile);
        }
    }

    public Path getSessionsDirectory() {
        return sessionsDirectory;
    }

    private Path sessionFileFor(String azureId) {
        return sessionsDirectory.resolve(azureId + ".json");
    }

    private Optional<String> readLastAzureId() {
        if (!Files.exists(lastSessionFile)) {
            return Optional.empty();
        }
        try {
            String content = Files.readString(lastSessionFile, StandardCharsets.UTF_8).trim();
            return content.isBlank() ? Optional.empty() : Optional.of(content);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void writeLastAzureId(String azureId) throws IOException {
        Files.createDirectories(baseDirectory);
        Files.writeString(lastSessionFile, azureId, StandardCharsets.UTF_8);
    }

    private void writeLastAzureIdSilently(String azureId) {
        try {
            writeLastAzureId(azureId);
        } catch (IOException ignored) {
            // Se non è possibile aggiornare il file, manteniamo comunque la cache in memoria
        }
    }

    private void clearSilently() {
        clearSilently(null);
    }

    private void clearSilently(String azureId) {
        try {
            if (azureId == null) {
                clear();
            } else {
                clear(azureId);
            }
        } catch (IOException ignored) {
            // Ignora eventuali errori durante la pulizia della sessione
        }
    }
}
