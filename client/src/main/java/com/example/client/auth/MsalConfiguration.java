package com.example.client.auth; // Package dedicato alle configurazioni e oggetti MSAL lato client.

import java.net.URI; // Tipo per rappresentare URI assoluti (authority, redirect).
import java.util.Arrays; // Utility per costruire liste dagli scope.
import java.util.LinkedHashSet; // Set che mantiene l’ordine di inserimento.
import java.util.Objects; // Validazioni null-safe.
import java.util.Set; // Collezione degli scope.

/**
 * Configurazione MSAL lato client.
 * Contiene clientId, authority, redirectUri e gli scope richiesti per
 * l’autenticazione.
 */
public record MsalConfiguration(
        String clientId, // Client ID dell'app registrata in Azure AD.
        String authority, // Endpoint di autorizzazione (tenant o /common).
        URI redirectUri, // Redirect URI configurato in Azure App Registration.
        Set<String> scopes // Scopes OAuth2 richiesti.
) {

    /**
     * Compact constructor del record.
     * Esegue validazioni su tutti i campi alla costruzione dell’istanza.
     */
    public MsalConfiguration {
        Objects.requireNonNull(clientId, "clientId"); // clientId obbligatorio.
        Objects.requireNonNull(authority, "authority"); // authority obbligatoria.
        Objects.requireNonNull(redirectUri, "redirectUri"); // redirectUri obbligatoria.

        validateAuthority(authority); // Valida che authority sia un URL/MSAL valido.
        validateRedirectUri(redirectUri); // Valida schema e struttura del redirectUri.

        if (scopes == null || scopes.isEmpty()) { // Gli scope devono essere presenti.
            throw new IllegalArgumentException("Almeno uno scope MSAL è richiesto");
        }
    }

    /**
     * Costruisce una configurazione MSAL leggendo valori da:
     * - system properties (es. -Dmsal.clientId=…)
     * - environment variables
     * - default incorporati (authority default, scopes di Graph .default)
     */
    public static MsalConfiguration fromEnvironment() {
        String clientId = firstNonBlank( // ClientId preso da property → env.
                System.getProperty("msal.clientId"),
                System.getenv("MSAL_CLIENT_ID"));

        String authority = firstNonBlank( // Authority con fallback a /common.
                System.getProperty("msal.authority"),
                System.getenv("MSAL_AUTHORITY"),
                "https://login.microsoftonline.com/common");

        String redirect = firstNonBlank( // RedirectUri obbligatoria.
                System.getProperty("msal.redirectUri"),
                System.getenv("MSAL_REDIRECT_URI"));

        String scopes = firstNonBlank( // Scope multipli separati da virgola.
                System.getProperty("msal.scopes"),
                System.getenv("MSAL_SCOPES"),
                "https://graph.microsoft.com/.default");

        if (clientId == null || clientId.isBlank()) { // Validazione esplicita delle variabili obbligatorie.
            throw new IllegalStateException("Variabile MSAL_CLIENT_ID non configurata");
        }
        if (redirect == null || redirect.isBlank()) {
            throw new IllegalStateException("Variabile MSAL_REDIRECT_URI non configurata");
        }

        // Parsing scopes → rimuove stringhe vuote e preserva l'ordine.
        Set<String> scopeSet = new LinkedHashSet<>(Arrays.asList(scopes.split(",")));
        scopeSet.removeIf(String::isBlank);
        if (scopeSet.isEmpty()) {
            throw new IllegalStateException("Nessuno scope valido configurato per MSAL");
        }

        return new MsalConfiguration(
                clientId.trim(),
                authority.trim(),
                URI.create(redirect.trim()),
                scopeSet);
    }

    /**
     * Restituisce il primo valore non nullo e non blank tra quelli forniti.
     * Usato per property/env con fallback.
     */
    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) { // Ritorna il primo valore utile.
                return value;
            }
        }
        return null; // Nessun valore valido trovato.
    }

    /**
     * Valida che l'authority MSAL sia un URL assoluto valido.
     */
    private static void validateAuthority(String authority) {
        try {
            URI authorityUri = URI.create(authority); // Parsing URI.
            if (!authorityUri.isAbsolute()
                    || authorityUri.getScheme() == null
                    || authorityUri.getHost() == null) { // Richiede schema + host.
                throw new IllegalArgumentException("L'authority MSAL deve essere un URL assoluto valido");
            }
        } catch (IllegalArgumentException e) { // Wrap con messaggio più esplicito.
            throw new IllegalArgumentException("Authority MSAL non valida: " + authority, e);
        }
    }

    /**
     * Valida il redirect URI assicurando che sia assoluto e provvisto di schema.
     */
    private static void validateRedirectUri(URI redirectUri) {
        if (!redirectUri.isAbsolute() // Richiede URI assoluto.
                || redirectUri.getScheme() == null) { // Richiede schema (es. https).
            throw new IllegalArgumentException(
                    "Il redirect URI MSAL deve essere un URI assoluto con schema");
        }
    }
}
