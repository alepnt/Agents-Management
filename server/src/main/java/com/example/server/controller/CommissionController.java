package com.example.server.controller; // Package del controller

import com.example.common.api.CommissionApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.CommissionDTO; // Import delle dipendenze necessarie
import com.example.server.service.CommissionService; // Import delle dipendenze necessarie
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
@RequestMapping("/api/commissions") // Imposta il percorso base degli endpoint
public class CommissionController implements CommissionApiContract { // Dichiarazione della classe controller

    private final CommissionService commissionService; // Definizione di una dipendenza iniettata

    public CommissionController(CommissionService commissionService) { // Inizio di un metodo esposto dal controller
        this.commissionService = commissionService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<CommissionDTO> listCommissions() { // Inizio di un metodo esposto dal controller
        return commissionService.findAll(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<CommissionDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return commissionService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public CommissionDTO create(@RequestBody CommissionDTO commissionDTO) { // Inizio di un metodo esposto dal controller
        return commissionService.create(commissionDTO); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public CommissionDTO update(@PathVariable Long id, @RequestBody CommissionDTO commissionDTO) { // Inizio di un metodo esposto dal controller
        return commissionService.update(id, commissionDTO) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commissione non trovata")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = commissionService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commissione non trovata"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
