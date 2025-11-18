package com.example.client.model;

import com.example.common.dto.RoleDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello JavaFX per la gestione dei ruoli.
 */
public class RoleModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();

    public static RoleModel fromDto(RoleDTO dto) {
        RoleModel model = new RoleModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        return model;
    }

    public RoleDTO toDto() {
        return new RoleDTO(getId(), getName());
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return getName() != null ? getName() : String.valueOf(getId());
    }
}
