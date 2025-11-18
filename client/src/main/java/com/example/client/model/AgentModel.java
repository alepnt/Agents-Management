package com.example.client.model;

import com.example.common.dto.AgentDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modello JavaFX per la gestione degli agenti.
 */
public class AgentModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> userId = new SimpleObjectProperty<>();
    private final StringProperty agentCode = new SimpleStringProperty();
    private final StringProperty teamRole = new SimpleStringProperty();

    public static AgentModel fromDto(AgentDTO dto) {
        AgentModel model = new AgentModel();
        model.setId(dto.getId());
        model.setUserId(dto.getUserId());
        model.setAgentCode(dto.getAgentCode());
        model.setTeamRole(dto.getTeamRole());
        return model;
    }

    public AgentDTO toDto() {
        return new AgentDTO(getId(), getUserId(), getAgentCode(), getTeamRole());
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

    public Long getUserId() {
        return userId.get();
    }

    public void setUserId(Long userId) {
        this.userId.set(userId);
    }

    public ObjectProperty<Long> userIdProperty() {
        return userId;
    }

    public String getAgentCode() {
        return agentCode.get();
    }

    public void setAgentCode(String agentCode) {
        this.agentCode.set(agentCode);
    }

    public StringProperty agentCodeProperty() {
        return agentCode;
    }

    public String getTeamRole() {
        return teamRole.get();
    }

    public void setTeamRole(String teamRole) {
        this.teamRole.set(teamRole);
    }

    public StringProperty teamRoleProperty() {
        return teamRole;
    }

    @Override
    public String toString() {
        return agentCode.get() != null ? agentCode.get() : String.valueOf(getId());
    }
}
