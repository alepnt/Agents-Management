package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.time.Instant;                   // Validazioni: il campo non può essere null né vuoto.
import java.util.Objects;                                         // Timestamp UTC usato per audit.

import org.springframework.lang.NonNull;                                         // Utility per validazioni e equals()/hashCode().
import org.springframework.lang.Nullable;                          // Annotazione che indica valori non null.

import jakarta.validation.constraints.NotBlank;                         // Annotazione che indica valori nullable.

/**
 * DTO che rappresenta una notifica destinata a un utente o a un team.
 * Contiene metadati, testo della notifica e lo stato di lettura.
 */
public class NotificationDTO {                                    // DTO mutabile per modellare una notifica applicativa.

    private Long id;                                              // Identificatore univoco della notifica.
    private Long userId;                                          // ID dell'utente destinatario (se notifica individuale).
    private Long teamId;                                          // ID del team destinatario (se notifica collettiva).

    @NotBlank
    private @NonNull String title;                               // Titolo della notifica; obbligatorio e non vuoto.

    @NotBlank
    private @NonNull String message;                             // Testo della notifica; obbligatorio e non vuoto.

    private @NonNull Boolean read;                               // Flag di lettura (TRUE/FALSE); mai null.
    private @NonNull Instant createdAt;                          // Timestamp di creazione della notifica; mai null.

    public NotificationDTO() {                                   // Costruttore vuoto con valori di default sicuri.
        this.title = "";                                          // Titolo iniziale vuoto per rispettare @NotBlank in contesto di serializzazione.
        this.message = "";                                        // Messaggio iniziale vuoto.
        this.read = Boolean.FALSE;                                // Le notifiche nuove sono non lette.
        this.createdAt = Instant.now();                           // Timestamp di creazione corrente.
    }

    public NotificationDTO(Long id,
                           Long userId,
                           Long teamId,
                           String title,
                           String message,
                           Boolean read,
                           Instant createdAt) {                   // Costruttore completo.
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.title = Objects.requireNonNull(title, "title");      // Titolo obbligatorio.
        this.message = Objects.requireNonNull(message, "message");// Messaggio obbligatorio.
        this.read = Objects.requireNonNullElse(read, Boolean.FALSE); 
        // Se null → FALSE.
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now); 
        // Se null → timestamp corrente.
    }

    @Nullable
    public Long getId() {                                         // Restituisce l'ID della notifica (può essere null per nuove notifiche).
        return id;
    }

    public void setId(@Nullable Long id) {                        // Imposta l'ID della notifica.
        this.id = id;
    }

    @Nullable
    public Long getUserId() {                                     // Restituisce l'ID del destinatario utente, se presente.
        return userId;
    }

    public void setUserId(@Nullable Long userId) {                // Imposta il destinatario utente.
        this.userId = userId;
    }

    @Nullable
    public Long getTeamId() {                                     // Restituisce l'ID del team destinatario, se presente.
        return teamId;
    }

    public void setTeamId(@Nullable Long teamId) {                // Imposta il destinatario team.
        this.teamId = teamId;
    }

    @NonNull
    public String getTitle() {                                    // Restituisce il titolo della notifica.
        return title;
    }

    public void setTitle(@NonNull String title) {                 // Imposta il titolo della notifica.
        this.title = Objects.requireNonNull(title, "title");
    }

    @NonNull
    public String getMessage() {                                  // Restituisce il messaggio della notifica.
        return message;
    }

    public void setMessage(@NonNull String message) {             // Imposta il messaggio della notifica.
        this.message = Objects.requireNonNull(message, "message");
    }

    @NonNull
    public Boolean getRead() {                                    // Restituisce lo stato di lettura della notifica.
        return read;
    }

    public void setRead(@Nullable Boolean read) {                 // Imposta lo stato di lettura (null → FALSE).
        this.read = Objects.requireNonNullElse(read, Boolean.FALSE);
    }

    @NonNull
    public Instant getCreatedAt() {                               // Restituisce il timestamp di creazione.
        return createdAt;
    }

    public void setCreatedAt(@Nullable Instant createdAt) {       // Imposta il timestamp di creazione (null → now).
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    @Override
    public boolean equals(Object o) {                             // Confronto basato sull’ID della notifica.
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe NotificationDTO.
