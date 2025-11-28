package com.example.common.api;                               // Package che raccoglie i contratti API condivisi dell’applicazione.

import java.util.List;                 // DTO che rappresenta una singola riga di fattura.
import java.util.Optional;                                        // Utilizzato per restituire elenchi di risultati.

import com.example.common.dto.InvoiceLineDTO;                                    // Rappresenta risultati opzionali (riga non garantita).

/**
 * Contratto API condiviso per la gestione delle righe fattura.
 * Definisce le operazioni CRUD e la ricerca filtrata per fattura.
 */
public interface InvoiceLineApiContract {                     // Interfaccia che espone le funzionalità sulle righe fattura.

    /**
     * Restituisce tutte le righe di fattura o, se specificato, solo quelle di una determinata fattura.
     *
     * @param invoiceId identificativo della fattura da filtrare (opzionale)
     * @return elenco di righe fattura
     */
    List<InvoiceLineDTO> listInvoiceLines(Long invoiceId);    // Elenco delle righe fattura, filtrabile per ID fattura.

    Optional<InvoiceLineDTO> findById(Long id);               // Recupera una singola riga di fattura tramite ID.

    InvoiceLineDTO create(InvoiceLineDTO invoiceLine);        // Crea una nuova riga di fattura con i dati forniti nel DTO.

    InvoiceLineDTO update(Long id,                            // Aggiorna la riga fattura identificata dall'ID,
                           InvoiceLineDTO invoiceLine);       // applicando i dati presenti nel DTO.

    void delete(Long id);                                     // Elimina la riga fattura associata all'ID specificato.
}                                                             // Fine dell’interfaccia InvoiceLineApiContract.
