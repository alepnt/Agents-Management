package com.example.client.command; // Package dei comandi lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate REST verso il backend.
import com.example.common.dto.DocumentHistoryDTO; // DTO per una voce dello storico documentale.
import com.example.common.dto.InvoiceDTO; // DTO che rappresenta una fattura.
import com.example.common.dto.InvoicePaymentRequest; // Payload per registrare un pagamento.
import com.example.common.enums.DocumentType; // Tipo documento (INVOICE) utilizzato nei risultati.

import java.util.List; // API Java per liste.

/**
 * Comando che registra il pagamento di una fattura.
 * L’operazione aggiorna il documento e produce uno storico documentale.
 */
public class RegisterInvoicePaymentCommand implements ClientCommand<InvoiceDTO> { // Il comando restituisce la fattura
                                                                                  // aggiornata.

    private final Long id; // Identificativo della fattura da aggiornare.
    private final InvoicePaymentRequest paymentRequest; // Dati del pagamento (data, importo, ecc.).

    public RegisterInvoicePaymentCommand(Long id, // Costruttore del comando.
            InvoicePaymentRequest paymentRequest) {
        this.id = id; // Memorizza l’ID della fattura.
        this.paymentRequest = paymentRequest; // Memorizza il payload del pagamento.
    }

    @Override
    public CommandResult<InvoiceDTO> execute(BackendGateway gateway) { // Metodo eseguito dal CommandExecutor.
        InvoiceDTO updated = gateway.registerInvoicePayment(id, paymentRequest); // Esegue la chiamata REST che registra
                                                                                 // il pagamento.

        List<DocumentHistoryDTO> history = gateway.invoiceHistory(id); // Recupera lo storico aggiornato relativo alla
                                                                       // fattura.

        return CommandResult.withHistory( // Restituisce risultato + storico:
                updated, // - fattura aggiornata
                id, // - id del documento
                DocumentType.INVOICE, // - tipo documento
                history // - snapshot dello storico aggiornato
        );
    }

    @Override
    public String description() { // Descrizione del comando per il Memento.
        return "Pagamento fattura #" + id; // Etichetta leggibile dell’operazione eseguita.
    }
} // Fine classe RegisterInvoicePaymentCommand.
