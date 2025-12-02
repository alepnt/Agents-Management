package com.example.client.service;
// Package che contiene i servizi che comunicano con il backend (API REST).

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.common.dto.RegistrationLookupDTO;
// Jackson per serializzazione JSON, con supporto a Instant/LocalDateTime.

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// HttpClient di Java 11+ per chiamate REST senza dipendenze esterne.

/**
 * Client HTTP dedicato alle operazioni di autenticazione:
 *
 * - login tramite Azure AD (access token)
 * - registrazione nuovi utenti
 *
 * È un componente molto leggero:
 * - usa HttpClient standard
 * - serializza e deserializza JSON tramite Jackson
 * - gestisce errori HTTP in modo centralizzato
 */
public class AuthApiClient {

    private final HttpClient httpClient;
    // HttpClient condiviso per tutte le chiamate.

    private final ObjectMapper mapper;
    // ObjectMapper configurato per Instant e altre date.

    private final String baseUrl;
    // URL base del backend (localhost:8080 per default).

    /**
     * Costruttore predefinito.
     * Punta all'istanza locale del backend.
     */
    public AuthApiClient() {
        this("http://localhost:8080");
    }

    /**
     * Permette di configurare un URL personalizzato (es: ambiente di produzione).
     */
    public AuthApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        // Necessario per deserializzare Instant e campi time-based.
    }

    /**
     * Esegue il login inviando al backend:
     * - accessToken ottenuto via MSAL
     * - email utente
     * - displayName
     * - azureId
     *
     * Restituisce:
     * - AuthSession → contiene token JWT backend, expire time, user info
     */
    public AuthSession login(LoginForm form) throws IOException, InterruptedException {

        // Crea payload JSON della richiesta login
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        serialize(new LoginPayload(
                                form.accessToken(),
                                form.email(),
                                form.displayName(),
                                form.azureId()))))
                .build();

        // Esegue richiesta sincronamente
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ensureSuccess(response);

        // Deserializza risposta JSON in AuthResponsePayload
        AuthResponsePayload payload = deserialize(response.body(), AuthResponsePayload.class);

        // Converte in AuthSession locale
        return new AuthSession(payload.accessToken(), payload.tokenType(), payload.expiresAt(), payload.user());
    }

    /**
     * Registra un nuovo utente.
     *
     * Campi richiesti:
     * - azureId
     * - email
     * - displayName
     * - agentCode (facoltativo)
     * - password
     * - teamName (facoltativo)
     * - roleName (facoltativo)
     *
     * Restituisce UserSummary per mostrare info di benvenuto.
     */
    public UserSummary register(RegisterForm form) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        serialize(new RegisterPayload(
                                form.azureId(),
                                form.email(),
                                form.displayName(),
                                form.agentCode(),
                                form.password(),
                                form.teamName(),
                                form.roleName()))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ensureSuccess(response);

        // Deserializza la risposta nel DTO UserSummary
        return deserialize(response.body(), UserSummary.class);
    }

    public RegistrationLookupDTO registrationLookups() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/register/lookups"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return deserialize(response.body(), RegistrationLookupDTO.class);
    }

    /**
     * Controlla che la risposta HTTP sia 2xx.
     * In caso contrario lancia IOException con messaggio dettagliato.
     */
    private void ensureSuccess(HttpResponse<String> response) throws IOException {
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException(
                    "Errore nella risposta del server: " +
                            status +
                            " - " +
                            response.body());
        }
    }

    /**
     * Serializza un oggetto qualsiasi in JSON.
     */
    private String serialize(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    /**
     * Deserializza JSON in una classe specifica.
     */
    private <T> T deserialize(String body, Class<T> type) throws JsonProcessingException {
        return mapper.readValue(body, type);
    }

    /**
     * Payload JSON inviato dal client per il login.
     */
    private record LoginPayload(
            String accessToken,
            String email,
            String displayName,
            String azureId) {
    }

    /**
     * Payload JSON inviato dal client per la registrazione.
     */
    private record RegisterPayload(
            String azureId,
            String email,
            String displayName,
            String agentCode,
            String password,
            String teamName,
            String roleName) {
    }

    /**
     * Payload JSON restituito dal backend durante il login.
     * Contiene:
     * - token di accesso
     * - tipo token (es: Bearer)
     * - scadenza
     * - user info
     */
    private record AuthResponsePayload(
            String accessToken,
            String tokenType,
            java.time.Instant expiresAt,
            UserSummary user) {
    }
}
