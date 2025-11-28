package com.example.common.dto;                                   // Package contenente i DTO condivisi tra client e server.

import java.time.Instant;                                         // Timestamp UTC per indicare la creazione del messaggio.
import java.util.Objects;                                         // Utility per equals() e hashCode().

/**
 * DTO per trasferire le informazioni dei messaggi.
 * Utilizzato da client e server per rappresentare un messaggio della messaggistica interna.
 */
public class MessageDTO {                                         // DTO mutabile che rappresenta un singolo messaggio.

    private Long id;                                              // Identificatore univoco del messaggio.
    private String conversationId;                                // Identificatore della conversazione di appartenenza.
    private Long senderId;                                        // ID dell’utente che ha inviato il messaggio.
    private Long teamId;                                          // ID del team di appartenenza del mittente (per filtri o routing).
    private String body;                                          // Contenuto testuale del messaggio.
    private Instant createdAt;                                    // Data/ora di creazione del messaggio.

    public MessageDTO() {                                         // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public MessageDTO(Long id,
                      String conversationId,
                      Long senderId,
                      Long teamId,
                      String body,
                      Instant createdAt) {                         // Costruttore completo per inizializzazione rapida.
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.teamId = teamId;
        this.body = body;
        this.createdAt = createdAt;
    }

    public Long getId() {                                         // Restituisce l’ID del messaggio.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID del messaggio.
        this.id = id;
    }

    public String getConversationId() {                           // Restituisce l’ID della conversazione associata.
        return conversationId;
    }

    public void setConversationId(String conversationId) {        // Imposta l’ID della conversazione associata.
        this.conversationId = conversationId;
    }

    public Long getSenderId() {                                   // Restituisce l’ID del mittente.
        return senderId;
    }

    public void setSenderId(Long senderId) {                      // Imposta l’ID del mittente.
        this.senderId = senderId;
    }

    public Long getTeamId() {                                     // Restituisce l’ID del team del mittente.
        return teamId;
    }

    public void setTeamId(Long teamId) {                          // Imposta l’ID del team del mittente.
        this.teamId = teamId;
    }

    public String getBody() {                                     // Restituisce il contenuto testuale del messaggio.
        return body;
    }

    public void setBody(String body) {                            // Imposta il contenuto testuale del messaggio.
        this.body = body;
    }

    public Instant getCreatedAt() {                               // Restituisce il timestamp di creazione del messaggio.
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {                 // Imposta il timestamp di creazione.
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due MessageDTO basandosi sull’ID.
        if (this == o) {                                          // Stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Null o classe diversa → non uguali.
            return false;
        }
        MessageDTO that = (MessageDTO) o;                         // Cast dopo verifica del tipo.
        return Objects.equals(id, that.id);                       // Confronto basato sull’identificatore.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }
}                                                                  // Fine della classe MessageDTO.
