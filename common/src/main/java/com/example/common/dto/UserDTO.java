package com.example.common.dto; // Package che contiene i DTO condivisi tra client e server.

import java.time.LocalDateTime; // Tipo per timestamp con data e ora (senza timezone).
import java.util.Objects; // Utility per equals(), hashCode() e confronti.

/**
 * DTO per rappresentare un utente applicativo.
 * Contiene informazioni di autenticazione, profilo e appartenenza
 * organizzativa.
 */
public class UserDTO { // DTO mutabile che modella un utente del sistema.

    private Long id; // Identificatore univoco dell’utente.
    private String azureId; // ID Azure AD (per autenticazione federata).
    private String email; // Indirizzo email dell’utente.
    private String displayName; // Nome visualizzato (es. "Mario Rossi").
    private String password; // Hash della password (usato solo per utenti locali).
    private Long roleId; // Ruolo associato all’utente.
    private Long teamId; // Team di appartenenza.
    private Boolean active; // Stato di attivazione dell’account.
    private LocalDateTime createdAt; // Timestamp di creazione dell’utente.

    public UserDTO() { // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public UserDTO(Long id,
            String azureId,
            String email,
            String displayName,
            String password,
            Long roleId,
            Long teamId,
            Boolean active,
            LocalDateTime createdAt) { // Costruttore completo per inizializzazione rapida.
        this.id = id;
        this.azureId = azureId;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.roleId = roleId;
        this.teamId = teamId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { // Restituisce l’ID dell’utente.
        return id;
    }

    public void setId(Long id) { // Imposta l’ID dell’utente.
        this.id = id;
    }

    public String getAzureId() { // Restituisce l’ID Azure AD dell’utente.
        return azureId;
    }

    public void setAzureId(String azureId) { // Imposta l’ID Azure AD.
        this.azureId = azureId;
    }

    public String getEmail() { // Restituisce l’indirizzo email.
        return email;
    }

    public void setEmail(String email) { // Imposta l’indirizzo email.
        this.email = email;
    }

    public String getDisplayName() { // Restituisce il nome visualizzato.
        return displayName;
    }

    public void setDisplayName(String displayName) { // Imposta il nome visualizzato.
        this.displayName = displayName;
    }

    public String getPassword() { // Restituisce l’hash della password.
        return password;
    }

    public void setPassword(String password) { // Imposta l’hash della password.
        this.password = password;
    }

    public Long getRoleId() { // Restituisce l’ID del ruolo associato.
        return roleId;
    }

    public void setRoleId(Long roleId) { // Imposta l’ID del ruolo.
        this.roleId = roleId;
    }

    public Long getTeamId() { // Restituisce l’ID del team dell’utente.
        return teamId;
    }

    public void setTeamId(Long teamId) { // Imposta l’ID del team.
        this.teamId = teamId;
    }

    public Boolean getActive() { // Restituisce lo stato di attivazione dell’utente.
        return active;
    }

    public void setActive(Boolean active) { // Imposta lo stato attivo/inattivo.
        this.active = active;
    }

    public LocalDateTime getCreatedAt() { // Restituisce la data/ora di creazione dell’utente.
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) { // Imposta la data/ora di creazione.
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) { // Confronta due UserDTO in base all'ID.
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() { // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() { // Rappresentazione leggibile utile nei log.
        return "UserDTO{" +
                "id=" + id +
                ", azureId='" + azureId + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", roleId=" + roleId +
                ", teamId=" + teamId +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
} // Fine della classe UserDTO.
