package com.example.client.auth; // Package dedicato all’autenticazione MSAL lato client.

import com.fasterxml.jackson.core.type.TypeReference; // Per deserializzare JSON del token ID.
import com.fasterxml.jackson.databind.ObjectMapper; // Mapper JSON condiviso.
import com.microsoft.aad.msal4j.*; // Import MSAL4J: account, token, eccezioni, parametri, client.
import java.io.IOException; // Necessario per deserializzazione idToken.
import java.net.MalformedURLException; // Validazione authority MSAL.
import java.net.URI; // Redirect URI e authority.
import java.time.Instant; // Per calcolare la scadenza dei token.
import java.util.Base64; // Per decodificare ID token JWT.
import java.util.Collections; // Collezioni vuote.
import java.util.Map; // Claims estratti dal token.
import java.util.Optional; // Risultati opzionali.
import java.util.Set; // Scopes.
import java.util.concurrent.CompletionException; // Errori async MSAL.

public class MsalTokenProvider implements TokenProvider { // Implementa la gestione token tramite MSAL4J.

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // Mapper JSON statico riutilizzato.

    private final PublicClientApplication application; // Client MSAL4J configurato per l’applicazione.
    private final URI redirectUri; // Redirect URI configurato.
    private final String authority; // Endpoint authority MSAL.
    private final Set<String> scopes; // Scopes richiesti.
    private final TokenCache cache; // Cache MSAL serializzata.
    private volatile IAuthenticationResult lastResult; // Ultimo token acquisito (per caching locale).

