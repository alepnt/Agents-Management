package com.example.server.domain; // Definisce il package per la classe di dominio

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.time.Instant; // Importa l'oggetto data/ora
import java.util.Objects; // Importa utility per equals e hashCode

@Table("notification_subscriptions") // Mappa la classe sulla tabella notification_subscriptions
public class NotificationSubscription { // Rappresenta un'iscrizione alle notifiche per un utente

    @Id // Indica la chiave primaria
    private Long id; // Identificativo dell'iscrizione

    @Column("user_id") // Colonna che collega l'utente
    @NotNull(message = "L'utente è obbligatorio")
    private Long userId; // Identificativo dell'utente collegato

    @NotBlank(message = "Il canale di notifica è obbligatorio")
    private String channel; // Canale di notifica (es. email, push)

    @Column("created_at") // Colonna con la data di creazione
    private Instant createdAt; // Momento in cui è stata creata l'iscrizione

    public NotificationSubscription(Long id, Long userId, String channel, Instant createdAt) { // Costruttore completo
        this.id = id; // Assegna l'identificativo
        this.userId = userId; // Imposta l'utente associato
        this.channel = channel; // Imposta il canale di notifica
        this.createdAt = createdAt; // Imposta la data di creazione
    }

    public static NotificationSubscription create(Long userId, String channel, Instant createdAt) { // Factory method per creare una nuova iscrizione
        return new NotificationSubscription(null, userId, channel, createdAt); // Istanzia con id nullo in attesa di persistenza
    }

    public Long getId() { // Restituisce l'id dell'iscrizione
        return id; // Ritorna l'identificativo
    }

    public Long getUserId() { // Restituisce l'id dell'utente associato
        return userId; // Ritorna l'utente collegato
    }

    public String getChannel() { // Restituisce il canale di notifica
        return channel; // Ritorna il nome del canale
    }

    public Instant getCreatedAt() { // Restituisce la data di creazione
        return createdAt; // Ritorna il timestamp
    }

    public NotificationSubscription withId(Long id) { // Crea una copia con un id specifico
        return new NotificationSubscription(id, userId, channel, createdAt); // Restituisce nuova istanza con id aggiornato
    }

    @Override // Override del confronto di uguaglianza
    public boolean equals(Object o) { // Confronta due iscrizioni
        if (this == o) return true; // Se è lo stesso oggetto sono uguali
        if (!(o instanceof NotificationSubscription that)) return false; // Se il tipo è diverso non sono uguali
        return Objects.equals(id, that.id); // Due iscrizioni sono uguali se hanno lo stesso id
    }

    @Override // Override del calcolo hash
    public int hashCode() { // Calcola l'hash basato sull'id
        return Objects.hash(id); // Usa l'id come base per l'hash
    }

    @Override
    public String toString() {
        return "NotificationSubscription{" +
                "id=" + id +
                ", userId=" + userId +
                ", channel='" + channel + '\'' +
                '}';
    }
}
