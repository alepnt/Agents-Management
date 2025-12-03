package com.example.server.domain; // Pacchetto che contiene le entità di dominio

import org.springframework.data.annotation.Id; // Identifica il campo ID
import org.springframework.data.relational.core.mapping.Column; // Mappa un campo su una colonna
import org.springframework.data.relational.core.mapping.Table; // Mappa la classe su una tabella

import java.time.Instant; // Rappresenta un istante temporale
import java.util.Objects; // Utility per equals e hashCode

@Table("messages") // Associa la classe alla tabella "messages"
public class Message { // Modello che rappresenta un messaggio di chat

    @Id // Identificativo univoco del messaggio
    @Column("id") // Colonna primaria in minuscolo
    private Long id; // Campo per l'ID del messaggio

    @Column("conversation_id") // Colonna che memorizza l'ID della conversazione
    private String conversationId; // Identificativo della conversazione a cui appartiene il messaggio

    @Column("sender_id") // Colonna che memorizza l'ID del mittente
    private Long senderId; // Identificativo dell'utente che invia il messaggio

    @Column("team_id") // Colonna che memorizza l'ID del team
    private Long teamId; // Identificativo del team associato

    private String body; // Contenuto testuale del messaggio

    @Column("created_at") // Colonna che memorizza la data di creazione
    private Instant createdAt; // Timestamp di quando il messaggio è stato creato

    public Message(Long id, // Costruttore completo con ID
                   String conversationId, // ID della conversazione
                   Long senderId, // ID del mittente
                   Long teamId, // ID del team
                   String body, // Testo del messaggio
                   Instant createdAt) { // Momento di creazione
        this.id = id; // Imposta l'ID
        this.conversationId = conversationId; // Imposta la conversazione
        this.senderId = senderId; // Imposta il mittente
        this.teamId = teamId; // Imposta il team
        this.body = body; // Imposta il testo
        this.createdAt = createdAt; // Imposta il timestamp
    }

    public static Message create(String conversationId, Long senderId, Long teamId, String body, Instant createdAt) { // Factory method per creare un messaggio senza ID
        return new Message(null, conversationId, senderId, teamId, body, createdAt); // Crea un'istanza con ID nullo
    }

    public Long getId() { // Restituisce l'ID del messaggio
        return id; // Ritorna l'identificativo
    }

    public String getConversationId() { // Restituisce l'ID della conversazione
        return conversationId; // Ritorna l'identificativo della conversazione
    }

    public Long getSenderId() { // Restituisce l'ID del mittente
        return senderId; // Ritorna l'identificativo dell'utente mittente
    }

    public Long getTeamId() { // Restituisce l'ID del team
        return teamId; // Ritorna l'identificativo del team
    }

    public String getBody() { // Restituisce il contenuto del messaggio
        return body; // Ritorna il testo
    }

    public Instant getCreatedAt() { // Restituisce il timestamp di creazione
        return createdAt; // Ritorna la data di creazione
    }

    public Message withId(Long id) { // Ritorna una copia del messaggio con ID assegnato
        return new Message(id, conversationId, senderId, teamId, body, createdAt); // Crea nuova istanza con il nuovo ID
    }

    @Override // Ridefinisce equals per la classe
    public boolean equals(Object o) { // Confronta l'oggetto con un altro
        if (this == o) return true; // Se è lo stesso riferimento, sono uguali
        if (!(o instanceof Message message)) return false; // Se l'altro non è Message, non sono uguali
        return Objects.equals(id, message.id); // Due messaggi sono uguali se hanno lo stesso ID
    }

    @Override // Ridefinisce hashCode in coerenza con equals
    public int hashCode() { // Calcola l'hash del messaggio
        return Objects.hash(id); // Usa l'ID come base per l'hash
    }
}
