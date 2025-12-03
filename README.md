# Gestore Agenti

Questo repository contiene un progetto multi-modulo per l'applicazione Gestore Agenti. Il modulo **server** espone le API REST basate su Spring Boot e utilizza Microsoft SQL Server come database principale. Per lo sviluppo locale viene attivato automaticamente il profilo `local`, che usa un database H2 in-memory e applica comunque le migrazioni Flyway; per collegarsi a un SQL Server reale abilita il profilo `sqlserver` (ad esempio con `SPRING_PROFILES_ACTIVE=sqlserver`).

## Prerequisiti

- JDK 21+
- Maven 3.9+
- Docker (necessario per i test di integrazione basati su Testcontainers)
- Un'istanza Microsoft SQL Server raggiungibile dalla JVM dell'applicazione

## Configurazione del database

Le credenziali del database e i parametri del connection pool sono gestiti tramite variabili d'ambiente. È possibile valorizzarle direttamente prima dell'avvio oppure aggiungerle a un file `.env` utilizzato dagli strumenti di deploy.

| Variabile                 | Default                                                                  | Descrizione                                                                    |
|---------------------------|--------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| `DB_URL`                  | `jdbc:sqlserver://localhost:1433;databaseName=gestoreagenti;encrypt=true;trustServerCertificate=true` | URL JDBC Microsoft SQL Server                                                   |
| `DB_USERNAME`             | `sa`                                                                     | Utente SQL Server                                                              |
| `DB_PASSWORD`             | `ChangeMe!`                                                              | Password SQL Server                                                            |
| `DB_AUTHENTICATION_MODE`  | `sql`                                                                    | `sql` per credenziali standard, `windows` per Integrated Security               |
| `DB_NATIVE_LIBRARY_PATH`  | (vuoto)                                                                 | Percorso alla cartella contenente `sqljdbc_auth.dll` (solo modalità `windows`) |
| `DB_MAX_POOL_SIZE`        | `10`                                                                     | Numero massimo di connessioni nel pool                                          |
| `DB_MIN_IDLE`             | `5`                                                                      | Connessioni minime inattive mantenute nel pool                                  |

## Migrazioni database

Flyway è abilitato di default e applica automaticamente le migrazioni allo startup. Le migrazioni creano lo schema applicativo e inseriscono i dati seed (ruoli e team) necessari all'inizializzazione.

Per applicare le migrazioni manualmente:

```bash
mvn -pl server flyway:migrate
```

## Build e packaging

Per compilare l'intero progetto e lanciare tutti i test (unitari e di integrazione):

```bash
mvn clean verify
```

Per verificare localmente i check di compilazione con i warning di null-safety abilitati (gli warning vengono trattati come err
ori):

```bash
mvn -ntp -DskipTests compile
```

Il modulo server può essere impacchettato come JAR eseguibile (Spring Boot riaggiunge il manifest principale durante la fase di
 `repackage`) con:

```bash
mvn -pl server -am clean package
```

### Avvio su Windows (PowerShell) o da Visual Studio Code

Per lavorare su Windows puoi usare la console **PowerShell** o il terminale integrato di **Visual Studio Code**.

1. Apri PowerShell (o il terminale di VS Code) nella cartella del progetto.
2. Esporta le variabili d'ambiente necessarie al database:

   ```powershell
   $Env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=gestoreagenti;encrypt=true;trustServerCertificate=true"
   $Env:DB_USERNAME = "sa"
   $Env:DB_PASSWORD = "ChangeMe!"
   $Env:DB_MAX_POOL_SIZE = "10"
   $Env:DB_MIN_IDLE = "5"
   ```

3. Compila e crea il JAR del server:

   ```powershell
   mvn -pl server -am clean package
   ```

4. Avvia l'applicazione:

   ```powershell
   java -jar server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar
   ```

