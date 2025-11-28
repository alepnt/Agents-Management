package com.example.client.command; // Package che contiene l’implementazione del pattern Command lato client.

import com.example.client.service.BackendGateway; // Componente che incapsula le chiamate HTTP verso il backend.
import com.example.common.dto.CustomerDTO; // DTO che rappresenta un cliente dell’anagrafica.

/**
 * Comando che crea un nuovo cliente nell’anagrafica centralizzata.
 * Implementa il pattern Command e incapsula l’operazione di creazione
 * delegandola al BackendGateway.
 */
public class CreateCustomerCommand implements ClientCommand<CustomerDTO> { // Il comando restituisce un CustomerDTO.

    private final CustomerDTO customer; // Cliente da creare, fornito dal chiamante.

    public CreateCustomerCommand(CustomerDTO customer) { // Costruttore che accetta il DTO.
        this.customer = customer; // Salva il DTO per l’uso nell’esecuzione del comando.
    }

    @Override
    public CommandResult<CustomerDTO> execute(BackendGateway gateway) { // Metodo invocato dal CommandExecutor.
        CustomerDTO created = gateway.createCustomer(customer); // Invoca l’endpoint REST per creare il cliente.
        return CommandResult.withoutHistory(created); // Nessun documento legato ai clienti → ritorna senza storico.
    }

    @Override
    public String description() { // Descrizione del comando per il pattern Memento.
        return "Creazione cliente " + customer.getName(); // Descrizione leggibile con il nome del cliente creato.
    }
} // Fine classe CreateCustomerCommand.
