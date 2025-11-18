package com.example.common.api;

import com.example.common.dto.MessageDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API per la gestione dei messaggi.
 */
public interface MessageApiContract {

    List<MessageDTO> listMessages();

    Optional<MessageDTO> findById(Long id);

    MessageDTO create(MessageDTO message);

    MessageDTO update(Long id, MessageDTO message);

    void delete(Long id);
}
