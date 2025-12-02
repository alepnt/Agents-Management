package com.example.common.dto; // Package condiviso per i DTO.

import java.util.List; // Collezione ordinata di elementi.
import java.util.Objects; // Utility per controlli null-safe.

/**
 * DTO che espone i suggerimenti utili per la schermata di registrazione.
 * Contiene liste di valori recuperati dal database (team, ruoli, Azure ID)
 * e il prossimo codice agente suggerito.
 */
public class RegistrationLookupDTO {

    private List<String> azureIds; // Elenco Azure ID esistenti.
    private List<String> agentCodes; // Elenco dei codici agente già presenti.
    private List<String> teamNames; // Elenco team registrati.
    private List<String> roleNames; // Elenco ruoli definiti.
    private String nextAgentCode; // Prossimo codice agente disponibile.

    public RegistrationLookupDTO() {
    }

    public RegistrationLookupDTO(List<String> azureIds, List<String> agentCodes, List<String> teamNames, List<String> roleNames, String nextAgentCode) {
        this.azureIds = Objects.requireNonNull(azureIds);
        this.agentCodes = Objects.requireNonNull(agentCodes);
        this.teamNames = Objects.requireNonNull(teamNames);
        this.roleNames = Objects.requireNonNull(roleNames);
        this.nextAgentCode = nextAgentCode;
    }

    public List<String> getAzureIds() {
        return azureIds;
    }

    public void setAzureIds(List<String> azureIds) {
        this.azureIds = azureIds;
    }

    public List<String> getAgentCodes() {
        return agentCodes;
    }

    public void setAgentCodes(List<String> agentCodes) {
        this.agentCodes = agentCodes;
    }

    public List<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(List<String> teamNames) {
        this.teamNames = teamNames;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public String getNextAgentCode() {
        return nextAgentCode;
    }

    public void setNextAgentCode(String nextAgentCode) {
        this.nextAgentCode = nextAgentCode;
    }
}
