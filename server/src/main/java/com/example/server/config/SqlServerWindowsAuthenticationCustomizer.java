package com.example.server.config; // Dichiara il package in cui vive il customizer di autenticazione.

import com.zaxxer.hikari.HikariDataSource; // Importa il datasource HikariCP che verrà personalizzato.
import java.io.File; // Importa l'API per gestire separatori di percorso dipendenti dal sistema.
import java.lang.reflect.Field; // Importa la reflection per manipolare campi privati di ClassLoader.
import java.nio.file.Files; // Importa utility per verificare l'esistenza del percorso della DLL.
import java.nio.file.Path; // Importa il tipo che rappresenta un percorso di file.
import java.nio.file.Paths; // Importa la factory per costruire percorsi a partire da stringhe.
import java.util.Locale; // Importa la classe per gestire le maiuscole/minuscole in modo coerente.
import java.util.concurrent.atomic.AtomicBoolean; // Importa la classe per gestire flag thread-safe.
import org.slf4j.Logger; // Importa l'interfaccia di logging.
import org.slf4j.LoggerFactory; // Importa la factory per creare logger.
import org.springframework.beans.BeansException; // Importa l'eccezione generica di gestione bean.
import org.springframework.beans.factory.config.BeanPostProcessor; // Importa l'interfaccia per post-processare i bean.
import org.springframework.boot.context.properties.EnableConfigurationProperties; // Importa l'annotazione per attivare le properties.
import org.springframework.lang.NonNull; // Importa l'annotazione di non-nullabilità.
import org.springframework.stereotype.Component; // Importa l'annotazione per registrare il componente nel contesto.
import org.springframework.util.StringUtils; // Importa utility di Spring per controllare le stringhe.

/**
 * Configures the primary {@link javax.sql.DataSource} to use Windows integrated authentication when
 * requested through {@link DatabaseAuthenticationProperties}.
 */
