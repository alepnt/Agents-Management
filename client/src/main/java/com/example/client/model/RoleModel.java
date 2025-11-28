package com.example.client.model;
// Package che contiene modelli e strutture dati lato client (JavaFX).

import com.example.common.dto.RoleDTO;
// DTO condiviso per rappresentare un ruolo lato backend.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import JavaFX necessari per binding e proprietà osservabili.

/**
 * Modello JavaFX per la gestione dei ruoli.
 * Questo modello incapsula RoleDTO e fornisce proprietà osservabili
 * utilizzate da form, ComboBox, TableView e altre viste JavaFX.
 */
public class RoleModel {

    // ID univoco del ruolo.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Nome del ruolo (es. "ADMIN", "MANAGER", "AGENT").
    private final StringProperty name = new SimpleStringProperty();

    /**
     * Converte un RoleDTO in RoleModel.
     * Mapping DTO → Model (JavaFX friendly).
     */
    public static RoleModel fromDto(RoleDTO dto) {
        RoleModel model = new RoleModel();
        model.setId(dto.getId()); // Assegna ID.
        model.setName(dto.getName()); // Nome ruolo.
        return model;
    }

    /**
     * Converte RoleModel in RoleDTO.
     * Mapping Model → DTO.
     */
    public RoleDTO toDto() {
        return new RoleDTO(getId(), getName());
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Rappresentazione stringa usata in ComboBox, liste e debug.
     * Se name è valorizzato, lo mostra; altrimenti usa l’ID come fallback.
     */
    @Override
    public String toString() {
        return getName() != null ? getName() : String.valueOf(getId());
    }
}
