package com.example.common.api;

import com.example.common.dto.MailRequest;
import org.springframework.http.ResponseEntity;

/**
 * Contratto API per l'invio di email tramite Microsoft Graph.
 */
public interface MailApiContract {

    ResponseEntity<Void> sendMail(String authorization, MailRequest request);
}
