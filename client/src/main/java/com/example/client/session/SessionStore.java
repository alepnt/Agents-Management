package com.example.client.session;
// Package dedicato alla gestione delle sessioni lato client.

import com.example.client.service.AuthSession;
// Rappresenta una sessione autenticata: token, scadenza, utente.

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// ObjectMapper configurato per leggere/scrivere file JSON delle sessioni
// e per gestire LocalDateTime/Instant tramite JavaTimeModule.

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
// File system API e Optional per gestione sicura dei ritorni.

/**
 * Gestisce la persistenza delle sessioni utente sul client.
 *
 * - Salva sessioni su file JSON
 * - Mantiene una cache in memoria
 * - Ripristina la sessione più recente all’avvio
 * - Gestisce scadenza token, cleanup, selezione utente corrente
 *
 * Il tutto in modo thread-safe (con synchronized).
 */
public class SessionStore {

    private final ObjectMapper mapper;
    // JSON Mapper per serializzare/deserializzare AuthSession.

    private final Path baseDirectory;
    // Directory base, es: ~/.gestore-agenti

    private final Path sessionsDirectory;
    // Directory dove sono salvate le sessioni utente: ~/.gestore-agenti/sessions

    private final Path lastSessionFile;
    // File che contiene l'azureId dell'ultima sessione utilizzata.

    private final java.util.Map<String, AuthSession> memoryCache = new java.util.HashMap<>();
    // Cache in memoria delle sessioni già caricate.

    private AuthSession currentSession;
    // Sessione attualmente attiva in memoria.

    private String currentAzureId;
    // AzureId dell'utente attualmente attivo.

    /**
     * Costruttore predefinito: salva la sessione nella directory personale utente.
     */
    public SessionStore() {
        this(Path.of(System.getProperty("user.home"), ".gestore-agenti"));
    }

    /**
     * Costruttore che consente di specificare una directory personalizzata.
     */
    public SessionStore(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.sessionsDirectory = baseDirectory.resolve("sessions");
        this.lastSessionFile = baseDirectory.resolve("last-session");

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        // Permette la gestione di Instant/LocalDateTime nel JSON.
    }

    /**
     * Carica la sessione attuale (se esiste e non è scaduta).
     */
    public synchronized Optional<AuthSession> load() {
        // Se è già in memoria ed è valida → usa quella.
        if (currentSession != null && currentAzureId != null && !currentSession.isExpired()) {
            return Optional.of(currentSession);
        }

        // Legge l’ultimo AzureID usato.
        Optional<String> lastAzureId = readLastAzureId();
        if (lastAzureId.isEmpty()) {
            return Optional.empty();
        }

        // Carica la sessione per quell’utente.
        return loadForUser(lastAzureId.get());
    }

    /**
     * Carica la sessione per uno specifico AzureID.
     */
    public synchronized Optional<AuthSession> loadForUser(String azureId) {
        if (azureId == null || azureId.isBlank()) {
            return Optional.empty();
        }

        // Controlla prima la cache in memoria.
        if (memoryCache.containsKey(azureId)) {
            AuthSession session = memoryCache.get(azureId);
            if (session != null && !session.isExpired()) {
                currentSession = session;
                currentAzureId = azureId;
                writeLastAzureIdSilently(azureId);
                return Optional.of(session);
            }
        }

        // File di sessione: ~/.gestore-agenti/sessions/<azureId>.json
        Path sessionFile = sessionFileFor(azureId);
        if (!Files.exists(sessionFile)) {
            return Optional.empty();
        }

        try {
            // Carica il file JSON della sessione.
            byte[] content = Files.readAllBytes(sessionFile);
            AuthSession session = mapper.readValue(content, AuthSession.class);

            // Sessione scaduta → cleanup automatico.
            if (session.isExpired()) {
                clearSilently(azureId);
                return Optional.empty();
            }

            // Aggiorna cache + variabili correnti.
            memoryCache.put(azureId, session);
            currentSession = session;
            currentAzureId = azureId;
            writeLastAzureId(azureId);

            return Optional.of(session);

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Restituisce la sessione attuale, se valida. Se non c’è prova a caricarla.
     */
    public synchronized Optional<AuthSession> currentSession() {
        Optional<AuthSession> session = Optional.ofNullable(currentSession);

        if (session.isEmpty()) {
            session = load();
        }

        // Sessione scaduta → reset.
        if (session.isPresent() && session.get().isExpired()) {
            clearSilently();
            currentSession = null;
            currentAzureId = null;
            return Optional.empty();
        }

        return session;
    }

    /**
     * Ritorna il token della sessione attuale (se esiste ed è valido).
     */
    public synchronized Optional<String> currentToken() {
        return currentSession().map(AuthSession::accessToken);
    }

    /**
     * Salva una sessione per l'utente contenuto nella sessione.
     */
    public synchronized void save(AuthSession session) throws IOException {
        if (session == null || session.user() == null || session.user().azureId() == null) {
            throw new IllegalArgumentException("Sessione non valida: azureId mancante");
        }
        saveForUser(session.user().azureId(), session);
    }

    /**
     * Salva una sessione dati azureId esplicito.
     */
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

    /**
     * Cancella la sessione attuale.
     */
    public synchronized void clear() throws IOException {
        Optional<String> azureId = Optional.ofNullable(currentAzureId);

        // Se non c’è sessione corrente, prova a leggere il file last-session.
        if (azureId.isEmpty()) {
            azureId = readLastAzureId();
        }

        if (azureId.isEmpty()) {
            return;
        }

        clear(azureId.get());
    }

    /**
     * Cancella la sessione per uno specifico AzureID.
     */
    public synchronized void clear(String azureId) throws IOException {
        if (azureId == null || azureId.isBlank()) {
            return;
        }

        Files.deleteIfExists(sessionFileFor(azureId));
        memoryCache.remove(azureId);

        // Se stiamo cancellando quella attuale, reset.
        if (azureId.equals(currentAzureId)) {
            currentSession = null;
            currentAzureId = null;
        }

        // Se il file last-session punta a questo AzureID → rimuovilo.
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
            // Fallisce silenziosamente ma mantiene la sessione in memoria.
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
            // Errori ignorati volutamente per non interrompere il flusso.
        }
    }
}
