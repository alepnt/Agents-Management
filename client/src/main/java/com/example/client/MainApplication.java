package com.example.client;
// Package principale dell’applicazione JavaFX lato client.

import com.example.client.auth.DevBypassTokenProvider;
import com.example.client.auth.MsalTokenProvider;
import com.example.client.auth.TokenProvider;
// Provider per ottenere token tramite MSAL (Azure AD) oppure fallback disabilitato.

import com.example.client.controller.LoginController;
import com.example.client.controller.MainViewController;
// Controller principali dell’applicazione.

import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
// API per autenticazione e modello della sessione.

import com.example.client.session.SessionStore;
// Gestione locale delle sessioni (file JSON e cache in memoria).

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
// Librerie JavaFX.

import java.io.IOException;
import java.net.URL;
// Gestione di risorse nel classpath e I/O.

/**
 * Entry point principale dell'applicazione JavaFX "Gestore Agenti".
 *
 * Responsabilità:
 * - inizializzare servizi e provider token
 * - caricare sessione salvata (SessionStore)
 * - decidere se aprire la vista Login o la vista Main
 * - impostare i controller con le dipendenze corrette
 * - caricare e applicare i CSS comuni
 *
 * Implementa Application di JavaFX.
 */
public class MainApplication extends Application {

    private static TokenProvider sharedTokenProvider;
    // TokenProvider condiviso per Login multipli o ricaricamenti UI.

    private final SessionStore sessionStore = new SessionStore();
    // Gestore delle sessioni locali (.gestore-agenti/sessions)

    private final AuthApiClient authApiClient = new AuthApiClient();
    // Client REST per autenticazione (login, refresh session, registrazione)

    private final TokenProvider tokenProvider;
    // Provider token effettivo (MSAL o fallback)

    /**
     * Costruttore: inizializza TokenProvider (MSAL → fallback) e aggiorna
     * sharedTokenProvider.
     */
    public MainApplication() {
        this.tokenProvider = buildTokenProvider();
        sharedTokenProvider = this.tokenProvider;
    }

    /**
     * Metodo di avvio JavaFX.
     * Decide se aprire:
     * - MainView → se esiste sessione valida
     * - LoginView → altrimenti
     */
    @Override
    public void start(Stage stage) throws IOException {

        var loadedSession = sessionStore.load(); // prova a caricare sessione
        var existingSession = loadedSession.filter(s -> !s.isExpired()); // filtra se valida

        if (existingSession.isPresent()) {
            // Sessione valida → apri vista principale
            loadMainView(stage, existingSession.get());

        } else {
            // Sessione NON valida → eventuale pulizia file sessione
            loadedSession.filter(AuthSession::isExpired).ifPresent(session -> {
                try {
                    sessionStore.clear(); // rimuove file .json scaduto
                } catch (IOException ignored) {
                    // Se fallisce, continuiamo comunque verso il login
                }
            });

            // Apri login
            loadLoginView(stage);
        }

        stage.show();
    }

    /**
     * Carica la vista di login con il suo controller.
     */
    private void loadLoginView(Stage stage) throws IOException {
        loadLoginScene(stage, param -> {
            if (param == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, tokenProvider);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        });
    }

    /**
     * Metodo statico invocabile da altri componenti per forzare la schermata di
     * login,
     * ad esempio dopo un logout o sessione scaduta.
     */
    public static void showLoginSelection(Stage stage, SessionStore sessionStore,
            String statusMessage, String statusStyle) throws IOException {

        TokenProvider provider = sharedTokenProvider != null
                ? sharedTokenProvider
                : MsalTokenProvider.disabled("Servizio MSAL non disponibile");

        loadLoginScene(stage, param -> {
            if (param == LoginController.class) {
                return LoginController.create(
                        sessionStore,
                        new AuthApiClient(),
                        provider,
                        statusMessage,
                        statusStyle);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        });
    }

    /**
     * Carica la vista principale passando la sessione utente.
     */
    private void loadMainView(Stage stage, AuthSession session) throws IOException {
        loadScene(stage, "/com/example/client/view/MainView.fxml", param -> {
            if (param == MainViewController.class) {
                return MainViewController.create(session, sessionStore);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        }, "Gestore Agenti");
    }

    /**
     * Carica la schermata di login da FXML con controller factory personalizzato.
     */
    private static void loadLoginScene(Stage stage,
            javafx.util.Callback<Class<?>, Object> controllerFactory)
            throws IOException {

        URL loginView = MainApplication.class.getResource("/com/example/client/view/LoginView.fxml");
        if (loginView == null) {
            throw new IllegalStateException("Risorsa FXML non trovata: /com/example/client/view/LoginView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(loginView);
        loader.setControllerFactory(controllerFactory);

        Parent root = loader.load();
        Scene scene = new Scene(root);

        URL theme = MainApplication.class.getResource("/com/example/client/style/theme.css");
        if (theme == null) {
            throw new IllegalStateException("Foglio di stile non trovato: /com/example/client/style/theme.css");
        }

        scene.getStylesheets().add(theme.toExternalForm());
        stage.setTitle("Gestore Agenti - Login");
        stage.setScene(scene);
    }

    /**
     * Carica una scena generica da un percorso FXML e applica un controller factory
     * dinamico.
     */
    private void loadScene(Stage stage, String fxmlPath,
            javafx.util.Callback<Class<?>, Object> controllerFactory,
            String title) throws IOException {

        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalStateException("Risorsa FXML non trovata: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory);

        Parent root = loader.load();
        Scene scene = new Scene(root);

        URL theme = getClass().getResource("/com/example/client/style/theme.css");
        if (theme == null) {
            throw new IllegalStateException("Foglio di stile non trovato: /com/example/client/style/theme.css");
        }

        scene.getStylesheets().add(theme.toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
    }

    /**
     * Entry point standard JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Costruisce un TokenProvider basato su MSAL se possibile,
     * altrimenti crea una versione disabilitata con messaggio di fallback.
     */
    private TokenProvider buildTokenProvider() {
        try {
            if (System.getenv("MSAL_DEV_BYPASS_SECRET") != null
                    && !System.getenv("MSAL_DEV_BYPASS_SECRET").isBlank()) {
                return DevBypassTokenProvider.fromEnvironment();
            }
            return MsalTokenProvider.fromEnvironment();
        } catch (Exception e) {
            System.err.println("MSAL non disponibile: " + e.getMessage());
            return MsalTokenProvider.disabled("MSAL non configurato: " + e.getMessage());
        }
    }
}
