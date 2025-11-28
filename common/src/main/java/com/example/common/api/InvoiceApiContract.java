package com.example.common.api;                               // Package che definisce i contratti API condivisi fra client e server.

import java.util.List;             // DTO che rappresenta una singola voce dello storico documentale.
import java.util.Optional;                     // DTO principale che rappresenta una fattura.

import com.example.common.dto.DocumentHistoryDTO;          // DTO utilizzato per registrare il pagamento di una fattura.
import com.example.common.dto.InvoiceDTO;                                        // Supporto per elenchi di risultati.
import com.example.common.dto.InvoicePaymentRequest;                                    // Gestione del risultato opzionale (fattura non garantita).

/**
 * Contratto API condiviso fra client e server per la gestione delle fatture.
 * Definisce operazioni CRUD, registrazione pagamenti e accesso allo storico.
 */
public interface InvoiceApiContract {                         // Interfaccia che espone le funzioni legate alle fatture.

    List<InvoiceDTO> listInvoices();                          // Restituisce l'elenco completo delle fatture.

    Optional<InvoiceDTO> findById(Long id);                   // Recupera una singola fattura tramite ID, se esiste.

    InvoiceDTO create(InvoiceDTO invoiceDTO);                 // Crea una nuova fattura utilizzando i dati forniti nel DTO.

    InvoiceDTO update(Long id, InvoiceDTO invoiceDTO);        // Aggiorna la fattura identificata dall’ID con i dati del DTO.

    void delete(Long id);                                     // Elimina la fattura associata all’ID indicato.

    InvoiceDTO registerPayment(Long id,                       // Registra il pagamento della fattura identificata dall’ID,
                                InvoicePaymentRequest paymentRequest); // utilizzando i dati di pagamento forniti.

    List<DocumentHistoryDTO> history(Long id);                // Restituisce lo storico delle attività associate alla fattura.
}                                                             // Fine dell’interfaccia InvoiceApiContract.
