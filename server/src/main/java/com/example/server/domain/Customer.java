package com.example.server.domain; // Definisce il package della classe

import org.springframework.data.annotation.Id; // Importa l'annotazione per la chiave primaria
import org.springframework.data.annotation.LastModifiedDate; // Importa l'annotazione per il tracciamento dell'ultimo aggiornamento
import org.springframework.data.relational.core.mapping.Column; // Importa l'annotazione per mappare le colonne
import org.springframework.data.relational.core.mapping.Table; // Importa l'annotazione per mappare la tabella

import java.time.Instant; // Importa il tipo per timestamp
import java.util.Objects; // Importa utilità per confronti e hash

@Table("customers") // Associa la classe alla tabella "customers"
public class Customer { // Definisce l'entità Customer

    @Id // Identifica il campo come chiave primaria
    private Long id; // Identificativo univoco del cliente

    private String name; // Nome del cliente

    @Column("vat_number") // Mappa il campo alla colonna vat_number
    private String vatNumber; // Partita IVA del cliente

    @Column("tax_code") // Mappa il campo alla colonna tax_code
    private String taxCode; // Codice fiscale del cliente

    private String email; // Email del cliente

    private String phone; // Numero di telefono del cliente

    private String address; // Indirizzo del cliente

    @Column("created_at") // Mappa il campo alla colonna created_at
    private Instant createdAt; // Timestamp di creazione del record

    @LastModifiedDate // Indica che il campo viene aggiornato automaticamente all'ultimo salvataggio
    @Column("updated_at") // Mappa il campo alla colonna updated_at
    private Instant updatedAt; // Timestamp di ultimo aggiornamento del record

    public Customer(Long id, // Costruttore completo
                    String name, // Nome del cliente
                    String vatNumber, // Partita IVA
                    String taxCode, // Codice fiscale
                    String email, // Email
                    String phone, // Telefono
                    String address, // Indirizzo
                    Instant createdAt, // Data di creazione
                    Instant updatedAt) { // Data di ultimo aggiornamento
        this.id = id; // Imposta l'id
        this.name = name; // Imposta il nome
        this.vatNumber = vatNumber; // Imposta la partita IVA
        this.taxCode = taxCode; // Imposta il codice fiscale
        this.email = email; // Imposta l'email
        this.phone = phone; // Imposta il telefono
        this.address = address; // Imposta l'indirizzo
        this.createdAt = createdAt; // Imposta la data di creazione
        this.updatedAt = updatedAt; // Imposta la data di ultimo aggiornamento
    }

    public static Customer create(String name, // Factory method per creare un nuovo cliente
                                  String vatNumber, // Partita IVA del cliente
                                  String taxCode, // Codice fiscale del cliente
                                  String email, // Email del cliente
                                  String phone, // Telefono del cliente
                                  String address) { // Indirizzo del cliente
        return new Customer(null, name, vatNumber, taxCode, email, phone, address, null, null); // Crea un cliente senza id e senza date
    }

    public Customer withId(Long id) { // Restituisce una copia del cliente con un id specificato
        return new Customer(id, name, vatNumber, taxCode, email, phone, address, createdAt, updatedAt); // Crea una nuova istanza con l'id fornito
    }

    public Customer updateFrom(Customer source) { // Restituisce una copia aggiornata dai dati di un altro cliente
        return new Customer(id, // Mantiene lo stesso id
                source.name, // Usa il nuovo nome
                source.vatNumber, // Usa la nuova partita IVA
                source.taxCode, // Usa il nuovo codice fiscale
                source.email, // Usa la nuova email
                source.phone, // Usa il nuovo telefono
                source.address, // Usa il nuovo indirizzo
                createdAt, // Mantiene la data di creazione originale
                updatedAt); // Mantiene la data di ultimo aggiornamento corrente
    }

    public Long getId() { // Restituisce l'id del cliente
        return id; // Ritorna il valore di id
    }

    public String getName() { // Restituisce il nome del cliente
        return name; // Ritorna il valore di name
    }

    public String getVatNumber() { // Restituisce la partita IVA
        return vatNumber; // Ritorna il valore di vatNumber
    }

    public String getTaxCode() { // Restituisce il codice fiscale
        return taxCode; // Ritorna il valore di taxCode
    }

    public String getEmail() { // Restituisce l'email del cliente
        return email; // Ritorna il valore di email
    }

    public String getPhone() { // Restituisce il telefono del cliente
        return phone; // Ritorna il valore di phone
    }

    public String getAddress() { // Restituisce l'indirizzo del cliente
        return address; // Ritorna il valore di address
    }

    public Instant getCreatedAt() { // Restituisce la data di creazione
        return createdAt; // Ritorna il valore di createdAt
    }

    public Instant getUpdatedAt() { // Restituisce la data di ultimo aggiornamento
        return updatedAt; // Ritorna il valore di updatedAt
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public boolean equals(Object o) { // Confronta due clienti per uguaglianza
        if (this == o) { // Se i riferimenti coincidono, sono uguali
            return true; // Restituisce vero
        }
        if (o == null || getClass() != o.getClass()) { // Se l'oggetto è nullo o di classe diversa
            return false; // Restituisce falso
        }
        Customer customer = (Customer) o; // Effettua il cast a Customer
        return Objects.equals(id, customer.id); // Confronta gli id per stabilire l'uguaglianza
    }

    @Override // Indica che si sta sovrascrivendo un metodo della superclasse
    public int hashCode() { // Calcola l'hash del cliente
        return Objects.hash(id); // Usa l'id per calcolare l'hash
    }
} // Chiude la definizione della classe
