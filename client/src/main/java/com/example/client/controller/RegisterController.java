package com.example.client.controller;
// Definisce il package in cui risiede questo controller.

import com.example.client.service.AuthApiClient;
// Client che gestisce le chiamate API di autenticazione lato backend.

import com.example.client.service.RegisterForm;
// DTO utilizzato per inviare i dati di registrazione al server.

import com.example.client.service.UserSummary;
// DTO restituito dopo una registrazione avvenuta con successo.

import com.example.client.session.SessionStore;
// Oggetto che mantiene i dati di sessione lato client.

import com.example.client.validation.CompositeValidator;
import com.example.client.validation.EmailValidationStrategy;
import com.example.client.validation.PasswordValidationStrategy;
// Validatori modulari: CompositeValidator applica più strategie a un valore.

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
// Import necessari per interagire con JavaFX, FXML e gestire la UI.

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
// Import di utilità per gestione errori e Optional.

public class RegisterController {
    // Controller responsabile della schermata di registrazione.

    @FXML
    private TextField azureIdField; // Campo per l’Azure ID.
    @FXML
    private TextField emailField; // Campo email dell'utente.
    @FXML
    private TextField displayNameField; // Campo nome visualizzato.
    @FXML
    private TextField agentCodeField; // Campo codice agente (opzionale).
    @FXML
    private PasswordField passwordField; // Campo per la password.
    @FXML
    private TextField teamNameField; // Campo nome del team (opzionale).
    @FXML
    private TextField roleNameField; // Campo ruolo dell’utente (opzionale).
    @FXML
    private Label messageLabel; // Etichetta dove vengono mostrati errori/successi.
    @FXML
    private Button registerButton; // Bottone che attiva la registrazione.

    private final SessionStore sessionStore; // Riferimento alla sessione corrente.
    private final AuthApiClient authApiClient; // API client per registrarsi.

    private final CompositeValidator emailValidator = new CompositeValidator()
            .addStrategy(new EmailValidationStrategy());
    // Validatore email che usa la strategia di validazione email.

    private final CompositeValidator passwordValidator = new CompositeValidator()
            .addStrategy(new PasswordValidationStrategy());
    // Validatore password che applica la strategia password.

    public static RegisterController create(SessionStore sessionStore, AuthApiClient authApiClient) {
        return new RegisterController(sessionStore, authApiClient);
    }
    // Factory statica: crea un nuovo controller passando dipendenze.

    private RegisterController(SessionStore sessionStore, AuthApiClient authApiClient) {
        this.sessionStore = sessionStore;
        this.authApiClient = authApiClient;
    }
    // Costruttore privato usato dalla factory: dependency injection manuale.

