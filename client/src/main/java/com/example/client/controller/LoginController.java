package com.example.client.controller;

import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.service.LoginForm;
import com.example.client.session.SessionStore;
import com.example.client.validation.CompositeValidator;
import com.example.client.validation.EmailValidationStrategy;
import com.example.client.controller.MainViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField displayNameField;
    @FXML
    private TextField azureIdField;
    @FXML
    private TextArea accessTokenArea;
    @FXML
    private Label statusLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    private final SessionStore sessionStore;
    private final AuthApiClient authApiClient;
    private final Optional<String> prefilledEmail;
    private final Optional<String> statusMessage;
    private final Optional<String> statusStyle;
    private final CompositeValidator emailValidator = new CompositeValidator().addStrategy(new EmailValidationStrategy());

    public static LoginController create(SessionStore sessionStore) {
        return new LoginController(sessionStore, new AuthApiClient());
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client) {
        return new LoginController(sessionStore, client);
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client, String prefilledEmail, String statusMessage) {
        return new LoginController(sessionStore, client, Optional.ofNullable(prefilledEmail), Optional.ofNullable(statusMessage), Optional.of("-fx-text-fill: #2e7d32; -fx-font-weight: bold;"));
    }

    public static LoginController create(SessionStore sessionStore, String prefilledEmail, String statusMessage, String statusStyle) {
        return new LoginController(sessionStore, new AuthApiClient(), Optional.ofNullable(prefilledEmail), Optional.ofNullable(statusMessage), Optional.ofNullable(statusStyle));
    }

    private LoginController(SessionStore sessionStore, AuthApiClient authApiClient) {
        this(sessionStore, authApiClient, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private LoginController(SessionStore sessionStore, AuthApiClient authApiClient, Optional<String> prefilledEmail, Optional<String> statusMessage, Optional<String> statusStyle) {
        this.sessionStore = sessionStore;
        this.authApiClient = authApiClient;
        this.prefilledEmail = prefilledEmail;
        this.statusMessage = statusMessage;
        this.statusStyle = statusStyle;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            prefilledEmail.ifPresent(emailField::setText);

            Optional<AuthSession> existingSession = sessionStore.load();
            if (existingSession.isPresent()) {
                AuthSession session = existingSession.get();
                if (session.isExpired()) {
                    try {
                        sessionStore.clear();
                    } catch (IOException ignored) {
                        // ignora errori di pulizia
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
        Optional<String> validationError = emailValidator.validate(emailField.getText());
        if (validationError.isPresent()) {
            showValidationError(validationError.get());
            return;
        }
        if (accessTokenArea.getText() == null || accessTokenArea.getText().isBlank()) {
            showValidationError("Inserire un access token Microsoft valido");
            return;
        }
        if (azureIdField.getText() == null || azureIdField.getText().isBlank()) {
            showValidationError("Inserire l'identificativo Azure dell'utente");
            return;
        }

        LoginForm form = new LoginForm(accessTokenArea.getText().trim(), emailField.getText().trim(), displayNameField.getText().trim(), azureIdField.getText().trim());
        try {
            AuthSession session = authApiClient.login(form);
            sessionStore.save(session);
            statusLabel.setText("Autenticazione riuscita. Bentornato " + session.user().displayName() + "!");
            openMainView(session);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showValidationError("Operazione interrotta: " + e.getMessage());
        } catch (IOException e) {
            showValidationError("Autenticazione fallita: " + e.getMessage());
        }
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
                return MainViewController.create(session, sessionStore);
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
}
