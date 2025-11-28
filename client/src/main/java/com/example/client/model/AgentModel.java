package com.example.client.model;
// Package che contiene i modelli JavaFX usati dal client.

import com.example.common.dto.AgentDTO;
// DTO condiviso tra client e server, rappresenta l’agente lato backend.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import delle proprietà JavaFX, necessarie per binding e aggiornamento UI.

/**
 * Modello JavaFX per la gestione degli agenti.
 * Rappresenta l'adattamento locale del DTO verso proprietà osservabili JavaFX.
 */
public class AgentModel {

    // Proprietà osservabile che contiene l'ID dell'agente (Long).
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Proprietà osservabile per l'ID dell'utente associato all'agente.
    private final ObjectProperty<Long> userId = new SimpleObjectProperty<>();

    // Codice agente come proprietà stringa JavaFX (usata nelle tabelle/UI).
    private final StringProperty agentCode = new SimpleStringProperty();

    // Ruolo all'interno del team, anch'esso stringa osservabile.
    private final StringProperty teamRole = new SimpleStringProperty();

    /**
     * Costruisce un AgentModel a partire da un AgentDTO.
     * Operazione di adattamento DTO → JavaFX Model.
     */
    public static AgentModel fromDto(AgentDTO dto) {
        AgentModel model = new AgentModel(); // Crea nuovo modello vuoto.

        model.setId(dto.getId()); // Trasferisce l'id.
        model.setUserId(dto.getUserId()); // Trasferisce lo userId.
        model.setAgentCode(dto.getAgentCode()); // Codice agente.
        model.setTeamRole(dto.getTeamRole()); // Team role.

        return model; // Restituisce il modello popolato.
    }

    /**
     * Converte il modello JavaFX in un DTO da mandare al backend.
     * Operazione Model → DTO.
     */
    public AgentDTO toDto() {
        return new AgentDTO(
                getId(),
                getUserId(),
                getAgentCode(),
                getTeamRole());
    }

    // Getter per id (restituisce il valore corrente della proprietà).
    public Long getId() {
        return id.get();
    }

    // Setter per id (aggiorna il valore nella proprietà osservabile).
    public void setId(Long id) {
        this.id.set(id);
    }

    // Espone la proprietà id (necessario per binding nelle TableView e UI).
    public ObjectProperty<Long> idProperty() {
        return id;
    }

    // Getter per userId.
    public Long getUserId() {
        return userId.get();
    }

    // Setter per userId.
    public void setUserId(Long userId) {
        this.userId.set(userId);
    }

    // Proprietà osservabile userId.
    public ObjectProperty<Long> userIdProperty() {
        return userId;
    }

    // Getter per agentCode.
    public String getAgentCode() {
        return agentCode.get();
    }

    // Setter per agentCode.
    public void setAgentCode(String agentCode) {
        this.agentCode.set(agentCode);
    }

    // Espone la proprietà agentCode (binding UI).
    public StringProperty agentCodeProperty() {
        return agentCode;
    }

    // Getter per teamRole.
    public String getTeamRole() {
        return teamRole.get();
    }

    // Setter per teamRole.
    public void setTeamRole(String teamRole) {
        this.teamRole.set(teamRole);
    }

    // Proprietà osservabile teamRole.
    public StringProperty teamRoleProperty() {
        return teamRole;
    }

    /**
     * Override di toString per rappresentazione del modello nelle ComboBox.
     * Se è presente un agentCode, mostra quello, altrimenti mostra l'id.
     */
    @Override
    public String toString() {
        return agentCode.get() != null ? agentCode.get() : String.valueOf(getId());
    }
}
