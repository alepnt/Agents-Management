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

import com.example.common.dto.RegistrationLookupDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
// Import necessari per interagire con JavaFX, FXML e gestire la UI.

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
// Import di utilità per gestione errori e Optional.

public class RegisterController {
    // Controller responsabile della schermata di registrazione.

    @FXML
    private ComboBox<String> azureIdField; // Campo per l’Azure ID con lookup.
    @FXML
    private TextField emailField; // Campo email dell'utente.
    @FXML
    private TextField displayNameField; // Campo nome visualizzato.
    @FXML
    private ComboBox<String> agentCodeField; // Campo codice agente (opzionale) con lookup.
    @FXML
    private PasswordField passwordField; // Campo per la password.
    @FXML
    private ComboBox<String> teamNameField; // Campo nome del team (opzionale) con lookup.
    @FXML
    private ComboBox<String> roleNameField; // Campo ruolo dell’utente (opzionale) con lookup.
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
    public void initialize() {
        loadLookups();
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        // Metodo richiamato al click del bottone di registrazione.

        String azureId = comboValue(azureIdField);

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

        if (azureId == null || azureId.isBlank()) {
            // Azure ID obbligatorio.
            setMessage("Specificare l'Azure ID", false);
            AlertUtils.showError("Specificare l'Azure ID");
            return;
        }

        String agentCode = comboValue(agentCodeField);
        // Codice agente opzionale ma pulito.

        if (agentCode != null && agentCode.length() < 5) {
            // Se presente, deve avere almeno 5 caratteri (es. AG001).
            setMessage("Il codice agente deve avere almeno 5 caratteri", false);
            AlertUtils.showError("Il codice agente deve avere almeno 5 caratteri");
            return;
        }

        String teamName = comboValue(teamNameField);
        // Nome team opzionale.

        String roleName = comboValue(roleNameField);
        // Nome ruolo opzionale.

        RegisterForm form = new RegisterForm(
                azureId,
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

    private void loadLookups() {
        try {
            RegistrationLookupDTO lookup = authApiClient.registrationLookups();
            applyLookup(azureIdField, lookup.getAzureIds(), null);
            applyLookup(agentCodeField, lookup.getAgentCodes(), lookup.getNextAgentCode());
            applyLookup(teamNameField, lookup.getTeamNames(), null);
            applyLookup(roleNameField, lookup.getRoleNames(), null);
        } catch (Exception e) {
            setMessage("Impossibile caricare i suggerimenti: " + e.getMessage(), false);
        }
    }

    private void applyLookup(ComboBox<String> comboBox, List<String> values, String suggestedValue) {
        if (comboBox == null) {
            return;
        }
        if (values != null) {
            comboBox.getItems().setAll(values);
        }
        if (suggestedValue != null && !suggestedValue.isBlank()) {
            comboBox.setValue(suggestedValue);
            comboBox.getEditor().setText(suggestedValue);
        }
    }

    private String comboValue(ComboBox<String> comboBox) {
        if (comboBox == null) {
            return null;
        }
        String value = comboBox.getEditor().getText();
        if ((value == null || value.isBlank()) && comboBox.getValue() != null) {
            value = comboBox.getValue();
        }
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
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
