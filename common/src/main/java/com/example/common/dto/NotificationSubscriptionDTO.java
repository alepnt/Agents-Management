package com.example.common.dto;                                   // Package contenente i DTO condivisi tra client e server.

import java.time.Instant;                                         // Timestamp utilizzato per indicare la creazione della sottoscrizione.
import java.util.Objects;                                         // Utility per validazioni e equals()/hashCode().

import org.springframework.lang.NonNull;                          // Annotazione per indicare valori non null.
import org.springframework.lang.Nullable;                         // Annotazione per indicare valori opzionali.

/**
 * DTO che rappresenta una sottoscrizione a un canale di notifica.
 * Utilizzato dal sistema di notifica interna per sapere chi ascolta quali eventi.
 */
public class NotificationSubscriptionDTO {                        // DTO mutabile per modellare una subscription.

    private Long id;                                              // Identificatore della sottoscrizione.
    private Long userId;                                          // ID dell’utente sottoscrittore.
    private @NonNull String channel;                              // Nome del canale sottoscritto; obbligatorio.
    private @Nullable Instant createdAt;                          // Data/ora della sottoscrizione; può essere null.

    public NotificationSubscriptionDTO() {                        // Costruttore vuoto per serializzazione.
        this.channel = "";                                        // Default sicuro che evita null pointer.
        this.createdAt = null;                                    // createdAt opzionale → default null.
    }

    public NotificationSubscriptionDTO(Long id,
                                       Long userId,
                                       String channel,
                                       Instant createdAt) {       // Costruttore completo.
        this.id = id;
        this.userId = userId;
        this.channel = Objects.requireNonNull(channel, "channel"); // Validazione del canale.
        this.createdAt = createdAt;                               // createdAt può essere null.
    }

    @Nullable
    public Long getId() {                                         // Restituisce l’ID della sottoscrizione.
        return id;
    }

    public void setId(@Nullable Long id) {                        // Imposta l’ID della sottoscrizione.
        this.id = id;
    }

    @Nullable
    public Long getUserId() {                                     // Restituisce l’ID dell’utente.
        return userId;
    }

    public void setUserId(@Nullable Long userId) {                // Imposta l’ID dell’utente.
        this.userId = userId;
    }

    @NonNull
    public String getChannel() {                                  // Restituisce il nome del canale sottoscritto.
        return channel;
    }

    public void setChannel(@NonNull String channel) {             // Imposta il canale; mai null.
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    @Nullable
    public Instant getCreatedAt() {                               // Restituisce la data di creazione della sottoscrizione.
        return createdAt;
    }

    public void setCreatedAt(@Nullable Instant createdAt) {       // Imposta la data di creazione.
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {                             // Confronto basato sull’ID.
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationSubscriptionDTO that = (NotificationSubscriptionDTO) o;
        return Objects.equals(id, that.id);                       // Equals basato solo sull’identificatore.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe NotificationSubscriptionDTO.
