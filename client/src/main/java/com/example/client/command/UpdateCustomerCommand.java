package com.example.client.command; // Package che contiene le implementazioni del pattern Command lato client.

import com.example.client.service.BackendGateway; // Gateway che incapsula le chiamate REST verso il backend.
import com.example.common.dto.CustomerDTO; // DTO che rappresenta un cliente dell’anagrafica.

/**
 * Comando che aggiorna i dati di un cliente esistente.
 * Non genera storico documentale, poiché i clienti non producono history.
 */
public class UpdateCustomerCommand implements ClientCommand<CustomerDTO> { // Il comando restituisce il CustomerDTO
                                                                           // aggiornato.

    private final Long id; // Identificativo del cliente da aggiornare.
    private final CustomerDTO customer; // Dati aggiornati del cliente.

    public UpdateCustomerCommand(Long id, CustomerDTO customer) { // Costruttore del comando.
        this.id = id; // Salva l'identificativo da aggiornare.
        this.customer = customer; // Salva il DTO contenente i nuovi dati.
    }

    @Override
    public CommandResult<CustomerDTO> execute(BackendGateway gateway) { // Esecuzione del comando.
        CustomerDTO updated = gateway.updateCustomer(id, customer); // Invoca il backend per aggiornare i dati del
                                                                    // cliente.
        return CommandResult.withoutHistory(updated); // Nessuno storico correlato → ritorna senza history.
    }

    @Override
    public String description() { // Descrizione testuale per il Memento.
        return "Aggiornamento cliente #" + id; // Etichetta leggibile dell’operazione.
    }
} // Fine classe UpdateCustomerCommand.
