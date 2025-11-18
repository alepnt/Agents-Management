package com.example.common.dto;

import java.util.Objects;

/**
 * DTO per rappresentare un agente.
 */
public class AgentDTO {

    private Long id;
    private Long userId;
    private String agentCode;
    private String teamRole;

    public AgentDTO() {
    }

    public AgentDTO(Long id, Long userId, String agentCode, String teamRole) {
        this.id = id;
        this.userId = userId;
        this.agentCode = agentCode;
        this.teamRole = teamRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AgentDTO agentDTO = (AgentDTO) o;
        return Objects.equals(id, agentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AgentDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", agentCode='" + agentCode + '\'' +
                ", teamRole='" + teamRole + '\'' +
                '}';
    }
}
