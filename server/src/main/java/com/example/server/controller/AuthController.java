package com.example.server.controller; // Package del controller

import com.example.server.dto.AuthResponse; // Import delle dipendenze necessarie
import com.example.server.dto.LoginRequest; // Import delle dipendenze necessarie
import com.example.server.dto.RegisterRequest; // Import delle dipendenze necessarie
import com.example.server.dto.UserSummary; // Import delle dipendenze necessarie
import com.example.server.service.UserService; // Import delle dipendenze necessarie
import jakarta.validation.Valid; // Import delle dipendenze necessarie
import org.springframework.http.HttpStatus; // Import delle dipendenze necessarie
import org.springframework.http.ResponseEntity; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/auth") // Imposta il percorso base degli endpoint
public class AuthController { // Dichiarazione della classe controller

    private final UserService userService; // Definizione di una dipendenza iniettata

    public AuthController(UserService userService) { // Inizio di un metodo esposto dal controller
        this.userService = userService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @PostMapping("/login") // Mapping per una richiesta POST
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) { // Inizio di un metodo esposto dal controller
        return ResponseEntity.ok(userService.loginWithMicrosoft(request)); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @PostMapping("/register") // Mapping per una richiesta POST
    public ResponseEntity<UserSummary> register(@Valid @RequestBody RegisterRequest request) { // Inizio di un metodo esposto dal controller
        UserSummary summary = userService.register(request); // Gestione di un DTO di risposta
        return ResponseEntity.status(HttpStatus.CREATED).body(summary); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
