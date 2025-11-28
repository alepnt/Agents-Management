package com.example.common.api;                                   // Package che raccoglie i contratti API condivisi nel progetto.

import java.time.Instant;               // DTO che rappresenta una conversazione (metadati, ultimo messaggio, ecc.).
import java.util.List;                    // DTO che rappresenta un singolo messaggio scambiato tra utenti.

import org.springframework.web.context.request.async.DeferredResult;                // DTO utilizzato per inviare un nuovo messaggio.

import com.example.common.dto.ChatConversationDTO; // Supporto Spring per long-polling asincrono.
import com.example.common.dto.ChatMessageDTO;                                        // Rappresenta un timestamp preciso in formato UTC.
import com.example.common.dto.ChatMessageRequest;                                           // Permette di gestire collezioni di risultati.

/**
 * Contratto API per la messaggistica interna.
 * Contiene le operazioni necessarie alla gestione conversazioni
 * e allo scambio di messaggi tra gli utenti.
 */
public interface ChatApiContract {                               // Interfaccia che definisce le funzionalità principali del modulo chat.

    List<ChatConversationDTO> conversations(Long userId);        // Restituisce tutte le conversazioni associate all'utente.

    List<ChatMessageDTO> messages(Long userId,                   // Restituisce i messaggi di una conversazione specifica.
                                  String conversationId,         // Identificatore della conversazione richiesta.
                                  Instant since);                // Restituisce solo i messaggi più recenti di un timestamp dato (per aggiornamenti incrementali).

    DeferredResult<List<ChatMessageDTO>> poll(Long userId,       // Attiva una richiesta di long-polling per ricevere nuovi messaggi.
                                              String conversationId); // L'operazione rimane sospesa finché non arrivano messaggi nuovi.

    ChatMessageDTO send(ChatMessageRequest request);             // Invia un nuovo messaggio all'interno della conversazione.
}                                                                // Fine dell’interfaccia ChatApiContract.
