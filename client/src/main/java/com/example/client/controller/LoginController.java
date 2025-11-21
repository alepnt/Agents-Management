package com.example.client.controller;

import com.example.client.auth.MsalAuthenticationException;
import com.example.client.auth.MsalAuthenticationResult;
import com.example.client.auth.MsalTokenProvider;
import com.example.client.auth.TokenProvider;
import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.service.LoginForm;
import com.example.client.session.SessionStore;
import javafx.application.Platform;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LoginController {

    @FXML
    private Label statusLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    private final SessionStore sessionStore;
    private final AuthApiClient authApiClient;
    private final Optional<String> statusMessage;
    private final Optional<String> statusStyle;
    private final MainViewFactory mainViewFactory;
    private final TokenProvider tokenProvider;

    @FunctionalInterface
    public interface MainViewFactory {
        MainViewController create(AuthSession session, SessionStore sessionStore);
    }

    public static LoginController create(SessionStore sessionStore) {
        return new LoginController(sessionStore, new AuthApiClient(), Optional.empty(), Optional.empty(), defaultTokenProvider(), MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client) {
        return new LoginController(sessionStore, client, Optional.empty(), Optional.empty(), defaultTokenProvider(), MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, String prefilledEmail, String statusMessage) {
        return new LoginController(sessionStore, client, Optional.ofNullable(statusMessage), Optional.of("-fx-text-fill: #2e7d32; -fx-font-weight: bold;"), defaultTokenProvider(), MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, String prefilledEmail, String statusMessage, String statusStyle) {
        return new LoginController(sessionStore, new AuthApiClient(), Optional.ofNullable(statusMessage), Optional.ofNullable(statusStyle), defaultTokenProvider(), MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, MainViewFactory mainViewFactory) {
        return new LoginController(sessionStore, client, Optional.empty(), Optional.empty(), defaultTokenProvider(), mainViewFactory);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, TokenProvider tokenProvider) {
        return new LoginController(sessionStore, client, Optional.empty(), Optional.empty(), tokenProvider, MainViewController::create);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, TokenProvider tokenProvider, MainViewFactory mainViewFactory) {
        return new LoginController(sessionStore, client, Optional.empty(), Optional.empty(), tokenProvider, mainViewFactory);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, TokenProvider tokenProvider, String statusMessage, String statusStyle) {
        return new LoginController(sessionStore, client, Optional.ofNullable(statusMessage), Optional.ofNullable(statusStyle), tokenProvider, MainViewController::create);
    }

    private LoginController(SessionStore sessionStore,
                            AuthApiClient authApiClient,
                            Optional<String> statusMessage,
                            Optional<String> statusStyle,
                            TokenProvider tokenProvider,
                            MainViewFactory mainViewFactory) {
        this.sessionStore = sessionStore;
        this.authApiClient = authApiClient;
        this.statusMessage = statusMessage;
        this.statusStyle = statusStyle;
        this.mainViewFactory = mainViewFactory;
        this.tokenProvider = tokenProvider == null ? MsalTokenProvider.disabled("Servizio MSAL non configurato") : tokenProvider;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Optional<AuthSession> existingSession = sessionStore.load();
            if (existingSession.isPresent()) {
                AuthSession session = existingSession.get();
                if (session.isExpired()) {
                    try {
                        sessionStore.clear();
                    } catch (IOException ignored) {
                        // impossibile pulire la sessione, ma proseguiamo con il login
                    }
                    statusLabel.setText("La sessione salvata è scaduta. Effettua nuovamente il login.");
                } else {
                    statusLabel.setText("Sessione attiva per " + session.user().displayName());
                }
                return;
            }

            statusMessage.ifPresent(message -> {
                statusLabel.setText(message);
                statusLabel.setStyle(statusStyle.orElse("-fx-text-fill: #2e7d32; -fx-font-weight: bold;"));
            });
        });
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        loginButton.setDisable(true);
        updateStatus("Connessione a Microsoft in corso...");

        if (isHeadlessEnvironment()) {
            runSynchronously();
            return;
        }

        CompletableFuture
                .supplyAsync(this::acquireMsalToken)
                .thenApply(this::authenticateWithBackend)
                .whenComplete((session, error) -> Platform.runLater(() -> handleAuthenticationResult(session, error)));
    }

    @FXML
    public void openRegister(ActionEvent event) {
        navigate("/com/example/client/view/RegisterView.fxml", type -> {
            if (type == RegisterController.class) {
                return RegisterController.create(sessionStore, authApiClient);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Registrazione");
    }

    private void openMainView(AuthSession session) {
        navigate("/com/example/client/view/MainView.fxml", type -> {
            if (type == MainViewController.class) {
                return mainViewFactory.create(session, sessionStore);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti");
    }

    private void navigate(String fxmlPath, ControllerFactory factory, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(factory::create);
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            showValidationError("Impossibile aprire la vista richiesta: " + e.getMessage());
        }
    }

    private void showValidationError(String message) {
        statusLabel.setText(message);
        AlertUtils.showError(message);
    }

    @FunctionalInterface
    private interface ControllerFactory {
        Object create(Class<?> type);
    }

    private static TokenProvider defaultTokenProvider() {
        try {
            return MsalTokenProvider.fromEnvironment();
        } catch (Exception e) {
            return MsalTokenProvider.disabled("MSAL non configurato: " + e.getMessage());
        }
    }

    private MsalAuthenticationResult acquireMsalToken() {
        try {
            Optional<MsalAuthenticationResult> cached = tokenProvider.acquireTokenSilently();
            if (cached.isPresent()) {
                updateStatus("Token Microsoft trovato. Sto collegando l'account...");
                return cached.get();
            }
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
            AuthSession session = authApiClient.login(form);
            sessionStore.saveForUser(form.azureId(), session);
            return session;
        } catch (IOException e) {
            throw new CompletionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
    }

    private LoginForm buildLoginForm(MsalAuthenticationResult msalResult) {
        String email = msalResult.account() != null ? msalResult.account().username() : null;
        String displayName = msalResult.account() != null ? msalResult.account().displayName() : null;
        String azureId = msalResult.account() != null ? firstNonBlank(msalResult.account().objectId(), msalResult.account().homeAccountId()) : null;
        if (azureId == null || azureId.isBlank()) {
            throw new IllegalStateException("Impossibile determinare l'identificativo Azure dell'account Microsoft");
        }
        return new LoginForm(msalResult.accessToken(), email, displayName, azureId, msalResult.authority(), msalResult.refreshToken());
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

    private void handleAuthenticationResult(AuthSession session, Throwable error) {
        loginButton.setDisable(false);
        if (error != null) {
            handleAuthenticationError(error);
        } else if (session != null) {
            statusLabel.setText("Autenticazione riuscita. Bentornato " + session.user().displayName() + "!");
            openMainView(session);
        }
    }

    private void runSynchronously() {
        try {
            AuthSession session = authenticateWithBackend(acquireMsalToken());
            Platform.runLater(() -> handleAuthenticationResult(session, null));
        } catch (Throwable error) {
            Platform.runLater(() -> handleAuthenticationResult(null, error));
        }
    }

    private boolean isHeadlessEnvironment() {
        return Boolean.getBoolean("testfx.headless") || Boolean.getBoolean("java.awt.headless");
    }

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
}
