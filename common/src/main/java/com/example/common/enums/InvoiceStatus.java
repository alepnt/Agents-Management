package com.example.common.enums; // Package che contiene gli enum condivisi tra client e server.

/**
 * Stato dei documenti di fatturazione condiviso fra client e server.
 * Rappresenta il ciclo di vita di una fattura nell’applicazione.
 */
public enum InvoiceStatus { // Enumerazione degli stati possibili di una fattura.

    DRAFT, // Fattura in bozza, non ancora inviata.
    SENT, // Fattura inviata al cliente ma non ancora pagata.
    PAID, // Fattura saldata completamente.
    CANCELLED // Fattura annullata e non più valida.
} // Fine dell'enum InvoiceStatus.
