package com.example.common.enums; // Package contenente gli enum condivisi tra client e server.

/**
 * Azioni che generano una voce di storico documentale.
 * Ogni valore rappresenta un evento significativo registrato nel log dei
 * documenti.
 */
public enum DocumentAction { // Enumerazione delle azioni tracciabili su un documento.

    CREATED, // Il documento è stato creato.
    UPDATED, // Il documento è stato modificato.
    APPROVED, // Il documento è stato approvato e validato.
    PAYMENT_REGISTERED, // È stato registrato un pagamento (tipicamente per fatture).
    STATUS_CHANGED, // Lo stato del documento è cambiato (es. da DRAFT ad ACTIVE).
    DELETED // Il documento è stato eliminato.
} // Fine dell'enum DocumentAction.
