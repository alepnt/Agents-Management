package com.example.server.controller;

import com.example.server.dto.MailRequest;
import com.example.server.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                         @Valid @RequestBody MailRequest request) {
        String token = extractBearerToken(authorization);
        mailService.sendMail(token, request);
        return ResponseEntity.accepted().build();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Intestazione Authorization mancante o non valida");
        }
        return authorizationHeader.substring("Bearer ".length());
    }
}
