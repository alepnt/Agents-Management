package com.example.server.controller; // Package del controller

import com.example.common.api.ContractApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.ContractDTO; // Import delle dipendenze necessarie
import com.example.common.dto.DocumentHistoryDTO; // Import delle dipendenze necessarie
import com.example.server.service.ContractService; // Import delle dipendenze necessarie
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
@RequestMapping("/api/contracts") // Imposta il percorso base degli endpoint
public class ContractController implements ContractApiContract { // Dichiarazione della classe controller

    private final ContractService contractService; // Definizione di una dipendenza iniettata

    public ContractController(ContractService contractService) { // Inizio di un metodo esposto dal controller
        this.contractService = contractService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<ContractDTO> listContracts() { // Inizio di un metodo esposto dal controller
        return contractService.findAll(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<ContractDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return contractService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public ContractDTO create(@RequestBody ContractDTO contractDTO) { // Inizio di un metodo esposto dal controller
        return contractService.create(contractDTO); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public ContractDTO update(@PathVariable Long id, @RequestBody ContractDTO contractDTO) { // Inizio di un metodo esposto dal controller
        return contractService.update(id, contractDTO) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = contractService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}/history") // Mapping per una richiesta GET
    public List<DocumentHistoryDTO> history(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return contractService.history(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
