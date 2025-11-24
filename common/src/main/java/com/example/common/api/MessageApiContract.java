package com.example.common.api;

import com.example.common.dto.MessageDTO;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API per la gestione dei messaggi.
 */
public interface MessageApiContract {

    /**
     * Restituisce la lista completa dei messaggi; la collezione non è mai {@code null}.
     */
    @NonNull
    List<MessageDTO> listMessages();

    /**
     * Cerca un messaggio per id. L'id deve essere valorizzato; il risultato può essere vuoto ma mai
     * {@code null}.
     */
    @NonNull
    Optional<MessageDTO> findById(@NonNull Long id);

    /**
     * Crea un nuovo messaggio, richiedendo un payload non nullo.
     */
    @NonNull
    MessageDTO create(@NonNull MessageDTO message);

    /**
     * Aggiorna un messaggio esistente. Id e payload devono essere valorizzati.
     */
    @NonNull
    MessageDTO update(@NonNull Long id, @NonNull MessageDTO message);

    /**
     * Elimina il messaggio con l'id indicato; l'id non può essere nullo.
     */
    void delete(@NonNull Long id);
}
