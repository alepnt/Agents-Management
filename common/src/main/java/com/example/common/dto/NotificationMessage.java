package com.example.common.dto;                                   // Package che contiene i DTO condivisi del sistema.

import java.time.Instant;                                         // Timestamp UTC usato per indicare il momento della notifica.
import java.util.Objects;                                         // Utility per validazioni e confronti.

/**
 * Messaggio di notifica generico utilizzato dall'Observer.
 * Rappresenta l’evento che viene pubblicato e propagato ai sottoscrittori.
 */
public class NotificationMessage {                                // DTO immutabile per trasportare notifiche interne.

    private final String channel;                                 // Canale sul quale è stata emessa la notifica.
    private final String payload;                                 // Contenuto testuale/JSON della notifica.
    private final Instant timestamp;                              // Momento esatto della creazione.

    public NotificationMessage(String channel,                    
                               String payload,
                               Instant timestamp) {               // Costruttore completo con validazioni.
        this.channel = Objects.requireNonNull(channel, "channel"); 
        // Il canale non può essere null.
        this.payload = Objects.requireNonNull(payload, "payload"); 
        // Il contenuto della notifica non può essere null.
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp"); 
        // Il timestamp non può essere null.
    }

    public String getChannel() {                                  // Restituisce il canale della notifica.
        return channel;
    }

    public String getPayload() {                                  // Restituisce il testo o JSON della notifica.
        return payload;
    }

    public Instant getTimestamp() {                               // Restituisce il timestamp di creazione.
        return timestamp;
    }
}                                                                  // Fine della classe NotificationMessage.
