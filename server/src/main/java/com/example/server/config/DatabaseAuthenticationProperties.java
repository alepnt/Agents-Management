package com.example.server.config; // Dichiara il package delle configurazioni applicative.

import org.springframework.boot.context.properties.ConfigurationProperties; // Importa il supporto per mappare proprietà esterne in un oggetto.
import org.springframework.util.StringUtils; // Importa le utility di Spring per gestire stringhe e validazioni.

@ConfigurationProperties(prefix = "app.database") // Collega le proprietà con prefisso "app.database" a questo bean.
public class DatabaseAuthenticationProperties { // Definisce la classe che incapsula le impostazioni di autenticazione del database.

    public enum AuthenticationMode { // Elenca le modalità di autenticazione possibili per il datasource.
        SQL, // Identifica l'uso di credenziali SQL classiche.
        WINDOWS // Identifica l'uso dell'autenticazione integrata Windows.
    }

    private AuthenticationMode authenticationMode = AuthenticationMode.SQL; // Modalità di autenticazione predefinita impostata su SQL.

    /**
     * Authentication scheme passed to the SQL Server JDBC driver when integrated security is enabled.
     * Defaults to {@code NativeAuthentication} which lets the driver pick the appropriate mechanism.
     */
    private String authenticationScheme = "NativeAuthentication"; // Schema di autenticazione usato dal driver JDBC quando serve l'integrated security.

    /**
     * Optional path to the folder that contains the {@code sqljdbc_auth.dll} library required for
     * Windows integrated authentication. When specified the path is prepended to the
     * {@code java.library.path} system property.
     */
    private String nativeLibraryPath; // Percorso opzionale alla cartella che ospita la libreria nativa sqljdbc_auth.dll.

    public AuthenticationMode getAuthenticationMode() { // Espone la modalità di autenticazione configurata.
        return authenticationMode; // Restituisce la modalità attuale.
    }

    public void setAuthenticationMode(AuthenticationMode authenticationMode) { // Imposta la modalità di autenticazione desiderata.
        this.authenticationMode = authenticationMode == null ? AuthenticationMode.SQL : authenticationMode; // Usa SQL come fallback se il valore è nullo.
    }

    public String getAuthenticationScheme() { // Restituisce lo schema di autenticazione configurato.
        return authenticationScheme; // Ritorna la stringa attualmente salvata.
    }

    public void setAuthenticationScheme(String authenticationScheme) { // Imposta lo schema di autenticazione se valorizzato.
        this.authenticationScheme = StringUtils.hasText(authenticationScheme) // Verifica che sia non vuoto
                ? authenticationScheme // usa il valore fornito
                : "NativeAuthentication"; // oppure torna al default.
    }

    public String getNativeLibraryPath() { // Espone il percorso configurato alla libreria nativa.
        return nativeLibraryPath; // Restituisce il path o null se non impostato.
    }

    public void setNativeLibraryPath(String nativeLibraryPath) { // Imposta il percorso alla libreria nativa se presente.
        this.nativeLibraryPath = StringUtils.hasText(nativeLibraryPath) ? nativeLibraryPath : null; // Salva il valore solo se non vuoto.
    }

    public boolean isWindowsAuthentication() { // Indica se la modalità corrente richiede l'autenticazione Windows.
        return authenticationMode == AuthenticationMode.WINDOWS; // Restituisce true solo quando è selezionata la modalità WINDOWS.
    }
} // Chiude la classe delle proprietà di autenticazione del database.
