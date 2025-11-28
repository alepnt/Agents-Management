package com.example.client.command; // Package dedicato al pattern Command lato client.

import com.example.common.dto.DocumentHistoryDTO; // DTO per rappresentare elementi di storico.
import com.example.common.enums.DocumentType; // Enum per identificare il tipo di documento correlato al comando.

import java.util.Collections; // Utility per liste immutabili.
import java.util.List; // API Java per collection List.

/**
 * Risultato generico di un comando eseguito lato client.
 *
 * Un CommandResult incapsula:
 * - il valore risultante dell'operazione (value)
 * - l'id del documento eventualmente coinvolto dal comando (documentId)
 * - la tipologia del documento (documentType)
 * - uno snapshot immutabile dello storico documentale aggiornato
 * (historySnapshot)
 *
 * È un record, quindi immutabile e con metodi predefiniti (equals, hashCode,
 * toString).
 */
public record CommandResult<T>(T value, // Valore restituito dal comando.
        Long documentId, // Identificativo del documento modificato o consultato.
        DocumentType documentType, // Tipo del documento coinvolto.
        List<DocumentHistoryDTO> historySnapshot) { // Copia dello storico dopo l'operazione.

    /**
     * Factory method per creare un risultato di comando privo di storico.
     *
     * @param value risultato del comando
     * @return CommandResult senza dati documentali associati
     */
    public static <T> CommandResult<T> withoutHistory(T value) {
        return new CommandResult<>(value, null, null, List.of()); // Crea un risultato “vuoto” per lo storico.
    }

    /**
     * Factory method per creare un risultato contenente anche lo storico
     * aggiornato.
     *
     * @param value           risultato del comando
     * @param documentId      id del documento coinvolto
     * @param documentType    tipo del documento coinvolto
     * @param historySnapshot snapshot dello storico (copiato per immutabilità)
     * @return CommandResult completo di contesto documentale
     */
    public static <T> CommandResult<T> withHistory(T value,
            Long documentId,
            DocumentType documentType,
            List<DocumentHistoryDTO> historySnapshot) {
        return new CommandResult<>(
                value,
                documentId,
                documentType,
                historySnapshot == null ? List.of() : List.copyOf(historySnapshot) // Copia difensiva della lista
                                                                                   // fornita.
        );
    }

    /**
     * Override necessario per garantire che la lista rientrante sia sempre
     * immutabile.
     *
     * @return versione non modificabile dello snapshot dello storico
     */
    @Override
    public List<DocumentHistoryDTO> historySnapshot() {
        return historySnapshot == null
                ? List.of() // In caso di null, ritorna lista vuota.
                : Collections.unmodifiableList(historySnapshot); // Garanzia di immutabilità esterna.
    }
} // Fine record CommandResult.
