package com.example.client.service;

/**
 * Record che rappresenta un riepilogo essenziale dell’utente autenticato.
 *
 * Questo modello viene tipicamente restituito dal backend dopo il login
 * o dopo la registrazione, e contiene solo le informazioni minimali
 * necessarie al client per identificare l’utente e determinare il suo ruolo.
 *
 * Perché un record?
 * - è immutabile
 * - ideale per DTO semplici
 * - serializza/deserializza facilmente tramite Jackson
 *
 * Campi inclusi:
 * - id: identificativo interno del database
 * - email: indirizzo email dell'utente
 * - displayName: nome visualizzato nell'interfaccia
 * - azureId: identificativo esterno per login SSO
 * - roleId: ruolo associato all’utente
 * - teamId: team dell’utente (se presente)
 *
 * Non contiene informazioni sensibili come password o permessi dettagliati.
 */
public record UserSummary(
                Long id,
                String email,
                String displayName,
                String azureId,
                Long roleId,
                Long teamId) {
}
