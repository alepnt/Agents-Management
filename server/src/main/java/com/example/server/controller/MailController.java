package com.example.server.controller; // Package del controller

import com.example.common.api.MailApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.MailRequest; // Import delle dipendenze necessarie
import com.example.server.service.MailService; // Import delle dipendenze necessarie
import jakarta.validation.Valid; // Import delle dipendenze necessarie
import org.springframework.http.HttpHeaders; // Import delle dipendenze necessarie
import org.springframework.http.ResponseEntity; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestHeader; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/mail") // Imposta il percorso base degli endpoint
public class MailController implements MailApiContract { // Dichiarazione della classe controller

    private final MailService mailService; // Definizione di una dipendenza iniettata

    public MailController(MailService mailService) { // Inizio di un metodo esposto dal controller
        this.mailService = mailService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping("/send") // Mapping per una richiesta POST
    public ResponseEntity<Void> sendMail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, // Firma di un metodo del controller
                                         @Valid @RequestBody MailRequest request) { // Abilita la validazione del parametro
        String token = extractBearerToken(authorization); // Istruzione di gestione del controller
        mailService.sendMail(token, request); // Istruzione di gestione del controller
        return ResponseEntity.accepted().build(); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    private String extractBearerToken(String authorizationHeader) { // Istruzione di gestione del controller
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) { // Controllo condizionale
            throw new IllegalArgumentException("Intestazione Authorization mancante o non valida"); // Genera un eccezione HTTP
        } // Istruzione di gestione del controller
        return authorizationHeader.substring("Bearer ".length()); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
