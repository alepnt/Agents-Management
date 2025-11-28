package com.example.common.dto; // Package che contiene i DTO condivisi dell’applicazione.

import java.util.Objects; // Utility per equals(), hashCode() e controlli null-safe.

/**
 * DTO per rappresentare un team.
 * Utilizzato per la gestione dell’anagrafica team e per associare utenti o
 * agenti a un gruppo.
 */
public class TeamDTO { // DTO mutabile che modella un team dell’organizzazione.

    private Long id; // Identificatore univoco del team.
    private String name; // Nome del team (es. "Lombardia Nord", "Veneto Est").

    public TeamDTO() { // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public TeamDTO(Long id, String name) { // Costruttore completo per inizializzazione diretta.
        this.id = id;
        this.name = name;
    }

    public Long getId() { // Restituisce l’ID del team.
        return id;
    }

    public void setId(Long id) { // Imposta l’ID del team.
        this.id = id;
    }

    public String getName() { // Restituisce il nome del team.
        return name;
    }

    public void setName(String name) { // Imposta il nome del team.
        this.name = name;
    }

    @Override
    public boolean equals(Object o) { // Confronta due TeamDTO in base all’ID.
        if (this == o) { // Stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) { // Null o classi diverse → non uguali.
            return false;
        }
        TeamDTO teamDTO = (TeamDTO) o; // Cast sicuro dopo controllo.
        return Objects.equals(id, teamDTO.id); // Due team sono uguali se hanno lo stesso ID.
    }

    @Override
    public int hashCode() { // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() { // Rappresentazione leggibile utile per log/debug.
        return "TeamDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} // Fine della classe TeamDTO.
