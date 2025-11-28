package com.example.client.command; // Package che contiene i comandi lato client.

import com.example.client.service.BackendGateway; // Gateway usato dal client per comunicare con il backend.

/**
 * Interfaccia base del Command pattern lato client.
 * Ogni comando incapsula un’operazione che interagisce con il backend
 * tramite BackendGateway e restituisce un risultato tipizzato.
 */
public interface ClientCommand<T> { // Interfaccia generica parametrizzata sul tipo di risultato.

    /**
     * Esegue il comando utilizzando il gateway di backend.
     *
     * @param gateway gateway che consente l’accesso ai servizi REST del server
     * @return risultato dell’operazione incapsulato in un CommandResult
     */
    CommandResult<T> execute(BackendGateway gateway); // Metodo principale del Command pattern.

    /**
     * Restituisce una descrizione testuale del comando,
     * utile per logging, debug e strumenti diagnostici.
     *
     * @return descrizione leggibile del comando
     */
    String description(); // Descrizione dell’operazione incapsulata.
} // Fine interfaccia ClientCommand.
