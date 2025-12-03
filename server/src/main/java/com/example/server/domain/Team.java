package com.example.server.domain; // Definisce il package della classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per la colonna
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per la tabella

import java.util.Objects; // Importa utility per equals e hashCode

@Table("teams") // Mappa la classe sulla tabella teams
public class Team { // Rappresenta un team aziendale

    @Id // Indica la chiave primaria
    @Column("id") // Colonna primaria in minuscolo
    private Long id; // Identificativo del team

    private String name; // Nome del team

    public Team(Long id, String name) { // Costruttore completo
        this.id = id; // Assegna l'id
        this.name = name; // Assegna il nome del team
    }

    public Long getId() { // Restituisce l'id del team
        return id; // Ritorna l'identificativo
    }

    public String getName() { // Restituisce il nome del team
        return name; // Ritorna il nome
    }

    @Override // Override del confronto di uguaglianza
    public boolean equals(Object o) { // Confronta due team
        if (this == o) return true; // Se è lo stesso oggetto sono uguali
        if (!(o instanceof Team team)) return false; // Se il tipo è diverso non sono uguali
        return Objects.equals(id, team.id); // Sono uguali se l'id coincide
    }

    @Override // Override del calcolo hash
    public int hashCode() { // Calcola l'hash basato sull'id
        return Objects.hash(id); // Usa l'id per l'hash
    }
}
