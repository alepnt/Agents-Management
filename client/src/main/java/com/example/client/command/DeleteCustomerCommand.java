package com.example.client.command; // Package dedicato al pattern Command lato client.

import com.example.client.service.BackendGateway; // Componente che espone le operazioni REST verso il backend.

/**
 * Comando che elimina un cliente dall’anagrafica centralizzata.
 * Non produce storico documentale, poiché i clienti non generano voci di
 * history.
 */
public class DeleteCustomerCommand implements ClientCommand<Void> { // Il comando restituisce un risultato di tipo Void.

    private final Long id; // Identificativo del cliente da eliminare.

    public DeleteCustomerCommand(Long id) { // Costruttore del comando.
        this.id = id; // Memorizza l'id per l'uso nell'esecuzione.
    }

    @Override
    public CommandResult<Void> execute(BackendGateway gateway) { // Metodo chiamato dal CommandExecutor.
        gateway.deleteCustomer(id); // Invoca la DELETE sul backend per rimuovere il cliente.
        return CommandResult.withoutHistory(null); // I clienti non hanno storico → ritorna senza history.
    }

    @Override
    public String description() { // Descrizione dell’operazione per il Memento.
        return "Eliminazione cliente #" + id; // Testo leggibile con riferimento al cliente eliminato.
    }
} // Fine della classe DeleteCustomerCommand.
