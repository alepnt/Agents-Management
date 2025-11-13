package com.example.client.view;

import com.example.client.model.MailAttachment;
import com.example.client.model.MailMessage;
import com.example.client.service.BackendGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MailComposerView extends BorderPane {

    private final BackendGateway backendGateway;
    private final TextField toField = new TextField();
    private final TextField ccField = new TextField();
    private final TextField bccField = new TextField();
    private final TextField subjectField = new TextField();
    private final TextArea bodyArea = new TextArea();
    private final TextField attachmentName = new TextField();
    private final TextField attachmentType = new TextField();
    private final TextArea attachmentContent = new TextArea();
    private final ObservableList<String> attachments = FXCollections.observableArrayList();
    private final ListView<String> attachmentList = new ListView<>(attachments);
    private final List<MailAttachment> attachmentData = new ArrayList<>();

    private String delegatedToken;

    public MailComposerView(BackendGateway backendGateway) {
        this.backendGateway = backendGateway;
        setPadding(new Insets(12));
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.addRow(0, new Label("A:"), toField);
        form.addRow(1, new Label("Cc:"), ccField);
        form.addRow(2, new Label("Bcc:"), bccField);
        form.addRow(3, new Label("Oggetto:"), subjectField);
        form.addRow(4, new Label("Messaggio:"), bodyArea);

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

        setTop(form);
        setCenter(attachmentPane);

        Button sendButton = new Button("Invia email");
        sendButton.setOnAction(event -> sendMail());
        setBottom(sendButton);
    }

    public void withDelegatedToken(String delegatedToken) {
        this.delegatedToken = delegatedToken;
    }

    private void addAttachment() {
        if (attachmentName.getText().isBlank() || attachmentType.getText().isBlank() || attachmentContent.getText().isBlank()) {
            return;
        }
        attachmentData.add(new MailAttachment(attachmentName.getText(), attachmentType.getText(), attachmentContent.getText()));
        attachments.add(String.format("%s (%s)", attachmentName.getText(), attachmentType.getText()));
        attachmentName.clear();
        attachmentType.clear();
        attachmentContent.clear();
    }

    private void sendMail() {
        if (delegatedToken == null || delegatedToken.isBlank()) {
            throw new IllegalStateException("Token delegato non configurato");
        }
        MailMessage mailMessage = new MailMessage(
                subjectField.getText(),
                bodyArea.getText(),
                parseAddresses(toField.getText()),
                parseAddresses(ccField.getText()),
                parseAddresses(bccField.getText()),
                buildAttachments()
        );
        backendGateway.sendMail(mailMessage, delegatedToken);
        clear();
    }

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

    private List<MailAttachment> buildAttachments() {
        return List.copyOf(attachmentData);
    }

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
