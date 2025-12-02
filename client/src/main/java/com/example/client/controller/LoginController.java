package com.example.client.controller; // Controller JavaFX responsabile del flusso di login.

import com.example.client.auth.MsalAuthenticationException; // Eccezione MSAL-specifica.
import com.example.client.auth.MsalAuthenticationResult; // Risultato dell’autenticazione MSAL.
import com.example.client.auth.MsalTokenProvider; // Provider MSAL predefinito.
import com.example.client.auth.TokenProvider; // Interfaccia astratta per il retrieval dei token.
import com.example.client.service.AuthApiClient; // Client REST per autenticazione backend.
import com.example.client.service.AuthSession; // Sessione autenticata lato backend.
import com.example.client.service.LoginForm; // Payload per il login REST.
import com.example.client.session.SessionStore; // Storage persistente della sessione.
import javafx.application.Platform; // Utilità per thread JavaFX.
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LoginController {

    // --- Riferimenti ai nodi FXML legati alla vista ---
    @FXML
    private Label statusLabel; // Etichetta che mostra messaggi all'utente.
    @FXML
    private Button loginButton; // Pulsante di accesso.
    @FXML
    private Hyperlink registerLink; // Link alla vista di registrazione.

    // --- Dipendenze principali del controller ---
    private final SessionStore sessionStore; // Persistenza locale sessione.
    private final AuthApiClient authApiClient; // Client REST di autenticazione.
    private final Optional<String> statusMessage; // Messaggio eventuale preimpostato.
    private final Optional<String> statusStyle; // Stile CSS opzionale per stato iniziale.
    private final MainViewFactory mainViewFactory; // Factory per creare il MainViewController.
    private final TokenProvider tokenProvider; // Provider MSAL o versione “disabled”.

    // --- Factory per costruire il MainViewController ---
    @FunctionalInterface
    public interface MainViewFactory {
        MainViewController create(AuthSession session, SessionStore sessionStore);
    }

    // ----------------------
    // FACTORY METHODS STATICI
    // ----------------------

    public static LoginController create(SessionStore sessionStore) {
        return new LoginController(
                sessionStore,
                new AuthApiClient(),
                Optional.empty(),
                Optional.empty(),
                defaultTokenProvider(),
                MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client) {
        return new LoginController(
                sessionStore,
                client,
                Optional.empty(),
                Optional.empty(),
                defaultTokenProvider(),
                MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore,
            AuthApiClient client,
            TokenProvider tokenProvider) {
        return new LoginController(
                sessionStore,
                client,
                Optional.empty(),
                Optional.empty(),
                tokenProvider,
                MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore,
            AuthApiClient client,
            String statusMessage,
            String statusStyle) {
        return new LoginController(
                sessionStore,
                client,
                Optional.ofNullable(statusMessage),
                Optional.ofNullable(statusStyle),
                defaultTokenProvider(),
                MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore,
            AuthApiClient client,
            TokenProvider tokenProvider,
            MainViewFactory mainViewFactory) {
        return new LoginController(
                sessionStore,
                client,
                Optional.empty(),
                Optional.empty(),
                tokenProvider,
                mainViewFactory);
    }

    public static LoginController create(SessionStore sessionStore,
            AuthApiClient client,
            TokenProvider tokenProvider,
            String statusMessage,
            String statusStyle) {
        return new LoginController(
                sessionStore,
                client,
                Optional.ofNullable(statusMessage),
                Optional.ofNullable(statusStyle),
                tokenProvider,
                MainViewController::create);
    }

    // Vari overload per supportare login precompilato, stati di errore precedenti,
    // override client e tokenProvider.

    // ----------------------
    // COSTRUTTORE PRINCIPALE
    // ----------------------

    private LoginController(SessionStore sessionStore,
            AuthApiClient authApiClient,
            Optional<String> statusMessage,
            Optional<String> statusStyle,
            TokenProvider tokenProvider,
            MainViewFactory mainViewFactory) {

        this.sessionStore = sessionStore; // Storage per la sessione salvata.
        this.authApiClient = authApiClient; // Client REST.
        this.statusMessage = statusMessage; // Messaggio opzionale pre-caricato.
        this.statusStyle = statusStyle; // Stile CSS opzionale.
        this.mainViewFactory = mainViewFactory; // Factory UI.
        this.tokenProvider = tokenProvider != null // Se tokenProvider è null → MSAL disabilitato.
                ? tokenProvider
                : MsalTokenProvider.disabled("Servizio MSAL non configurato");
    }

    // ----------------------------
    // INIT FXML (POST-CARICAMENTO)
    // ----------------------------

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            // 1) Se esiste una sessione salvata, provare a riutilizzarla
            Optional<AuthSession> existingSession = sessionStore.load();

            if (existingSession.isPresent()) {
                AuthSession session = existingSession.get();

                if (session.isExpired()) {
                    // Sessione esistente → ma scaduta
                    try {
                        sessionStore.clear();
                    } catch (IOException ignored) {
                        // Non è possibile pulire la sessione, si procede al login comunque
                    }
                    statusLabel.setText("La sessione salvata è scaduta. Effettua nuovamente il login.");
                } else {
                    // Sessione ancora valida
                    statusLabel.setText("Sessione attiva per " + session.user().displayName());
                }
                return;
            }

            // 2) Nessuna sessione → mostra eventuale messaggio predefinito
            statusMessage.ifPresent(message -> {
                statusLabel.setText(message);
                statusLabel.setStyle(statusStyle.orElse(
                        "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"));
            });
        });
    }

    // -----------------------
    // HANDLER PULSANTE LOGIN
    // -----------------------

    @FXML
    public void handleLogin(ActionEvent event) {
        loginButton.setDisable(true); // Disabilita doppio click.
        updateStatus("Connessione a Microsoft in corso...");

        // Se l'ambiente è headless (es. TestFX) → login sincrono
        if (isHeadlessEnvironment()) {
            runSynchronously();
            return;
        }

        // Login asincrono:
        // 1) Ottieni token Microsoft
        // 2) Autentica nel backend
        // 3) Gestisci risultato in JavaFX thread
        CompletableFuture
                .supplyAsync(this::acquireMsalToken)
                .thenApply(this::authenticateWithBackend)
                .whenComplete((session, error) -> Platform.runLater(() -> handleAuthenticationResult(session, error)));
    }

    // -----------------------
    // APERTURA VIEW REGISTER
    // -----------------------

    @FXML
    public void openRegister(ActionEvent event) {
        navigate(
                "/com/example/client/view/register/RegisterView.fxml",
                type -> {
                    if (type == RegisterController.class) {
                        return RegisterController.create(sessionStore, authApiClient);
                    }
                    throw new IllegalStateException("Controller non supportato: " + type.getName());
                },
                "Gestore Agenti - Registrazione");
    }

    // ------------------------
    // APERTURA VIEW PRINCIPALE
    // ------------------------

    private void openMainView(AuthSession session) {
        navigate(
                "/com/example/client/view/MainView.fxml",
                type -> {
                    if (type == MainViewController.class) {
                        return mainViewFactory.create(session, sessionStore);
                    }
                    throw new IllegalStateException("Controller non supportato: " + type.getName());
                },
                "Gestore Agenti");
    }

    // ------------------------
    // NAVIGAZIONE GENERICA FXML
    // ------------------------

    private void navigate(String fxmlPath, ControllerFactory controllerFactory, String title) {
        try {
            Scene targetScene = buildSceneWithTheme(fxmlPath, controllerFactory);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(targetScene);
            stage.setTitle(title);

        } catch (Exception e) {
            showValidationError("Impossibile aprire la vista richiesta: " + e.getMessage());
        }
    }

    private Scene buildSceneWithTheme(String fxmlPath, ControllerFactory controllerFactory) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalStateException("Risorsa FXML non trovata: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory::create);

        Parent root = loader.load();
        Scene scene = new Scene(root);

        URL theme = getClass().getResource("/com/example/client/style/theme.css");
        if (theme != null) {
            scene.getStylesheets().add(theme.toExternalForm());
        }

        return scene;
    }

    private void showValidationError(String message) {
        statusLabel.setText(message);
        AlertUtils.showError(message); // Popup grafico + messaggio su label
    }

    @FunctionalInterface
    private interface ControllerFactory {
        Object create(Class<?> type);
    }

    // ----------------------------
    // TOKEN PROVIDER DEFAULT MSAL
    // ----------------------------

    private static TokenProvider defaultTokenProvider() {
        try {
            return MsalTokenProvider.fromEnvironment();
        } catch (Exception e) {
            return MsalTokenProvider.disabled("MSAL non configurato: " + e.getMessage());
        }
    }

    // -------------------------
    // MSAL AUTHENTICATION FLOW
    // -------------------------

    private MsalAuthenticationResult acquireMsalToken() {
        try {
            // 1) Tentativo silenzioso (token cache)
            Optional<MsalAuthenticationResult> cached = tokenProvider.acquireTokenSilently();
            if (cached.isPresent()) {
                updateStatus("Token Microsoft trovato. Sto collegando l'account...");
                return cached.get();
            }

            // 2) Interattivo (popup Microsoft)
            updateStatus("Autenticazione Microsoft in corso...");
            return tokenProvider.acquireTokenInteractive();

        } catch (MsalAuthenticationException e) {
            throw new CompletionException(e);
        }
    }

    private AuthSession authenticateWithBackend(MsalAuthenticationResult msalResult) {
        try {
            LoginForm form = buildLoginForm(msalResult);

            updateStatus("Verifica delle credenziali con il portale Gestore Agenti...");

            AuthSession session = authApiClient.login(form); // Login backend
            sessionStore.saveForUser(form.azureId(), session); // Persistenza locale

            return session;

        } catch (IOException e) {
            throw new CompletionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
    }

    private LoginForm buildLoginForm(MsalAuthenticationResult msalResult) {
        // Estrazione informazioni account Microsoft
        String email = msalResult.account() != null ? msalResult.account().username() : null;
        String displayName = msalResult.account() != null ? msalResult.account().displayName() : null;
        String azureId = msalResult.account() != null
                ? firstNonBlank(msalResult.account().objectId(), msalResult.account().homeAccountId())
                : null;

        if (azureId == null || azureId.isBlank()) {
            throw new IllegalStateException("Impossibile determinare l'identificativo Azure dell'account Microsoft");
        }

        return new LoginForm(
                msalResult.accessToken(),
                email,
                displayName,
                azureId,
                msalResult.authority(),
                msalResult.refreshToken());
    }

    // ---------------------------
    // HANDLER RISULTATO AUTENTICA
    // ---------------------------

    private void handleAuthenticationResult(AuthSession session, Throwable error) {
        loginButton.setDisable(false);

        if (error != null) {
            handleAuthenticationError(error);
        } else if (session != null) {
            statusLabel.setText("Autenticazione riuscita. Bentornato " + session.user().displayName() + "!");
            openMainView(session);
        }
    }

    private void handleAuthenticationError(Throwable throwable) {
        Throwable root = unwrap(throwable);

        if (root instanceof MsalAuthenticationException) {
            showValidationError(root.getMessage());
        } else if (root instanceof IOException) {
            showValidationError("Autenticazione fallita: " + root.getMessage());
        } else if (root instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            showValidationError("Operazione interrotta. Riprova.");
        } else {
            showValidationError("Errore inatteso durante il login: " + root.getMessage());
        }
    }

    // -------------------------
    // MODALITÀ HEADLESS (TESTFX)
    // -------------------------

    private void runSynchronously() {
        try {
            AuthSession session = authenticateWithBackend(acquireMsalToken());
            Platform.runLater(() -> handleAuthenticationResult(session, null));
        } catch (Throwable error) {
            Platform.runLater(() -> handleAuthenticationResult(null, error));
        }
    }

    private boolean isHeadlessEnvironment() {
        return Boolean.getBoolean("testfx.headless")
                || Boolean.getBoolean("java.awt.headless");
    }

    // -------------------------
    // UTILITIES GENERICHE
    // -------------------------

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private void updateStatus(String message) {
        if (Platform.isFxApplicationThread()) {
            statusLabel.setText(message);
        } else {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
