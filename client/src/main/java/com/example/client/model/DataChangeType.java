package com.example.client.model;
// Package dei modelli lato client: contiene anche enum e strutture di supporto.

/**
 * Tipologie di variazioni dati gestite dal client.
 * Questo enum rappresenta gli eventi di modifica che possono
 * verificarsi nel sistema (es. aggiornamento di una fattura,
 * creazione di un agente, modifica articolI, ecc.).
 * 
 * È generalmente usato per notificare la UI, aggiornare tabelle,
 * invalidare cache locali o propagare refresh tra controller.
 */
public enum DataChangeType {

    INVOICE, // Indica che una fattura è stata creata, aggiornata o cancellata.
    CONTRACT, // Evento relativo ai contratti (nuovi, aggiornati, chiusi).
    CUSTOMER, // Modifica in anagrafica clienti.
    ARTICLE, // Aggiornamento nel catalogo articoli.
    AGENT, // Modifica dati degli agenti.
    TEAM, // Cambiamenti nella gestione dei team.
    ROLE, // Aggiornamenti sui ruoli utente.
    USER, // Variazioni dati utente (es. credenziali, permessi).
    MESSAGE, // Nuovo messaggio ricevuto/inviato o stato aggiornato.
    COMMISSION // Aggiornamenti sulle commissioni calcolate o liquidate.
}