    @FXML
    public void handleRegister(ActionEvent event) {
        // Metodo richiamato al click del bottone di registrazione.

        Optional<String> emailError = emailValidator.validate(emailField.getText());
        // Verifica se l'email è valida.

        if (emailError.isPresent()) {
            setMessage(emailError.get(), false);
            AlertUtils.showError(emailError.get());
            return; // Interrompe il flusso in caso di errore.
        }

        Optional<String> passwordError = passwordValidator.validate(passwordField.getText());
        // Verifica se la password è valida.

        if (passwordError.isPresent()) {
            setMessage(passwordError.get(), false);
            AlertUtils.showError(passwordError.get());
            return;
        }

        if (displayNameField.getText() == null || displayNameField.getText().isBlank()) {
            // Controllo nome visualizzato obbligatorio.
            setMessage("Inserire il nome visualizzato", false);
            AlertUtils.showError("Inserire il nome visualizzato");
            return;
        }

        if (azureIdField.getText() == null || azureIdField.getText().isBlank()) {
            // Azure ID obbligatorio.
            setMessage("Specificare l'Azure ID", false);
            AlertUtils.showError("Specificare l'Azure ID");
            return;
        }

        String agentCode = agentCodeField.getText();
        agentCode = (agentCode == null || agentCode.isBlank()) ? null : agentCode.trim();
        // Codice agente opzionale ma pulito.

        if (agentCode != null && agentCode.length() < 6) {
            // Se presente, deve avere almeno 6 caratteri.
            setMessage("Il codice agente deve avere almeno 6 caratteri", false);
            AlertUtils.showError("Il codice agente deve avere almeno 6 caratteri");
            return;
        }

        String teamName = teamNameField.getText();
        teamName = (teamName == null || teamName.isBlank()) ? null : teamName.trim();
        // Nome team opzionale.

        String roleName = roleNameField.getText();
        roleName = (roleName == null || roleName.isBlank()) ? null : roleName.trim();
        // Nome ruolo opzionale.

        RegisterForm form = new RegisterForm(
                azureIdField.getText().trim(),
                emailField.getText().trim(),
                displayNameField.getText().trim(),
                agentCode,
                passwordField.getText(),
                teamName,
                roleName);
        // Crea il payload della richiesta di registrazione.

        try {
            UserSummary summary = authApiClient.register(form);
            // Invia la registrazione al backend.

            String successMessage = "Registrazione completata per " + summary.displayName();
            // Messaggio di conferma.

            setMessage(successMessage + ". Verrai reindirizzato al login.", true);
            // Mostra messaggio in verde (successo).

            openLoginWithPrefilledEmail(summary.email(), successMessage + ". Accedi con le credenziali appena create.");
            // Passa al login con email precompilata.

        } catch (InterruptedException e) {
            // Caso raro: thread interrotto (timeout o chiusura app).
            Thread.currentThread().interrupt();
            setMessage("Operazione interrotta", false);
            AlertUtils.showError("Operazione interrotta");

        } catch (IOException e) {
            // Errore di comunicazione con il backend.
            String errorMessage = "Errore durante la registrazione: " + e.getMessage();
            setMessage(errorMessage, false);
            AlertUtils.showError(errorMessage);
        }
    }

    @FXML
    public void openLogin(ActionEvent event) {
        // Naviga verso la vista Login senza precompilazione.
        navigate("/com/example/client/view/LoginView.fxml", type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Login");
    }

    private void openLoginWithPrefilledEmail(String email, String bannerMessage) {
        // Naviga verso Login ma passando email predefinita e messaggio.
        final String statusMessage = (email != null && !email.isBlank())
                ? bannerMessage + " (" + email + ")"
                : bannerMessage;

        navigate("/com/example/client/view/LoginView.fxml", type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, statusMessage, null);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Login");
    }

    private void setMessage(String message, boolean success) {
        // Aggiorna la label dei messaggi con stile grafico.
        messageLabel.setText(message);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            // Verde per successo.
        } else {
            messageLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            // Rosso per errore.
        }
    }

    private void navigate(String fxmlPath, ControllerFactory factory, String title) {
        // Metodo generico per cambiare vista tramite caricamento FXML.

        try {
            Scene targetScene = buildSceneWithTheme(fxmlPath, factory);

            Stage stage = (Stage) registerButton.getScene().getWindow();
            // Recupera lo stage corrente dalla scena del bottone.

            stage.setScene(targetScene);
            stage.setTitle(title);
            // Cambia scena e titolo.

        } catch (Exception e) {
            // Mostra errore se non riesce a caricare l’FXML.
            messageLabel.setText("Impossibile cambiare vista: " + e.getMessage());
            AlertUtils.showError("Impossibile cambiare vista: " + e.getMessage());
        }
    }

    private Scene buildSceneWithTheme(String fxmlPath, ControllerFactory factory) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        // Recupera il file FXML.

        if (resource == null) {
            // Errore se la risorsa non esiste nel classpath.
            throw new IllegalStateException("Risorsa FXML non trovata: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(factory::create);
        // Inietta un controller personalizzato.

        Parent root = loader.load();
        // Carica la UI.

        Scene scene = new Scene(root);

        URL theme = getClass().getResource("/com/example/client/style/theme.css");
        if (theme != null) {
            scene.getStylesheets().add(theme.toExternalForm());
        }

        return scene;
    }

    @FunctionalInterface
    private interface ControllerFactory {
        Object create(Class<?> type);
        // Funzione che crea un controller a partire dal tipo richiesto da FXMLLoader.
    }
}
