package com.example.server.controller; // Package del controller

import com.example.common.api.NotificationApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.NotificationDTO; // Import delle dipendenze necessarie
import com.example.common.dto.NotificationSubscriptionDTO; // Import delle dipendenze necessarie
import com.example.server.dto.NotificationSubscribeRequest; // Import delle dipendenze necessarie
import com.example.server.service.NotificationService; // Import delle dipendenze necessarie
import jakarta.validation.Valid; // Import delle dipendenze necessarie
import org.springframework.format.annotation.DateTimeFormat; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.DeleteMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PathVariable; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PutMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie
import org.springframework.web.context.request.async.DeferredResult; // Import delle dipendenze necessarie
import org.springframework.http.HttpStatus; // Import delle dipendenze necessarie
import org.springframework.web.server.ResponseStatusException; // Import delle dipendenze necessarie

import java.time.Instant; // Import delle dipendenze necessarie
import java.util.List; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/notifications") // Imposta il percorso base degli endpoint
public class NotificationController implements NotificationApiContract { // Dichiarazione della classe controller

    private final NotificationService notificationService; // Definizione di una dipendenza iniettata

    public NotificationController(NotificationService notificationService) { // Inizio di un metodo esposto dal controller
        this.notificationService = notificationService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping // Mapping per una richiesta GET
    public List<NotificationDTO> listNotifications(@RequestParam("userId") Long userId, // Firma di un metodo del controller
                                                   @RequestParam(value = "since", required = false) // Istruzione di gestione del controller
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Istruzione di gestione del controller
                                                   Instant since) { // Istruzione di gestione del controller
        return notificationService.findNotifications(userId, since); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @GetMapping("/subscribe") // Mapping per una richiesta GET
    public DeferredResult<List<NotificationDTO>> subscribe(@RequestParam("userId") Long userId) { // Inizio di un metodo esposto dal controller
        DeferredResult<List<NotificationDTO>> deferredResult = new DeferredResult<>(30_000L); // Istruzione di gestione del controller
        notificationService.registerSubscriber(userId, deferredResult); // Istruzione di gestione del controller
        return deferredResult; // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping // Mapping per una richiesta POST
    public NotificationDTO create(@Valid @RequestBody NotificationDTO request) { // Inizio di un metodo esposto dal controller
        return notificationService.createNotification(request); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PutMapping("/{id}") // Mapping per una richiesta PUT
    public NotificationDTO update(@PathVariable Long id, @Valid @RequestBody NotificationDTO request) { // Inizio di un metodo esposto dal controller
        return notificationService.updateNotification(id, request) // Restituisce il risultato dell operazione
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifica non trovata")); // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @DeleteMapping("/{id}") // Mapping per una richiesta DELETE
    public void delete(@PathVariable Long id) { // Inizio di un metodo esposto dal controller
        try { // Istruzione di gestione del controller
            notificationService.deleteNotification(id); // Istruzione di gestione del controller
        } catch (IllegalArgumentException ex) { // Istruzione di gestione del controller
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
    } // Istruzione di gestione del controller

    @PostMapping("/subscribe") // Mapping per una richiesta POST
    public NotificationSubscriptionDTO registerChannel(@Valid @RequestBody NotificationSubscribeRequest request) { // Inizio di un metodo esposto dal controller
        return notificationService.subscribe(request); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