@Component // Registra il customizer come componente Spring.
@EnableConfigurationProperties(DatabaseAuthenticationProperties.class) // Abilita il binding delle properties di autenticazione.
public class SqlServerWindowsAuthenticationCustomizer implements BeanPostProcessor { // Implementa la post-elaborazione dei bean per configurare il datasource.

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServerWindowsAuthenticationCustomizer.class); // Logger centralizzato per tracciare la configurazione.

    private final DatabaseAuthenticationProperties authenticationProperties; // Mantiene le impostazioni di autenticazione lette dal configuration file.
    private final AtomicBoolean libraryPathUpdated = new AtomicBoolean(false); // Evita di aggiornare java.library.path più di una volta.

    public SqlServerWindowsAuthenticationCustomizer(DatabaseAuthenticationProperties authenticationProperties) { // Costruttore che riceve le properties iniettate.
        this.authenticationProperties = authenticationProperties; // Salva le impostazioni ricevute.
    }

    @Override // Specifica che stiamo sovrascrivendo un metodo del BeanPostProcessor.
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) // Invocato dopo l'inizializzazione di ogni bean.
            throws BeansException { // Segnala che possono emergere eccezioni legate ai bean.
        if (bean instanceof HikariDataSource dataSource) { // Controlla se il bean è un datasource Hikari da configurare.
            configureForWindowsAuthenticationIfRequired(dataSource); // Applica la configurazione solo se richiesta.
        }
        return bean; // Restituisce comunque il bean, modificato o meno.
    }

    private void configureForWindowsAuthenticationIfRequired(HikariDataSource dataSource) { // Configura il datasource per l'autenticazione Windows se abilitata.
        if (!authenticationProperties.isWindowsAuthentication()) { // Verifica se la modalità Windows è effettivamente richiesta.
            return; // Esce senza modificare il datasource in caso contrario.
        }

        String jdbcUrl = dataSource.getJdbcUrl(); // Recupera l'URL JDBC configurato nel datasource.
        if (!isSqlServer(jdbcUrl)) { // Controlla che il datasource punti a SQL Server.
            return; // Esce se non è un URL SQL Server.
        }

        ensureIntegratedSecurityEnabled(dataSource, jdbcUrl); // Si assicura che l'opzione integratedSecurity sia presente nell'URL.
        dataSource.setUsername(null); // Rimuove lo username perché non necessario con Windows auth.
        dataSource.setPassword(null); // Rimuove la password per evitare conflitti con l'autenticazione integrata.
        dataSource.addDataSourceProperty("integratedSecurity", "true"); // Impone al driver l'uso dell'integrated security.

        if (StringUtils.hasText(authenticationProperties.getAuthenticationScheme())) { // Se uno schema specifico è stato indicato nelle properties.
            dataSource.addDataSourceProperty("authenticationScheme", authenticationProperties.getAuthenticationScheme()); // Propaga lo schema scelto al datasource.
        }

        updateNativeLibraryPathIfNecessary(); // Aggiorna java.library.path per trovare la DLL nativa se necessario.
        LOGGER.info("Configured SQL Server datasource '{}' to use Windows integrated authentication.", dataSource.getPoolName()); // Logga la riuscita della configurazione.
    }

    private boolean isSqlServer(String jdbcUrl) { // Determina se l'URL punta a SQL Server.
        return jdbcUrl != null && jdbcUrl.toLowerCase(Locale.ROOT).startsWith("jdbc:sqlserver:"); // Verifica non null e prefisso jdbc:sqlserver: in modo case-insensitive.
    }

    private void ensureIntegratedSecurityEnabled(HikariDataSource dataSource, String jdbcUrl) { // Garantisce che il parametro integratedSecurity sia attivato.
        if (jdbcUrl == null) { // Se l'URL non è valorizzato.
            return; // Non può proseguire con la modifica.
        }

        String lowerUrl = jdbcUrl.toLowerCase(Locale.ROOT); // Crea una versione lowercase per le verifiche.
        if (!lowerUrl.contains("integratedsecurity=true")) { // Controlla se l'opzione è già presente.
            StringBuilder builder = new StringBuilder(jdbcUrl); // Prepara un builder per modificare l'URL esistente.
            if (!jdbcUrl.endsWith(";")) { // Aggiunge il separatore se manca il punto e virgola finale.
                builder.append(';'); // Inserisce il separatore richiesto dal formato JDBC.
            }
            builder.append("integratedSecurity=true"); // Appende il flag di integrated security.
            dataSource.setJdbcUrl(builder.toString()); // Aggiorna l'URL del datasource con il nuovo valore.
        }
    }

    private void updateNativeLibraryPathIfNecessary() { // Aggiorna il percorso della libreria nativa se configurato e non ancora applicato.
        String nativeLibraryPath = authenticationProperties.getNativeLibraryPath(); // Recupera il percorso della DLL dal configuration file.
        if (!StringUtils.hasText(nativeLibraryPath)) { // Verifica che sia stato effettivamente valorizzato.
            return; // Esce se manca una configurazione.
        }

        if (!libraryPathUpdated.compareAndSet(false, true)) { // Controlla e aggiorna il flag atomico per evitare più esecuzioni.
            return; // Esce se l'aggiornamento è già stato eseguito.
        }

        Path dllDirectory = Paths.get(nativeLibraryPath).toAbsolutePath(); // Converte il percorso configurato in percorso assoluto.
        if (!Files.exists(dllDirectory)) { // Verifica l'esistenza della directory indicata.
            LOGGER.warn("The configured sqljdbc_auth.dll directory '{}' does not exist. Windows authentication may fail.", // Avvisa se la cartella non è presente.
                    dllDirectory); // Inserisce il percorso mancante nel log.
            return; // Interrompe la procedura perché la DLL non è raggiungibile.
        }

        String currentLibraryPath = System.getProperty("java.library.path", ""); // Recupera l'attuale java.library.path.
        String newLibraryPath = dllDirectory + File.pathSeparator + currentLibraryPath; // Costruisce il nuovo percorso concatenando la directory della DLL.
        System.setProperty("java.library.path", newLibraryPath); // Aggiorna la system property con il nuovo valore.

        try { // Tenta di forzare il ricaricamento della property nei class loader già creati.
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths"); // Accede al campo interno sys_paths tramite reflection.
            sysPathsField.setAccessible(true); // Rende il campo accessibile per la modifica.
            sysPathsField.set(null, null); // Reset del cache dei percorsi per obbligare il ricalcolo.
        } catch (NoSuchFieldException | IllegalAccessException ex) { // Gestisce eventuali eccezioni dovute alla reflection.
            LOGGER.warn("Unable to refresh java.library.path for SQL Server integrated authentication." // Logga un avviso se non riesce a resettare la cache.
                    + " The sqljdbc_auth.dll must be reachable through the system path.", ex); // Specifica che la DLL deve essere raggiungibile tramite il path di sistema.
        }
    }
} // Chiude la classe che personalizza il datasource per l'autenticazione Windows.
