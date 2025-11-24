package com.example.server.service; // Definisce il package che contiene i servizi legati ai clienti.

import com.example.common.dto.CustomerDTO; // Importa il DTO usato per esporre i dati del cliente.
import com.example.server.domain.Customer; // Importa l'entità di dominio del cliente.
import com.example.server.repository.CustomerRepository; // Importa il repository per la persistenza dei clienti.
import com.example.server.service.mapper.CustomerMapper; // Importa il mapper tra entità Customer e CustomerDTO.
import org.springframework.stereotype.Service; // Importa l'annotazione Service di Spring.
import org.springframework.transaction.annotation.Transactional; // Importa l'annotazione per la gestione transazionale.
import org.springframework.util.StringUtils; // Importa utility per la gestione di stringhe.

import java.util.List; // Importa l'interfaccia List.
import java.util.Objects; // Importa metodi di utilità per controlli null-safe.
import java.util.Optional; // Importa Optional per risultati opzionali.

@Service // Indica che la classe è un componente di servizio Spring.
public class CustomerService { // Gestisce le operazioni di business sui clienti.

    private final CustomerRepository customerRepository; // Repository per l'accesso ai dati dei clienti.

    public CustomerService(CustomerRepository customerRepository) { // Costruttore che riceve il repository come dipendenza.
        this.customerRepository = customerRepository; // Inizializza il repository dei clienti.
    }

    public List<CustomerDTO> findAll() { // Restituisce tutti i clienti ordinati per nome.
        return customerRepository.findAllByOrderByNameAsc().stream() // Recupera i clienti e li trasforma in stream.
                .map(CustomerMapper::toDto) // Converte ogni entità Customer in DTO.
                .toList(); // Colleziona i DTO in una lista.
    }

    public Optional<CustomerDTO> findById(Long id) { // Cerca un cliente per id e restituisce un DTO se trovato.
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Valida l'id e interroga il repository.
                .map(CustomerMapper::toDto); // Converte l'entità trovata in DTO.
    }

    @Transactional // Assicura che la creazione avvenga in una transazione.
    public CustomerDTO create(CustomerDTO dto) { // Crea un nuovo cliente dopo averne validato i dati.
        CustomerDTO validatedDto = Objects.requireNonNull(dto, "customer must not be null"); // Garantisce che il DTO non sia null.
        validate(validatedDto); // Valida il contenuto del DTO.
        Customer customer = Objects.requireNonNull(CustomerMapper.fromDto(validatedDto), // Converte il DTO in entità Customer.
                "mapped customer must not be null"); // Controlla che la conversione non sia null.
        Customer toSave = Objects.requireNonNull(Customer.create( // Crea una nuova istanza normalizzata.
                normalize(customer.getName()), // Normalizza il nome.
                normalize(customer.getVatNumber()), // Normalizza la partita IVA.
                normalize(customer.getTaxCode()), // Normalizza il codice fiscale.
                normalize(customer.getEmail()), // Normalizza l'email.
                normalize(customer.getPhone()), // Normalizza il telefono.
                normalize(customer.getAddress()) // Normalizza l'indirizzo.
        ), "created customer must not be null"); // Assicura che la creazione del cliente non fallisca.
        Customer saved = customerRepository.save(toSave); // Salva il cliente nel database.
        return CustomerMapper.toDto(saved); // Restituisce il cliente salvato come DTO.
    }

    @Transactional // Garantisce che l'aggiornamento sia atomico.
    public Optional<CustomerDTO> update(Long id, CustomerDTO dto) { // Aggiorna un cliente esistente se presente.
        CustomerDTO validatedDto = Objects.requireNonNull(dto, "customer must not be null"); // Verifica che il DTO non sia null.
        validate(validatedDto); // Valida il contenuto del DTO.
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Cerca il cliente per id.
                .map(existing -> { // Se trovato, procede con l'aggiornamento.
                    Customer updateSource = Objects.requireNonNull(Customer.create( // Crea un cliente di appoggio con dati normalizzati.
                            normalize(validatedDto.getName()), // Normalizza il nome.
                            normalize(validatedDto.getVatNumber()), // Normalizza la partita IVA.
                            normalize(validatedDto.getTaxCode()), // Normalizza il codice fiscale.
                            normalize(validatedDto.getEmail()), // Normalizza l'email.
                            normalize(validatedDto.getPhone()), // Normalizza il telefono.
                            normalize(validatedDto.getAddress()) // Normalizza l'indirizzo.
                    ), "created customer must not be null"); // Controlla che la creazione non sia null.
                    Customer updated = Objects.requireNonNull(existing.updateFrom(updateSource), // Applica gli aggiornamenti all'entità esistente.
                            "updated customer must not be null"); // Verifica che l'oggetto aggiornato sia valido.
                    Customer saved = customerRepository.save(updated); // Salva il cliente aggiornato.
                    return CustomerMapper.toDto(saved); // Restituisce il DTO del cliente aggiornato.
                });
    }

    @Transactional // Esegue la cancellazione in una transazione.
    public boolean delete(Long id) { // Elimina un cliente se esiste.
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Valida l'id e cerca il cliente.
                .map(existing -> { // Se presente, procede con la cancellazione.
                    customerRepository.deleteById(id); // Cancella il cliente per id.
                    return true; // Indica che la cancellazione è avvenuta.
                })
                .orElse(false); // Restituisce false se il cliente non è stato trovato.
    }

    public Customer require(Long id) { // Recupera il cliente o lancia un'eccezione se mancante.
        return customerRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Valida l'id e interroga il repository.
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato")); // Solleva eccezione se non trovato.
    }

    private void validate(CustomerDTO dto) { // Valida che il DTO contenga i dati obbligatori.
        if (dto == null || !StringUtils.hasText(dto.getName())) { // Verifica presenza del DTO e del nome.
            throw new IllegalArgumentException("Il nome del cliente è obbligatorio"); // Segnala errore se il nome manca.
        }
    }

    private String normalize(String value) { // Rimuove gli spazi superflui e gestisce i null.
        return value != null ? value.trim() : null; // Restituisce la stringa ripulita o null.
    }
}
