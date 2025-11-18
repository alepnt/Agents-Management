package com.example.common.api;

import com.example.common.dto.InvoiceLineDTO;

import java.util.List;
import java.util.Optional;

/**
 * Contratto API condiviso per la gestione delle righe fattura.
 */
public interface InvoiceLineApiContract {

    /**
     * Restituisce tutte le righe di fattura o, se specificato, solo quelle di una fattura.
     *
     * @param invoiceId identificativo della fattura da filtrare (opzionale)
     * @return elenco di righe fattura
     */
    List<InvoiceLineDTO> listInvoiceLines(Long invoiceId);

    Optional<InvoiceLineDTO> findById(Long id);

    InvoiceLineDTO create(InvoiceLineDTO invoiceLine);

    InvoiceLineDTO update(Long id, InvoiceLineDTO invoiceLine);

    void delete(Long id);
}
