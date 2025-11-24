package com.example.server.domain; // Definisce il package in cui vive la classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.util.Objects; // Importa la utility per confrontare e calcolare hash

@Table("agents") // Associa la classe alla tabella "agents"
public class Agent { // Definisce l'entità Agent

    @Id // Identifica il campo come chiave primaria
    private Long id; // Identificativo univoco dell'agente

    @Column("user_id") // Mappa il campo alla colonna user_id
    private Long userId; // Riferimento all'utente associato

    @Column("agent_code") // Mappa il campo alla colonna agent_code
    private String agentCode; // Codice identificativo dell'agente

    @Column("team_role") // Mappa il campo alla colonna team_role
    private String teamRole; // Ruolo dell'agente nel team

    public Agent(Long id, Long userId, String agentCode, String teamRole) { // Costruttore completo dell'entità
        this.id = id; // Imposta l'identificativo
        this.userId = userId; // Imposta l'id utente
        this.agentCode = agentCode; // Imposta il codice agente
        this.teamRole = teamRole; // Imposta il ruolo del team
    }

    public static Agent forUser(Long userId, String agentCode, String teamRole) { // Factory method per creare un agente senza id
        return new Agent(null, userId, agentCode, teamRole); // Restituisce una nuova istanza con id nullo
    }

    public Long getId() { // Restituisce l'id dell'agente
        return id; // Ritorna il valore dell'id
    }

    public Long getUserId() { // Restituisce l'id dell'utente
        return userId; // Ritorna il valore del campo userId
    }

    public String getAgentCode() { // Restituisce il codice agente
        return agentCode; // Ritorna il valore del campo agentCode
    }

    public String getTeamRole() { // Restituisce il ruolo nel team
        return teamRole; // Ritorna il valore del campo teamRole
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public boolean equals(Object o) { // Confronta due oggetti Agent per uguaglianza
        if (this == o) return true; // Se i riferimenti coincidono, sono uguali
        if (!(o instanceof Agent agent)) return false; // Se non è un Agent, non sono uguali
        return Objects.equals(id, agent.id); // Considera uguali due Agent con lo stesso id
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public int hashCode() { // Calcola l'hash basato sull'id
        return Objects.hash(id); // Restituisce l'hash dell'id
    }
} // Chiude la definizione della classe
