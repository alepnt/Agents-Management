package com.example.server.service; // Definisce il package per le strategie di distribuzione.

public enum DistributionStrategy { // Enumera le strategie disponibili per distribuire le commissioni.
    PERCENTAGE, // Distribuzione proporzionale basata su percentuali.
    BARRIER // Distribuzione con soglie o barriere.
}
