package com.example.client.model;
// Package dei modelli JavaFX lato client.

/**
 * Modello JavaFX per la gestione degli utenti.
 * Incapsula UserDTO in proprietà osservabili (JavaFX Property)
 * per facilitare il binding in form, tabelle, ComboBox, ecc.
 */

import com.example.common.dto.UserDTO;
// DTO condiviso che rappresenta l'utente lato backend.

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import JavaFX Property per aggiornamento automatico della UI.

import java.time.LocalDateTime;
// Timestamp di creazione dell'utente.

/**
 * Modello JavaFX dell'utente con proprietà osservabili.
 */
public class UserModel {

    // Identificativo univoco dell'utente.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Azure Active Directory ID (identificativo esterno).
    private final StringProperty azureId = new SimpleStringProperty();

    // Email dell'utente.
    private final StringProperty email = new SimpleStringProperty();

    // Nome visualizzato nella UI.
    private final StringProperty displayName = new SimpleStringProperty();

    // Password (in chiaro solo lato client, prima di hashing lato server).
    private final StringProperty password = new SimpleStringProperty();

    // Ruolo associato all’utente.
    private final ObjectProperty<Long> roleId = new SimpleObjectProperty<>();

    // Team a cui l’utente appartiene.
    private final ObjectProperty<Long> teamId = new SimpleObjectProperty<>();

    // Stato attivo/inattivo dell’account.
    private final BooleanProperty active = new SimpleBooleanProperty(true);

    // Timestamp di creazione (settato dal backend).
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    /**
     * Converte un UserDTO in UserModel (DTO → JavaFX Model).
     */
    public static UserModel fromDto(UserDTO dto) {
        UserModel model = new UserModel();

        model.setId(dto.getId()); // ID utente.
        model.setAzureId(dto.getAzureId()); // Azure ID.
        model.setEmail(dto.getEmail()); // Email.
        model.setDisplayName(dto.getDisplayName()); // Nome visualizzato.
        model.setPassword(dto.getPassword()); // Password (mai memorizzata lato server in chiaro!).
        model.setRoleId(dto.getRoleId()); // ID ruolo.
        model.setTeamId(dto.getTeamId()); // ID team.

        // Attivo/inattivo: gestisce caso null.
        if (dto.getActive() != null) {
            model.setActive(dto.getActive());
        }

        model.setCreatedAt(dto.getCreatedAt()); // Timestamp creazione.

        return model;
    }

    /**
     * Converte il modello JavaFX in UserDTO (Model → DTO).
     */
    public UserDTO toDto() {
        return new UserDTO(
                getId(),
                getAzureId(),
                getEmail(),
                getDisplayName(),
                getPassword(),
                getRoleId(),
                getTeamId(),
                isActive(),
                getCreatedAt());
    }

    // ===========================
    // GETTER / SETTER
    // + JavaFX Properties
    // ===========================

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public String getAzureId() {
        return azureId.get();
    }

    public void setAzureId(String azureId) {
        this.azureId.set(azureId);
    }

    public StringProperty azureIdProperty() {
        return azureId;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public Long getRoleId() {
        return roleId.get();
    }

    public void setRoleId(Long roleId) {
        this.roleId.set(roleId);
    }

    public ObjectProperty<Long> roleIdProperty() {
        return roleId;
    }

    public Long getTeamId() {
        return teamId.get();
    }

    public void setTeamId(Long teamId) {
        this.teamId.set(teamId);
    }

    public ObjectProperty<Long> teamIdProperty() {
        return teamId;
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }
}
