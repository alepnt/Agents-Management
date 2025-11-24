package com.example.server.controller; // Package del controller

import com.example.common.api.CustomerApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.CustomerDTO; // Import delle dipendenze necessarie
import com.example.server.service.CustomerService; // Import delle dipendenze necessarie
import org.springframework.http.HttpStatus; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.DeleteMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PathVariable; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PutMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie
import org.springframework.web.server.ResponseStatusException; // Import delle dipendenze necessarie

import java.util.List; // Import delle dipendenze necessarie
import java.util.Optional; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/customers") // Imposta il percorso base degli endpoint
public class CustomerController implements CustomerApiContract { // Dichiarazione della classe controller

    private final CustomerService customerService; // Definizione di una dipendenza iniettata

    public CustomerController(CustomerService customerService) { // Inizio di un metodo esposto dal controller
        this.customerService = customerService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<CustomerDTO> listCustomers() { // Inizio di un metodo esposto dal controller
        return customerService.findAll(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<CustomerDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return customerService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public CustomerDTO create(@RequestBody CustomerDTO customer) { // Inizio di un metodo esposto dal controller
        return customerService.create(customer); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public CustomerDTO update(@PathVariable Long id, @RequestBody CustomerDTO customer) { // Inizio di un metodo esposto dal controller
        return customerService.update(id, customer) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = customerService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
