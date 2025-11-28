package com.example.common.enums; // Package che contiene gli enum condivisi dell'applicazione.

/**
 * Stato di avanzamento di un contratto commerciale.
 * Utilizzato per indicare il ciclo di vita di un contratto.
 */
public enum ContractStatus { // Enumerazione che rappresenta gli stati possibili di un contratto.

    DRAFT, // Contratto creato ma non ancora attivo.
    ACTIVE, // Contratto attualmente in vigore.
    SUSPENDED, // Contratto sospeso temporaneamente.
    TERMINATED // Contratto terminato o annullato in modo definitivo.
} // Fine dell'enum ContractStatus.
