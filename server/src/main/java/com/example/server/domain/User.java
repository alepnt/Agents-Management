package com.example.server.domain; // Definisce il package della classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.time.LocalDateTime; // Importa il tipo data/ora senza fuso
import java.util.Objects; // Importa utility per equals e hashCode

/**
 * Rappresenta l'utente autenticato tramite Microsoft Identity Platform.
 */
@Table("users") // Mappa la classe sulla tabella users
public class User { // Rappresenta un utente autenticato

    @Id // Indica la chiave primaria
    @Column("id") // Mappa esplicitamente la colonna id (lowercase)
    private Long id; // Identificativo interno dell'utente

    @Column("azure_id") // Colonna che memorizza l'id Azure
    private String azureId; // Identificativo esterno di Azure AD

    @Column("email") // Colonna email in lowercase nel database
    private String email; // Indirizzo email dell'utente

    @Column("display_name") // Colonna per il nome visualizzato
    private String displayName; // Nome completo mostrato

    @Column("password_hash") // Colonna che memorizza l'hash della password locale
    private String passwordHash; // Hash della password per autenticazione locale

    @Column("role_id") // Colonna che collega il ruolo
    private Long roleId; // Identificativo del ruolo associato

    @Column("team_id") // Colonna che collega il team
    private Long teamId; // Identificativo del team associato

    @Column("active") // Colonna active in lowercase nel database
    private Boolean active; // Stato di attivazione dell'account

    @Column("created_at") // Colonna che memorizza la data di creazione
    private LocalDateTime createdAt; // Timestamp di creazione dell'utente

    public User(Long id, // Identificativo interno
                String azureId, // Identificativo Azure AD
                String email, // Indirizzo email
                String displayName, // Nome visualizzato
                String passwordHash, // Hash della password locale
                Long roleId, // Ruolo associato
                Long teamId, // Team associato
                Boolean active, // Stato attivo/inattivo
                LocalDateTime createdAt) { // Timestamp di creazione
        this.id = id; // Assegna l'id interno
        this.azureId = azureId; // Imposta l'id Azure
        this.email = email; // Imposta l'email
        this.displayName = displayName; // Imposta il nome visualizzato
        this.passwordHash = passwordHash; // Imposta l'hash della password
        this.roleId = roleId; // Imposta il ruolo
        this.teamId = teamId; // Imposta il team
        this.active = active; // Imposta lo stato attivo
        this.createdAt = createdAt; // Imposta la data di creazione
    }

    public static User newAzureUser(String azureId, // Identificativo Azure per il nuovo utente
                                    String email, // Email fornita da Azure
                                    String displayName, // Nome visualizzato da Azure
                                    Long roleId, // Ruolo assegnato di default
                                    Long teamId) { // Team assegnato di default
        return new User(null, azureId, email, displayName, null, roleId, teamId, Boolean.TRUE, LocalDateTime.now()); // Crea un utente Azure con id nullo e stato attivo
    }

    public Long getId() { // Restituisce l'id interno
        return id; // Ritorna l'identificativo
    }

    public String getAzureId() { // Restituisce l'id Azure
        return azureId; // Ritorna l'identificativo Azure AD
    }

    public String getEmail() { // Restituisce l'email
        return email; // Ritorna l'indirizzo email
    }

    public String getDisplayName() { // Restituisce il nome visualizzato
        return displayName; // Ritorna il display name
    }

    public String getPasswordHash() { // Restituisce l'hash della password
        return passwordHash; // Ritorna il valore dell'hash
    }

    public Long getRoleId() { // Restituisce l'id del ruolo
        return roleId; // Ritorna il ruolo associato
    }

    public Long getTeamId() { // Restituisce l'id del team
        return teamId; // Ritorna il team associato
    }

    public Boolean getActive() { // Indica se l'utente è attivo
        return active; // Ritorna lo stato
    }

    public LocalDateTime getCreatedAt() { // Restituisce la data di creazione
        return createdAt; // Ritorna il timestamp di creazione
    }

    public User withId(Long id) { // Crea una copia impostando un id specifico
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt); // Restituisce nuova istanza con id valorizzato
    }

    public User updateFromAzure(String displayName, String email) { // Aggiorna i dati provenienti da Azure
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt); // Restituisce copia con nome e email aggiornati
    }

    public User withPasswordHash(String passwordHash) { // Restituisce una copia con un nuovo hash password
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt); // Crea nuova istanza con password aggiornata
    }

    public User withRoleAndTeam(Long roleId, Long teamId) { // Restituisce una copia con ruolo e team aggiornati
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt); // Crea nuova istanza con associazioni aggiornate
    }

    @Override // Override del confronto di uguaglianza
    public boolean equals(Object o) { // Confronta due utenti
        if (this == o) return true; // Se è lo stesso oggetto sono uguali
        if (!(o instanceof User user)) return false; // Se il tipo è diverso non sono uguali
        return Objects.equals(id, user.id); // Gli utenti sono uguali se l'id coincide
    }

    @Override // Override del calcolo hash
    public int hashCode() { // Calcola l'hash basato sull'id
        return Objects.hash(id); // Usa l'id come base per l'hash
    }
}
