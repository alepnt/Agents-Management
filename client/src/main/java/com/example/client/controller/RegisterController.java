package com.example.client.controller;

import com.example.client.service.AuthApiClient;
import com.example.client.service.RegisterForm;
import com.example.client.service.UserSummary;
import com.example.client.session.SessionStore;
import com.example.client.validation.CompositeValidator;
import com.example.client.validation.EmailValidationStrategy;
import com.example.client.validation.PasswordValidationStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class RegisterController {

    @FXML
    private TextField azureIdField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField displayNameField;
    @FXML
    private TextField agentCodeField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField teamNameField;
    @FXML
    private TextField roleNameField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button registerButton;

    private final SessionStore sessionStore;
    private final AuthApiClient authApiClient;
    private final CompositeValidator emailValidator = new CompositeValidator().addStrategy(new EmailValidationStrategy());
    private final CompositeValidator passwordValidator = new CompositeValidator().addStrategy(new PasswordValidationStrategy());

    public static RegisterController create(SessionStore sessionStore, AuthApiClient authApiClient) {
        return new RegisterController(sessionStore, authApiClient);
    }

    private RegisterController(SessionStore sessionStore, AuthApiClient authApiClient) {
        this.sessionStore = sessionStore;
        this.authApiClient = authApiClient;
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        Optional<String> emailError = emailValidator.validate(emailField.getText());
        if (emailError.isPresent()) {
            setMessage(emailError.get(), false);
            AlertUtils.showError(emailError.get());
            return;
        }
        Optional<String> passwordError = passwordValidator.validate(passwordField.getText());
        if (passwordError.isPresent()) {
            setMessage(passwordError.get(), false);
            AlertUtils.showError(passwordError.get());
            return;
        }
        if (displayNameField.getText() == null || displayNameField.getText().isBlank()) {
            setMessage("Inserire il nome visualizzato", false);
            AlertUtils.showError("Inserire il nome visualizzato");
            return;
        }
        if (azureIdField.getText() == null || azureIdField.getText().isBlank()) {
            setMessage("Specificare l'Azure ID", false);
            AlertUtils.showError("Specificare l'Azure ID");
            return;
        }
        String agentCode = agentCodeField.getText();
        agentCode = (agentCode == null || agentCode.isBlank()) ? null : agentCode.trim();
        if (agentCode != null && agentCode.length() < 6) {
            setMessage("Il codice agente deve avere almeno 6 caratteri", false);
            AlertUtils.showError("Il codice agente deve avere almeno 6 caratteri");
            return;
        }
        String teamName = teamNameField.getText();
        teamName = (teamName == null || teamName.isBlank()) ? null : teamName.trim();
        String roleName = roleNameField.getText();
        roleName = (roleName == null || roleName.isBlank()) ? null : roleName.trim();

        RegisterForm form = new RegisterForm(
                azureIdField.getText().trim(),
                emailField.getText().trim(),
                displayNameField.getText().trim(),
                agentCode,
                passwordField.getText(),
                teamName,
                roleName
        );
        try {
            UserSummary summary = authApiClient.register(form);
            String successMessage = "Registrazione completata per " + summary.displayName();
            setMessage(successMessage + ". Verrai reindirizzato al login.", true);
            openLoginWithPrefilledEmail(summary.email(), successMessage + ". Accedi con le credenziali appena create.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            setMessage("Operazione interrotta", false);
            AlertUtils.showError("Operazione interrotta");
        } catch (IOException e) {
            String errorMessage = "Errore durante la registrazione: " + e.getMessage();
            setMessage(errorMessage, false);
            AlertUtils.showError(errorMessage);
        }
    }

    @FXML
    public void openLogin(ActionEvent event) {
        navigate("/com/example/client/view/LoginView.fxml", type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Login");
    }

    private void openLoginWithPrefilledEmail(String email, String bannerMessage) {
        navigate("/com/example/client/view/LoginView.fxml", type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, email, bannerMessage);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Login");
    }

    private void setMessage(String message, boolean success) {
        messageLabel.setText(message);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        }
    }

    private void navigate(String fxmlPath, ControllerFactory factory, String title) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException("Risorsa FXML non trovata: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(factory::create);
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            messageLabel.setText("Impossibile cambiare vista: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface ControllerFactory {
        Object create(Class<?> type);
    }
}
