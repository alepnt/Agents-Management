package com.example.server.domain; // Definisce il package della classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per la tabella

import java.util.Objects; // Importa utility per equals e hashCode

@Table("roles") // Mappa la classe sulla tabella roles
public class Role { // Rappresenta un ruolo applicativo

    @Id // Indica la chiave primaria
    @Column("id") // Allinea il nome colonna con lo schema H2
    private Long id; // Identificativo del ruolo

    @Column("name") // Allinea il nome colonna con lo schema H2
    private String name; // Nome del ruolo

    public Role(Long id, String name) { // Costruttore completo
        this.id = id; // Assegna l'id
        this.name = name; // Assegna il nome
    }

    public Long getId() { // Restituisce l'id del ruolo
        return id; // Ritorna l'identificativo
    }

    public String getName() { // Restituisce il nome del ruolo
        return name; // Ritorna il nome
    }

    @Override // Override del confronto di uguaglianza
    public boolean equals(Object o) { // Confronta due ruoli
        if (this == o) return true; // Se è lo stesso oggetto sono uguali
        if (!(o instanceof Role role)) return false; // Se il tipo è diverso non sono uguali
        return Objects.equals(id, role.id); // Sono uguali se l'id coincide
    }

    @Override // Override del calcolo hash
    public int hashCode() { // Calcola l'hash basato sull'id
        return Objects.hash(id); // Usa l'id per l'hash
    }
}
