package com.example.common.dto; // Package che contiene i DTO condivisi tra client e server.

import java.util.Objects; // Utility per equals(), hashCode() e confronti null-safe.

/**
 * DTO per rappresentare un ruolo utente.
 * Utilizzato per il mapping dell’anagrafica ruoli e per la gestione dei
 * permessi applicativi.
 */
public class RoleDTO { // DTO mutabile che modella un ruolo nel sistema.

    private Long id; // Identificatore univoco del ruolo.
    private String name; // Nome del ruolo (es. "Admin", "Agent", "Manager").

    public RoleDTO() { // Costruttore vuoto richiesto per la serializzazione JSON.
    }

    public RoleDTO(Long id, String name) { // Costruttore completo per inizializzazione diretta del DTO.
        this.id = id;
        this.name = name;
    }

    public Long getId() { // Restituisce l'ID del ruolo.
        return id;
    }

    public void setId(Long id) { // Imposta l'ID del ruolo.
        this.id = id;
    }

    public String getName() { // Restituisce il nome del ruolo.
        return name;
    }

    public void setName(String name) { // Imposta il nome del ruolo.
        this.name = name;
    }

    @Override
    public boolean equals(Object o) { // Confronto basato sull’ID del ruolo.
        if (this == o) { // Se è lo stesso oggetto → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) { // Se null o classe diversa → non uguali.
            return false;
        }
        RoleDTO roleDTO = (RoleDTO) o; // Cast dopo verifica classe.
        return Objects.equals(id, roleDTO.id); // Due ruoli sono uguali se hanno lo stesso ID.
    }

    @Override
    public int hashCode() { // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() { // Rappresentazione leggibile utile per debug/logging.
        return "RoleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} // Fine della classe RoleDTO.
