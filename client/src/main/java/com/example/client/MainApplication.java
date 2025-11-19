package com.example.client;

import com.example.client.auth.MsalTokenProvider;
import com.example.client.auth.TokenProvider;
import com.example.client.controller.LoginController;
import com.example.client.controller.MainViewController;
import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.session.SessionStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static TokenProvider sharedTokenProvider;

    private final SessionStore sessionStore = new SessionStore();
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final TokenProvider tokenProvider;

    public MainApplication() {
        this.tokenProvider = buildTokenProvider();
        sharedTokenProvider = this.tokenProvider;
    }

    @Override
    public void start(Stage stage) throws IOException {
        var loadedSession = sessionStore.load();
        var existingSession = loadedSession.filter(session -> !session.isExpired());

        if (existingSession.isPresent()) {
            loadMainView(stage, existingSession.get());
        } else {
            loadedSession.filter(AuthSession::isExpired).ifPresent(session -> {
                try {
                    sessionStore.clear();
                } catch (IOException ignored) {
                    // impossibile cancellare la sessione scaduta, proseguo con il login
                }
            });
            loadLoginView(stage);
        }
        stage.show();
    }

    private void loadLoginView(Stage stage) throws IOException {
        loadLoginScene(stage, param -> {
            if (param == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, tokenProvider);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        });
    }

    public static void showLoginSelection(Stage stage, SessionStore sessionStore,
                                          String statusMessage, String statusStyle) throws IOException {
        TokenProvider provider = sharedTokenProvider != null ? sharedTokenProvider : MsalTokenProvider.disabled("Servizio MSAL non disponibile");
        loadLoginScene(stage, param -> {
            if (param == LoginController.class) {
                return LoginController.create(sessionStore, new AuthApiClient(), provider, statusMessage, statusStyle);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        });
    }

    private void loadMainView(Stage stage, AuthSession session) throws IOException {
        loadScene(stage, "/com/example/client/view/MainView.fxml", param -> {
            if (param == MainViewController.class) {
                return MainViewController.create(session, sessionStore);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        }, "Gestore Agenti");
    }

    private static void loadLoginScene(Stage stage, javafx.util.Callback<Class<?>, Object> controllerFactory) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/com/example/client/view/LoginView.fxml"));
        loader.setControllerFactory(controllerFactory);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestore Agenti - Login");
        stage.setScene(scene);
    }

    private void loadScene(Stage stage, String fxmlPath, javafx.util.Callback<Class<?>, Object> controllerFactory, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(controllerFactory);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TokenProvider buildTokenProvider() {
        try {
            return MsalTokenProvider.fromEnvironment();
        } catch (Exception e) {
            System.err.println("MSAL non disponibile: " + e.getMessage());
            return MsalTokenProvider.disabled("MSAL non configurato: " + e.getMessage());
        }
    }
}