Se utilizzi VS Code, gli stessi comandi funzionano nel terminale integrato. Puoi anche definire un file `.env` con le variabili e usare estensioni come *Env Files* per caricarle prima di eseguire i comandi Maven o `java -jar`.

### Avvio completo con script da desktop (PowerShell)

Per partire più velocemente da Windows è disponibile lo script `scripts/start-desktop.ps1`, che compila il server, lo avvia e lancia il client JavaFX nello stesso terminale.

1. (Opzionale) Prepara un file `.env` nella root del progetto con le variabili del database e di MSAL.
2. Apri PowerShell nella cartella `Agents-Management`.
3. Esegui lo script:

   ```powershell
   .\scripts\start-desktop.ps1
   ```

   - Usa `-Profile sqlserver` per attivare il profilo Spring `sqlserver`.
   - Usa `-JavafxPlatform win` se Maven non rileva automaticamente la piattaforma JavaFX.

Lo script arresta il server quando chiudi il client o interrompi l'esecuzione.

### Creare un collegamento sul desktop (Windows)

Per lanciare l'app con un doppio clic puoi generare un collegamento `.lnk` che richiama `start-desktop.ps1` con i parametri desiderati:

```powershell
./scripts/create-desktop-shortcut.ps1
```

- Usa `-Profile sqlserver` per creare il collegamento con il profilo `sqlserver`.
- Usa `-JavafxPlatform win` per fissare la piattaforma JavaFX sul collegamento.
- Cambia il nome del collegamento con `-ShortcutName "Gestore Agenti QA.lnk"`.
- Aggiungi `-Force` per sovrascrivere un collegamento già presente.

### Report di coverage

I test del modulo **server** sono strumentati con JaCoCo. Per generare e visualizzare il report di coverage:

1. Esegui i test con la fase `verify` (oppure `test` seguito da `jacoco:report`):

   ```bash
   mvn -pl server verify
   ```

2. Apri il report HTML generato in `server/target/site/jacoco/index.html` con il tuo browser.

### Note di rete

Per impostazione predefinita il proxy Maven è disattivato in `.mvn/settings.xml` per evitare errori come `Host sconosciuto (proxy)` su macchine senza proxy aziendale. Se lavori dietro un proxy abilitalo impostando `<active>true</active>` (o configurando host/porta corretti) nel file `.mvn/settings.xml` oppure nel tuo `~/.m2/settings.xml`. Senza proxy Maven utilizzerà l'accesso diretto a `https://repo.maven.apache.org`.

Il file risultante sarà disponibile in `server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar`.

### Avvio del client JavaFX

1. Verifica che il backend sia in esecuzione su `http://localhost:8080`.
2. Imposta le variabili d'ambiente richieste da MSAL prima di avviare il client. Usa i valori della tua registrazione applicativa su Azure AD:
   - `MSAL_CLIENT_ID`: l'**Application (client) ID** mostrato nella scheda **Overview** dell'app registrata.
   - `MSAL_REDIRECT_URI`: una **Redirect URI** configurata nell'app (tipo `http://localhost:53410`). Devi usare esattamente lo stesso valore dichiarato nel portale Azure.
   - `MSAL_SCOPES`: gli scope per cui vuoi richiedere il consenso, ad esempio `https://graph.microsoft.com/.default` se l'app usa i permessi delegati/configurati per Microsoft Graph.

   **Bypass in sviluppo (senza autenticazione Microsoft)**

   Se vuoi saltare del tutto il popup Microsoft durante lo sviluppo, imposta *lo stesso* segreto su client e server:

   - `MSAL_DEV_BYPASS_SECRET`: token condiviso che sostituisce l'access token reale.
   - `MSAL_DEV_BYPASS_AZURE_ID` (opzionale): GUID fittizio da inviare come Azure ID (default `dev-azure-id`).
   - `MSAL_DEV_BYPASS_EMAIL` / `MSAL_DEV_BYPASS_DISPLAY_NAME` (opzionali): dati fittizi per popolazione form.
   - lato server aggiungi `SECURITY_AZURE_DEV_BYPASS_SECRET` (oppure proprietà `security.azure.dev-bypass-secret`) con lo stesso valore di `MSAL_DEV_BYPASS_SECRET`.

   Con queste variabili il client genera un token fittizio e il server lo accetta senza contattare Azure AD.

   Esempio su shell Bash/PowerShell (sostituisci gli ID con i valori della tua registrazione app su Azure AD):

   ```bash
   export MSAL_CLIENT_ID="<Application (client) ID>"
   export MSAL_REDIRECT_URI="http://localhost:53410"
   export MSAL_SCOPES="https://graph.microsoft.com/.default"
   ```

   In PowerShell usa:

   ```powershell
   $Env:MSAL_CLIENT_ID = "<Application (client) ID>"
   $Env:MSAL_REDIRECT_URI = "http://localhost:53410"
   $Env:MSAL_SCOPES = "https://graph.microsoft.com/.default"
   ```

   Se le variabili non sono configurate l'applicazione termina con l'errore `MSAL_CLIENT_ID non configurato`.

