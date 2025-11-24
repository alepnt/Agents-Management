package com.example.server.controller; // Package del controller

import com.example.common.api.NotificationSubscriptionApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.NotificationSubscriptionDTO; // Import delle dipendenze necessarie
import com.example.server.service.NotificationSubscriptionService; // Import delle dipendenze necessarie
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
@RequestMapping("/api/notification-subscriptions") // Imposta il percorso base degli endpoint
public class NotificationSubscriptionController implements NotificationSubscriptionApiContract { // Dichiarazione della classe controller

    private final NotificationSubscriptionService subscriptionService; // Definizione di una dipendenza iniettata

    public NotificationSubscriptionController(NotificationSubscriptionService subscriptionService) { // Inizio di un metodo esposto dal controller
        this.subscriptionService = subscriptionService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<NotificationSubscriptionDTO> listSubscriptions(@RequestParam(value = "userId", required = false) Long userId) { // Inizio di un metodo esposto dal controller
        return subscriptionService.list(userId); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/{id}") // Mapping per una richiesta GET
    public Optional<NotificationSubscriptionDTO> findById(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        return subscriptionService.findById(id); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public NotificationSubscriptionDTO create(@RequestBody NotificationSubscriptionDTO subscription) { // Inizio di un metodo esposto dal controller
        return subscriptionService.create(subscription); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public NotificationSubscriptionDTO update(@PathVariable Long id, @RequestBody NotificationSubscriptionDTO subscription) { // Inizio di un metodo esposto dal controller
        return subscriptionService.update(id, subscription) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sottoscrizione non trovata")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        boolean deleted = subscriptionService.delete(id); // Gestione booleana dell esito dell operazione
        if (!deleted) { // Controllo condizionale
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sottoscrizione non trovata"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
