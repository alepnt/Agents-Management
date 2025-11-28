package com.example.client.model;
// Package dei modelli lato client, include DTO e record per gestione notifiche.

/**
 * Record che rappresenta una sottoscrizione a un canale di notifica.
 * 
 * Utilizzato dal client quando deve registrarsi (o ri-registrarsi)
 * a un particolare canale di messaggistica / notifiche push.
 *
 * Esempi di canali:
 * - "user-42"
 * - "team-7"
 * - "broadcast"
 *
 * Il server potrà poi inviare notifiche a chi ha sottoscritto un determinato
 * canale.
 *
 * Essendo un record, è immutabile e ideale come request DTO.
 */
public record NotificationSubscription(
        Long userId, // Identificativo dell’utente che si sottoscrive
        String channel // Nome del canale a cui si iscrive
) {
    // I record generano automaticamente constructor, equals, hashCode,
    // toString, e metodi accessor (userId(), channel()).
}