3. Avvia il client dalla root del progetto:

   ```bash
   mvn -pl client -am javafx:run
   ```

   Su Windows puoi forzare il classifier corretto di JavaFX aggiungendo `-Djavafx.platform=win`.
4. Se Maven segnala `No plugin found for prefix 'javafx'`, controlla che il proxy non blocchi l'accesso a Maven Central (vedi sezione precedente) e rilancia il comando con `-U` per forzare l'aggiornamento delle dipendenze:

   ```bash
   mvn -pl client -am -U javafx:run
   ```

## Sezioni principali dell'interfaccia client

### ChatView

- Vista principale per la messaggistica tra agenti.
- Si apre dalla dashboard tramite il pulsante **Chat** nella toolbar superiore, che lancia una finestra dedicata collegata
  all'utente corrente.
- A sinistra mostra la lista delle conversazioni; selezionandone una si caricano i messaggi nella colonna di destra.
- In basso include un'area di testo “Scrivi un messaggio…” e il pulsante **Invia** per mandare nuovi messaggi nella conversazione selezionata.

### NotificationTabView

- Tab dedicata alle notifiche dell'utente, con lista e pulsante **Aggiorna** in alto.
- Quando collegata a un utente tramite `bindUser`, carica le notifiche iniziali e avvia il polling automatico per mantenerle aggiornate.
- Può essere aggiunta al `TabPane` principale della dashboard per esporre rapidamente la sezione notifiche.

## Test di integrazione

I test di integrazione utilizzano Testcontainers per avviare automaticamente un container Microsoft SQL Server. Assicurarsi che Docker sia in esecuzione prima di lanciare i test.

```bash
mvn -pl server -am test
```

## Deploy

1. Assicurarsi che il database SQL Server di destinazione sia raggiungibile e che l'utente configurato disponga dei privilegi necessari (creazione tabelle, lettura, scrittura).
2. Configurare le variabili `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_MAX_POOL_SIZE` e `DB_MIN_IDLE` nell'ambiente della macchina/servizio in cui verrà eseguita l'applicazione.
3. Copiare il JAR prodotto (`server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar`) sulla macchina di destinazione.
4. Avviare l'applicazione:

   ```bash
   java -jar server/target/gestore-agenti-server-0.0.1-SNAPSHOT.jar
   ```

Al primo avvio Flyway applicherà automaticamente lo schema e i dati seed.

## Documentazione funzionale

- [Gestione provvigioni e organizzazione agenti](docs/commissioni.md): requisiti condivisi con il cliente su struttura dei team, regole di provvigione e modalità di ripartizione tra gli agenti.
- [Gestione dello storico documentale](docs/history.md): chiarisce che la cronologia è generata automaticamente dal backend e accessibile solo tramite endpoint di lettura.
