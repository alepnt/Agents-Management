package com.example.client.view;
// Package dedicato alle viste personalizzate JavaFX lato client.

import com.example.client.service.BackendGateway;
// Gateway per inviare richieste di invio mail al backend.

import com.example.common.dto.MailAttachmentDTO;
import com.example.common.dto.MailRequest;
// DTO condivisi usando per costruire la mail e i suoi allegati.

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// Liste JavaFX osservabili → utili per ListView.

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
// Layout e controlli UI JavaFX.

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
// Utility per gestire liste e stream.

/**
 * Vista JavaFX per la composizione delle email.
 *
 * Permette di:
 * - specificare destinatari (To, Cc, Bcc)
 * - inserire oggetto e corpo del messaggio
 * - aggiungere allegati base64
 * - inviare la mail tramite BackendGateway
 *
 * La vista è auto-contenuta e funge da "mini editor" email.
 */
public class MailComposerView extends BorderPane {

    private final BackendGateway backendGateway;
    // Gateway per inviare la mail al backend.

    // === CAMPI PER LA MAIL ===
    private final TextField toField = new TextField(); // Campo destinatari principali
    private final TextField ccField = new TextField(); // CC
    private final TextField bccField = new TextField(); // BCC
    private final TextField subjectField = new TextField(); // Oggetto
    private final TextArea bodyArea = new TextArea(); // Corpo del messaggio

    // === CAMPi PER ALLEGATI (nome, tipo MIME, base64) ===
    private final TextField attachmentName = new TextField();
    private final TextField attachmentType = new TextField();
    private final TextArea attachmentContent = new TextArea();

    // Lista delle descrizioni degli allegati mostrata nella UI
    private final ObservableList<String> attachments = FXCollections.observableArrayList();
    private final ListView<String> attachmentList = new ListView<>(attachments);

    // Lista dei dati completi degli allegati, da inviare al backend
    private final List<MailAttachmentDTO> attachmentData = new ArrayList<>();

    private String delegatedToken;
    // Token delegato dell’utente corrente (per invio mail proxy lato backend).

    /**
     * Costruisce l’interfaccia del compositore email.
     */
    public MailComposerView(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;

        setPadding(new Insets(12));

        // === FORM SUPERIORE (TO, CC, BCC, OGGETTO, TESTO) ===
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);

        form.addRow(0, new Label("A:"), toField);
        form.addRow(1, new Label("Cc:"), ccField);
        form.addRow(2, new Label("Bcc:"), bccField);
        form.addRow(3, new Label("Oggetto:"), subjectField);
        form.addRow(4, new Label("Messaggio:"), bodyArea);

        // === FORM ALLEGATI ===
        GridPane attachmentPane = new GridPane();
        attachmentPane.setHgap(6);
        attachmentPane.setVgap(6);

        attachmentPane.addRow(0, new Label("Nome"), attachmentName);
        attachmentPane.addRow(1, new Label("Tipo"), attachmentType);
        attachmentPane.addRow(2, new Label("Base64"), attachmentContent);

        Button addAttachment = new Button("Aggiungi allegato");
        addAttachment.setOnAction(event -> addAttachment());

        attachmentPane.add(addAttachment, 0, 3);
        attachmentPane.add(attachmentList, 1, 3);

        // Layout globale
        setTop(form);
        setCenter(attachmentPane);

        // Pulsante invio
        Button sendButton = new Button("Invia email");
        sendButton.setOnAction(event -> sendMail());
        setBottom(sendButton);
    }

    /**
     * Configura il token delegato necessario per il backend.
     */
    public void withDelegatedToken(String delegatedToken) {
        this.delegatedToken = delegatedToken;
    }

    /**
     * Aggiunge un allegato alla mail.
     */
    private void addAttachment() {
        if (attachmentName.getText().isBlank()
                || attachmentType.getText().isBlank()
                || attachmentContent.getText().isBlank()) {
            return; // evita allegati incompleti
        }

        // Aggiunge i dati tecnici dell’allegato
        attachmentData.add(new MailAttachmentDTO(
                attachmentName.getText(),
                attachmentType.getText(),
                attachmentContent.getText()));

        // Aggiunta della descrizione nella ListView (per l’utente)
        attachments.add(String.format("%s (%s)", attachmentName.getText(), attachmentType.getText()));

        // Reset dei campi
        attachmentName.clear();
        attachmentType.clear();
        attachmentContent.clear();
    }

    /**
     * Costruisce l'oggetto MailRequest e lo invia al backend.
     */
    private void sendMail() {
        if (delegatedToken == null || delegatedToken.isBlank()) {
            throw new IllegalStateException("Token delegato non configurato");
        }

        MailRequest mailMessage = new MailRequest(
                subjectField.getText(),
                bodyArea.getText(),
                parseAddresses(toField.getText()),
                parseAddresses(ccField.getText()),
                parseAddresses(bccField.getText()),
                buildAttachments());

        backendGateway.sendMail(mailMessage, delegatedToken);

        clear();
    }

    /**
     * Divide gli indirizzi separati da virgola.
     * Esempio: "a@b.it, c@d.it" → ["a@b.it","c@d.it"]
     */
    private List<String> parseAddresses(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return List.of(text.split(","))
                .stream()
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Costruisce la lista finale di allegati da inviare.
     */
    private List<MailAttachmentDTO> buildAttachments() {
        return List.copyOf(attachmentData);
    }

    /**
     * Ripristina il form allo stato iniziale.
     */
    private void clear() {
        toField.clear();
        ccField.clear();
        bccField.clear();
        subjectField.clear();
        bodyArea.clear();
        attachments.clear();
        attachmentData.clear();
    }
}
