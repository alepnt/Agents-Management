package com.example.server.controller; // Package del controller

import com.example.common.api.InvoiceLineApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.InvoiceLineDTO; // Import delle dipendenze necessarie
import com.example.server.service.InvoiceLineService; // Import delle dipendenze necessarie
import org.springframework.http.HttpStatus; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.DeleteMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PathVariable; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PutMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie
import org.springframework.web.server.ResponseStatusException; // Import delle dipendenze necessarie

import java.util.List; // Import delle dipendenze necessarie
import java.util.Optional; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/invoice-lines") // Imposta il percorso base degli endpoint
public class InvoiceLineController implements InvoiceLineApiContract { // Dichiarazione della classe controller

    private final InvoiceLineService invoiceLineService; // Definizione di una dipendenza iniettata

    public InvoiceLineController(InvoiceLineService invoiceLineService) { // Inizio di un metodo esposto dal controller
        this.invoiceLineService = invoiceLineService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<InvoiceLineDTO> listInvoiceLines(@RequestParam(value = "invoiceId", required = false) Long invoiceId) { // Inizio di un metodo esposto dal controller
        if (invoiceId != null) { // Controllo condizionale
            return invoiceLineService.findByInvoiceId(invoiceId); // Restituisce il risultato dell operazione
        } // Istruzione di gestione del controller
        return invoiceLineService.findAll(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<InvoiceLineDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return invoiceLineService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public InvoiceLineDTO create(@RequestBody InvoiceLineDTO invoiceLine) { // Inizio di un metodo esposto dal controller
        return invoiceLineService.create(invoiceLine); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public InvoiceLineDTO update(@PathVariable Long id, @RequestBody InvoiceLineDTO invoiceLine) { // Inizio di un metodo esposto dal controller
        return invoiceLineService.update(id, invoiceLine) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Riga fattura non trovata")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = invoiceLineService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Riga fattura non trovata"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
