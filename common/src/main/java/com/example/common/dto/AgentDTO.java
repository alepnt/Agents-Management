package com.example.common.dto;                               // Package che contiene i DTO condivisi tra client e server.

import java.util.Objects;                                      // Utility per equals(), hashCode() e confronti null-safe.

/**
 * DTO per rappresentare un agente.
 * Contiene i dati anagrafici essenziali collegati all’entità Agent.
 */
public class AgentDTO {                                        // Classe DTO mutabile usata per trasferire dati lato agenti.

    private Long id;                                           // Identificativo univoco dell’agente.
    private Long userId;                                       // Identificativo dell’utente collegato all’agente.
    private String agentCode;                                  // Codice identificativo dell’agente.
    private String teamRole;                                   // Ruolo dell’agente all’interno del team.

    public AgentDTO() {                                        // Costruttore vuoto richiesto da framework di serializzazione.
    }

    public AgentDTO(Long id, Long userId, String agentCode, String teamRole) { 
        // Costruttore completo che consente l'inizializzazione diretta del DTO.
        this.id = id;
        this.userId = userId;
        this.agentCode = agentCode;
        this.teamRole = teamRole;
    }

    public Long getId() {                                      // Restituisce l’ID dell’agente.
        return id;
    }

    public void setId(Long id) {                               // Imposta l’ID dell’agente.
        this.id = id;
    }

    public Long getUserId() {                                  // Restituisce l’ID dell’utente associato.
        return userId;
    }

    public void setUserId(Long userId) {                       // Imposta l’ID dell’utente associato.
        this.userId = userId;
    }

    public String getAgentCode() {                             // Restituisce il codice agente.
        return agentCode;
    }

    public void setAgentCode(String agentCode) {               // Imposta il codice agente.
        this.agentCode = agentCode;
    }

    public String getTeamRole() {                              // Restituisce il ruolo dell’agente nel team.
        return teamRole;
    }

    public void setTeamRole(String teamRole) {                 // Imposta il ruolo dell’agente nel team.
        this.teamRole = teamRole;
    }

    @Override
    public boolean equals(Object o) {                          // Confronta due AgentDTO basandosi sull’ID.
        if (this == o) {                                       // Stesso riferimento → oggetti uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {         // Se null o classe diversa → non uguali.
            return false;
        }
        AgentDTO agentDTO = (AgentDTO) o;                      // Cast sicuro dopo il controllo della classe.
        return Objects.equals(id, agentDTO.id);                // Confronto basato sull’ID.
    }

    @Override
    public int hashCode() {                                    // Calcola hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                 // Rappresentazione testuale utile per logging/debug.
        return "AgentDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", agentCode='" + agentCode + '\'' +
                ", teamRole='" + teamRole + '\'' +
                '}';
    }
}                                                              // Fine della classe AgentDTO.
