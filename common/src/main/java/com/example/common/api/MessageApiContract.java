package com.example.common.api;                                     // Package che contiene i contratti API condivisi del progetto.

import java.util.List;                           // DTO che rappresenta un messaggio applicativo.
import java.util.Optional;                            // Annotazione che garantisce la non-nullità a livello di contratto.

import org.springframework.lang.NonNull;                                              // Utilizzato per restituire liste di risultati.

import com.example.common.dto.MessageDTO;                                          // Utilizzato per risultati opzionali non garantiti.

/**
 * Contratto API per la gestione dei messaggi.
 * Definisce operazioni CRUD con vincoli di non-nullità esplicitati tramite annotazioni.
 */
public interface MessageApiContract {                               // Interfaccia che espone le funzionalità per la gestione messaggi.

    /**
     * Restituisce la lista completa dei messaggi; la collezione non è mai {@code null}.
     */
    @NonNull                                                       // Garantisce che il metodo non restituisca mai null.
    List<MessageDTO> listMessages();                               // Ritorna l'intera lista di messaggi presenti nel sistema.

    /**
     * Cerca un messaggio per id. L'id deve essere valorizzato; il risultato può essere vuoto ma mai
     * {@code null}.
     */
    @NonNull                                                       // Il return non sarà mai null, anche se Optional può essere empty.
    Optional<MessageDTO> findById(@NonNull Long id);               // Recupera un messaggio tramite ID; ID obbligatorio.

    /**
     * Crea un nuovo messaggio, richiedendo un payload non nullo.
     */
    @NonNull                                                       // Ritorna un DTO sempre valorizzato.
    MessageDTO create(@NonNull MessageDTO message);                // Crea un nuovo messaggio; il DTO deve essere non nullo.

    /**
     * Aggiorna un messaggio esistente. Id e payload devono essere valorizzati.
     */
    @NonNull                                                       // Ritorna sempre un DTO aggiornato.
    MessageDTO update(@NonNull Long id,                            // ID del messaggio da aggiornare, obbligatorio.
                      @NonNull MessageDTO message);                // Payload aggiornato, non nullo.

    /**
     * Elimina il messaggio con l'id indicato; l'id non può essere nullo.
     */
    void delete(@NonNull Long id);                                 // Elimina il messaggio corrispondente all'ID indicato.

}                                                                   // Fine dell’interfaccia MessageApiContract.