    public MsalTokenProvider(MsalConfiguration configuration) { // Costruttore basato su configurazione MSAL.
        this.redirectUri = configuration.redirectUri();
        this.scopes = Set.copyOf(configuration.scopes());
        this.authority = configuration.authority();
        this.cache = new TokenCache();
        try {
            this.application = PublicClientApplication.builder(configuration.clientId())
                    .authority(configuration.authority()) // Imposta authority.
                    .setTokenCacheAccessAspect(cache) // Aggancia cache MSAL personalizzata.
                    .build(); // Costruisce istanza MSAL.
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    "Authority MSAL non valida: " + configuration.authority(), e);
        }
    }

    public static TokenProvider fromEnvironment() { // Factory method per lettura configurazione da ENV.
        return new MsalTokenProvider(MsalConfiguration.fromEnvironment());
    }

    public static TokenProvider disabled(String reason) { // Factory per provider disattivato.
        return new DisabledTokenProvider(reason);
    }

    @Override
    public synchronized Optional<MsalAuthenticationResult> acquireTokenSilently()
            throws MsalAuthenticationException {

        if (lastResult != null && !MsalTokenProvider.this.isExpired(lastResult)) {
            return Optional.of(mapResult(lastResult)); // Se il token esiste ed è valido, restituiscilo.
        }

        try {
            Set<IAccount> accounts = application.getAccounts().join(); // Ottieni account presenti nella cache MSAL.

            if (accounts == null || accounts.isEmpty()) { // Se non ci sono account → richiesta interattiva.
                return Optional.empty();
            }

            IAccount account = accounts.iterator().next(); // Usa il primo account disponibile.
            SilentParameters parameters = SilentParameters.builder(scopes, account).build();
            lastResult = acquireTokenSilently(parameters); // Richiesta MSAL silenziosa.

            return Optional.of(mapResult(lastResult));

        } catch (CompletionException ex) { // Eccezioni async → unwrap.
            Throwable root = unwrap(ex);
            if (root instanceof MsalInteractionRequiredException) {
                return Optional.empty(); // L’utente deve interagire → ritorna empty.
            }
            throw asAuthenticationException("acquisizione silenziosa", root);

        } catch (MsalException ex) {
            throw asAuthenticationException("acquisizione silenziosa", ex);
        }
    }

    @Override
    public synchronized MsalAuthenticationResult acquireTokenInteractive()
            throws MsalAuthenticationException {

        try {
            InteractiveRequestParameters parameters = InteractiveRequestParameters.builder(redirectUri)
                    .scopes(scopes)
                    .build(); // Costruisce richiesta interattiva.

            lastResult = application.acquireToken(parameters).join();
            return mapResult(lastResult); // Mappa risultato MSAL → DTO.

        } catch (CompletionException ex) {
            throw asAuthenticationException("acquisizione interattiva", unwrap(ex));

        } catch (MsalException ex) {
            throw asAuthenticationException("acquisizione interattiva", ex);
        }
    }

    private boolean isExpired(IAuthenticationResult result) { // Controllo scadenza token locale.
        return result == null ||
                result.expiresOnDate() == null ||
                result.expiresOnDate().toInstant().isBefore(Instant.now());
    }

    private IAuthenticationResult acquireTokenSilently(SilentParameters parameters)
            throws MsalAuthenticationException {
        try {
            return application.acquireTokenSilently(parameters).join();
        } catch (MalformedURLException ex) {
            throw asAuthenticationException("acquisizione silenziosa", ex);
        }
    }

    private MsalAuthenticationResult mapResult(IAuthenticationResult result) { // Converte MSAL result → DTO.
        if (result == null) {
            return null;
        }

        IAccount account = result.account();
        Map<String, Object> claims = extractIdTokenClaims(result); // Estrae claims da idToken.

        String displayName = firstNonBlank(
                (String) claims.get("name"),
                account != null ? account.username() : null);

        String objectId = firstNonBlank(
                (String) claims.get("oid"),
                account != null ? account.homeAccountId() : null);

        String tenantId = (String) claims.get("tid");

        MsalAccount msalAccount = new MsalAccount(
                account != null ? account.username() : null,
                displayName,
                objectId,
                tenantId,
                account != null ? account.homeAccountId() : null);

        Instant expiresOn = result.expiresOnDate() != null
                ? result.expiresOnDate().toInstant()
                : null;

        return new MsalAuthenticationResult(
                result.accessToken(),
                null, // refreshToken non fornito da MSAL4J PublicClient.
                expiresOn,
                authority,
                msalAccount);
    }

    private Map<String, Object> extractIdTokenClaims(IAuthenticationResult result) { // Decodifica ID token JWT.
        try {
            String idToken = result.idToken();
            if (idToken == null || idToken.isBlank()) {
                return Collections.emptyMap();
            }
            String[] parts = idToken.split("\\."); // JWT → header.payload.signature
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return OBJECT_MAPPER.readValue(payload, new TypeReference<>() {
            });
        } catch (IllegalArgumentException | IOException e) {
            return Collections.emptyMap();
        }
    }

    private String firstNonBlank(String... values) { // Utility per selezionare primo valore non blank.
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private MsalAuthenticationException asAuthenticationException(String phase, Throwable error) {
        if (error instanceof MsalInteractionRequiredException interactionRequired) { // L'utente deve interagire.
            return new MsalAuthenticationException(
                    "È richiesta un'interazione utente per completare il login Microsoft",
                    interactionRequired);
        }

        if (error instanceof MsalServiceException serviceException) { // Errori servizi Microsoft.
            return new MsalAuthenticationException(
                    "Errore del servizio Microsoft durante " + phase + ": " + serviceException.errorCode(),
                    serviceException);
        }

        if (error instanceof MsalClientException clientException) { // Errori client MSAL (configurazione, rete…)
            return new MsalAuthenticationException(
                    "Errore di configurazione MSAL durante " + phase + ": " + clientException.errorCode(),
                    clientException);
        }

        String message = error != null ? error.getMessage() : "errore sconosciuto";
        return new MsalAuthenticationException(
                "Errore durante " + phase + ": " + message,
                error);
    }

    private Throwable unwrap(Throwable throwable) { // Risale alla causa reale degli errori async.
        Throwable current = throwable;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    // ---------------------- CACHE TOKEN MSAL ----------------------

    private static final class TokenCache implements ITokenCacheAccessAspect {

        private String cacheData; // Token cache serializzata in stringa.

        @Override
        public synchronized void beforeCacheAccess(ITokenCacheAccessContext context) {
            if (cacheData != null) { // Se esiste, deserializza la cache.
                context.tokenCache().deserialize(cacheData);
            }
        }

        @Override
        public synchronized void afterCacheAccess(ITokenCacheAccessContext context) {
            cacheData = context.tokenCache().serialize(); // Aggiorna stringa di cache dopo modifica.
        }
    }

    // ---------------- PROVIDER DISABILITATO -----------------------

    private static final class DisabledTokenProvider implements TokenProvider {

        private final String reason;

        private DisabledTokenProvider(String reason) { // Messaggio motivazione disable.
            this.reason = reason == null
                    ? "Servizio MSAL non configurato"
                    : reason;
        }

        @Override
        public Optional<MsalAuthenticationResult> acquireTokenSilently()
                throws MsalAuthenticationException {
            throw new MsalAuthenticationException(reason); // Sempre errore → provider inattivo.
        }

        @Override
        public MsalAuthenticationResult acquireTokenInteractive()
                throws MsalAuthenticationException {
            throw new MsalAuthenticationException(reason); // Sempre errore → provider inattivo.
        }
    }
}
