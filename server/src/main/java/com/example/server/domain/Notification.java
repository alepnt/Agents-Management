package com.example.server.domain; // Pacchetto che contiene le entità di dominio

import org.springframework.data.annotation.Id; // Identifica il campo ID
import org.springframework.data.relational.core.mapping.Column; // Mappa un campo su una colonna
import org.springframework.data.relational.core.mapping.Table; // Mappa la classe su una tabella

import java.time.Instant; // Rappresenta un istante temporale
import java.util.Objects; // Utility per equals e hashCode

@Table("notifications") // Associa la classe alla tabella "notifications"
public class Notification { // Modello che rappresenta una notifica

    @Id // Identificativo univoco della notifica
    private Long id; // Campo ID della notifica

    @Column("user_id") // Colonna per l'ID dell'utente destinatario
    private Long userId; // Identificativo dell'utente

    @Column("team_id") // Colonna per l'ID del team
    private Long teamId; // Identificativo del team destinatario

    private String title; // Titolo della notifica

    private String message; // Contenuto della notifica

    @Column("is_read") // Colonna che indica se la notifica è stata letta
    private Boolean read; // Flag di lettura

    @Column("created_at") // Colonna per la data di creazione
    private Instant createdAt; // Timestamp della creazione

    public Notification(Long id, // Costruttore completo con ID
                        Long userId, // ID dell'utente destinatario
                        Long teamId, // ID del team destinatario
                        String title, // Titolo della notifica
                        String message, // Testo della notifica
                        Boolean read, // Stato di lettura
                        Instant createdAt) { // Data di creazione
        this.id = id; // Imposta l'ID
        this.userId = userId; // Imposta l'utente destinatario
        this.teamId = teamId; // Imposta il team destinatario
        this.title = title; // Imposta il titolo
        this.message = message; // Imposta il testo
        this.read = read; // Imposta lo stato di lettura
        this.createdAt = createdAt; // Imposta il timestamp
    }

    public static Notification forUser(Long userId, String title, String message, Instant createdAt) { // Factory per notifiche indirizzate a un utente
        return new Notification(null, userId, null, title, message, Boolean.FALSE, createdAt); // Crea una notifica utente con ID nullo e non letta
    }

    public static Notification forTeam(Long teamId, String title, String message, Instant createdAt) { // Factory per notifiche indirizzate a un team
        return new Notification(null, null, teamId, title, message, Boolean.FALSE, createdAt); // Crea una notifica di team con ID nullo e non letta
    }

    public Long getId() { // Restituisce l'ID della notifica
        return id; // Ritorna l'identificativo
    }

    public Long getUserId() { // Restituisce l'ID dell'utente destinatario
        return userId; // Ritorna l'identificativo utente
    }

    public Long getTeamId() { // Restituisce l'ID del team destinatario
        return teamId; // Ritorna l'identificativo del team
    }

    public String getTitle() { // Restituisce il titolo della notifica
        return title; // Ritorna il testo del titolo
    }

    public Notification withTitle(String title) { // Ritorna una copia della notifica con titolo aggiornato
        return new Notification(id, userId, teamId, title, message, read, createdAt); // Crea una nuova istanza con nuovo titolo
    }

    public String getMessage() { // Restituisce il contenuto della notifica
        return message; // Ritorna il testo del messaggio
    }

    public Notification withMessage(String message) { // Ritorna una copia con messaggio aggiornato
        return new Notification(id, userId, teamId, title, message, read, createdAt); // Crea una nuova istanza con nuovo testo
    }

    public Boolean isRead() { // Indica se la notifica è stata letta
        return Boolean.TRUE.equals(read); // Ritorna true solo se read è TRUE
    }

    public Instant getCreatedAt() { // Restituisce la data di creazione
        return createdAt; // Ritorna il timestamp di creazione
    }

    public Notification markRead() { // Restituisce una copia marcata come letta
        return new Notification(id, userId, teamId, title, message, Boolean.TRUE, createdAt); // Crea una nuova istanza con read a TRUE
    }

    public Notification withRead(Boolean read) { // Restituisce una copia con stato di lettura personalizzato
        return new Notification(id, userId, teamId, title, message, read, createdAt); // Crea nuova istanza con il flag indicato
    }

    public Notification withId(Long id) { // Restituisce una copia con ID impostato
        return new Notification(id, userId, teamId, title, message, read, createdAt); // Crea nuova istanza assegnando l'ID
    }

    @Override // Ridefinisce equals per la classe
    public boolean equals(Object o) { // Confronta l'oggetto con un altro
        if (this == o) return true; // Se è lo stesso riferimento sono uguali
        if (!(o instanceof Notification that)) return false; // Se l'altro non è Notification non sono uguali
        return Objects.equals(id, that.id); // Due notifiche sono uguali se hanno lo stesso ID
    }

    @Override // Ridefinisce hashCode in coerenza con equals
    public int hashCode() { // Calcola l'hash della notifica
        return Objects.hash(id); // Usa l'ID come base
    }
}
