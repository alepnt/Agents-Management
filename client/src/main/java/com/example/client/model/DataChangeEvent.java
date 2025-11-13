package com.example.client.model;

import java.time.Instant;

/**
 * Evento pubblicato quando cambia lo stato dei dati locali.
 */
public record DataChangeEvent(DataChangeType type, Instant occurredAt) {
}
