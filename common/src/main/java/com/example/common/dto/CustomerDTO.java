package com.example.common.dto;                                   // Package che contiene i DTO condivisi tra client e server.

import java.time.Instant;                                         // Timestamp UTC per auditing.
import java.util.Objects;                                         // Utility per equals(), hashCode() e confronti null-safe.

/**
 * DTO per rappresentare un cliente nell'anagrafica condivisa.
 * Contiene dati identificativi, fiscali e di contatto.
 */
public class CustomerDTO {                                        // DTO mutabile che modella un cliente del sistema.

    private Long id;                                              // Identificatore del cliente.
    private String name;                                          // Ragione sociale o nome del cliente.
    private String vatNumber;                                     // Partita IVA del cliente (se applicabile).
    private String taxCode;                                       // Codice fiscale del cliente.
    private String email;                                         // Indirizzo email.
    private String phone;                                         // Numero di telefono.
    private String address;                                       // Indirizzo fisico completo.
    private Instant createdAt;                                    // Data/ora di creazione della scheda cliente.
    private Instant updatedAt;                                    // Data/ora dell’ultimo aggiornamento.

    public CustomerDTO() {                                        // Costruttore vuoto richiesto dai framework di serializzazione.
    }

    public CustomerDTO(Long id,                                   // Costruttore completo per inizializzazione diretta.
                       String name,
                       String vatNumber,
                       String taxCode,
                       String email,
                       String phone,
                       String address,
                       Instant createdAt,
                       Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.taxCode = taxCode;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {                                         // Restituisce l’ID del cliente.
        return id;
    }

    public void setId(Long id) {                                  // Imposta l’ID del cliente.
        this.id = id;
    }

    public String getName() {                                     // Restituisce il nome o ragione sociale del cliente.
        return name;
    }

    public void setName(String name) {                            // Imposta il nome o ragione sociale.
        this.name = name;
    }

    public String getVatNumber() {                                // Restituisce la partita IVA.
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {                  // Imposta la partita IVA.
        this.vatNumber = vatNumber;
    }

    public String getTaxCode() {                                  // Restituisce il codice fiscale.
        return taxCode;
    }

    public void setTaxCode(String taxCode) {                      // Imposta il codice fiscale.
        this.taxCode = taxCode;
    }

    public String getEmail() {                                    // Restituisce l’indirizzo email.
        return email;
    }

    public void setEmail(String email) {                          // Imposta l’indirizzo email.
        this.email = email;
    }

    public String getPhone() {                                    // Restituisce il numero di telefono.
        return phone;
    }

    public void setPhone(String phone) {                          // Imposta il numero di telefono.
        this.phone = phone;
    }

    public String getAddress() {                                  // Restituisce l’indirizzo fisico.
        return address;
    }

    public void setAddress(String address) {                      // Imposta l’indirizzo fisico.
        this.address = address;
    }

    public Instant getCreatedAt() {                               // Restituisce il timestamp di creazione.
        return createdAt;
    }

    public Instant getUpdatedAt() {                               // Restituisce il timestamp dell’ultimo aggiornamento.
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {                 // Imposta il timestamp dell’ultimo aggiornamento.
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Instant createdAt) {                 // Imposta il timestamp di creazione.
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {                             // Confronta due CustomerDTO basandosi sull’ID.
        if (this == o) {                                          // Stessa istanza → uguali.
            return true;
        }
        if (o == null || getClass() != o.getClass()) {            // Null o classe diversa → non uguali.
            return false;
        }
        CustomerDTO that = (CustomerDTO) o;                       // Cast dopo controllo classe.
        return Objects.equals(id, that.id);                       // Confronto basato solo sull’ID.
    }

    @Override
    public int hashCode() {                                       // hashCode coerente con equals().
        return Objects.hash(id);
    }

    @Override
    public String toString() {                                    // Rappresentazione leggibile per logging/debug.
        return "CustomerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vatNumber='" + vatNumber + '\'' +
                ", taxCode='" + taxCode + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}                                                                  // Fine della classe CustomerDTO.
