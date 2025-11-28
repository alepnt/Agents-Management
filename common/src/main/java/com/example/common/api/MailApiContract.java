package com.example.common.api;                           // Package che contiene i contratti API condivisi tra client e server.

import org.springframework.http.ResponseEntity;                // DTO che incapsula i dati necessari per comporre un'email.

import com.example.common.dto.MailRequest;           // Wrapper HTTP utilizzato per restituire risposte tipizzate.

/**
 * Contratto API per l'invio di email tramite Microsoft Graph.
 * Espone l’operazione di invio utilizzata dal client.
 */
public interface MailApiContract {                        // Interfaccia che definisce le funzionalità di invio email.

    ResponseEntity<Void> sendMail(                        // Metodo che richiede l'invio di un'email.
            String authorization,                         // Token di autorizzazione (tipicamente Bearer) per Microsoft Graph.
            MailRequest request                           // Dati strutturati dell'email da inviare (destinatari, oggetto, contenuto).
    );
}                                                         // Fine dell’interfaccia MailApiContract.
