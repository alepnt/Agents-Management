package com.example.client.model;
// Package dei modelli JavaFX lato client.

/**
 * Modello JavaFX per la gestione dei team.
 * Fornisce proprietà osservabili per integrazione con la UI (liste, ComboBox, form).
 */

import com.example.common.dto.TeamDTO;
// DTO condiviso lato backend per la rappresentazione dei team.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import delle JavaFX Properties per binding automatico.

/**
 * Modello JavaFX che mappa TeamDTO in proprietà osservabili.
 */
public class TeamModel {

    // Identificativo univoco del team.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Nome del team (es. "Lombardia Nord", "Milano 2", "Team Vendite A").
    private final StringProperty name = new SimpleStringProperty();

    /**
     * Crea un TeamModel a partire da un DTO.
     * Mapping DTO → Model.
     */
    public static TeamModel fromDto(TeamDTO dto) {
        TeamModel model = new TeamModel();
        model.setId(dto.getId()); // ID team.
        model.setName(dto.getName()); // Nome team.
        return model;
    }

    /**
     * Converte TeamModel in TeamDTO.
     * Mapping Model → DTO.
     */
    public TeamDTO toDto() {
        return new TeamDTO(getId(), getName());
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
     * Ritorna una rappresentazione leggibile del team.
     * Usata automaticamente da ComboBox, tabelle, log, ecc.
     */
    @Override
    public String toString() {
        return getName() != null ? getName() : String.valueOf(getId());
    }
}
