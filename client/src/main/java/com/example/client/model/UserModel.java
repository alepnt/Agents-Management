package com.example.client.model;

import com.example.common.dto.UserDTO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

/**
 * Modello JavaFX per la gestione degli utenti.
 */
public class UserModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty azureId = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Long> roleId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> teamId = new SimpleObjectProperty<>();
    private final BooleanProperty active = new SimpleBooleanProperty(true);
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    public static UserModel fromDto(UserDTO dto) {
        UserModel model = new UserModel();
        model.setId(dto.getId());
        model.setAzureId(dto.getAzureId());
        model.setEmail(dto.getEmail());
        model.setDisplayName(dto.getDisplayName());
        model.setPassword(dto.getPassword());
        model.setRoleId(dto.getRoleId());
        model.setTeamId(dto.getTeamId());
        if (dto.getActive() != null) {
            model.setActive(dto.getActive());
        }
        model.setCreatedAt(dto.getCreatedAt());
        return model;
    }

    public UserDTO toDto() {
        return new UserDTO(getId(),
                getAzureId(),
                getEmail(),
                getDisplayName(),
                getPassword(),
                getRoleId(),
                getTeamId(),
                isActive(),
                getCreatedAt());
    }

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
