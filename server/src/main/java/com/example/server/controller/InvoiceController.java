package com.example.server.controller; // Package del controller

import com.example.common.api.InvoiceApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.DocumentHistoryDTO; // Import delle dipendenze necessarie
import com.example.common.dto.InvoiceDTO; // Import delle dipendenze necessarie
import com.example.common.dto.InvoicePaymentRequest; // Import delle dipendenze necessarie
import com.example.server.service.InvoiceService; // Import delle dipendenze necessarie
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
@RequestMapping("/api/invoices") // Imposta il percorso base degli endpoint
public class InvoiceController implements InvoiceApiContract { // Dichiarazione della classe controller

    private final InvoiceService invoiceService; // Definizione di una dipendenza iniettata

    public InvoiceController(InvoiceService invoiceService) { // Inizio di un metodo esposto dal controller
        this.invoiceService = invoiceService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<InvoiceDTO> listInvoices() { // Inizio di un metodo esposto dal controller
        return invoiceService.findAll(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<InvoiceDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return invoiceService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public InvoiceDTO create(@RequestBody InvoiceDTO invoiceDTO) { // Inizio di un metodo esposto dal controller
        return invoiceService.create(invoiceDTO); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public InvoiceDTO update(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) { // Inizio di un metodo esposto dal controller
        return invoiceService.update(id, invoiceDTO) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = invoiceService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping("/{id}/payments") // Mapping per una richiesta POST
    public InvoiceDTO registerPayment(@PathVariable Long id, @RequestBody InvoicePaymentRequest paymentRequest) { // Inizio di un metodo esposto dal controller
        return invoiceService.registerPayment(id, paymentRequest) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}/history") // Mapping per una richiesta GET
    public List<DocumentHistoryDTO> history(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return invoiceService.history(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

} // Istruzione di gestione del controller
