package com.example.server.controller; // Package del controller

import com.example.common.api.ChatApiContract; // Import delle dipendenze necessarie
import com.example.common.dto.ChatConversationDTO; // Import delle dipendenze necessarie
import com.example.common.dto.ChatMessageDTO; // Import delle dipendenze necessarie
import com.example.common.dto.ChatMessageRequest; // Import delle dipendenze necessarie
import com.example.server.service.ChatService; // Import delle dipendenze necessarie
import jakarta.validation.Valid; // Import delle dipendenze necessarie
import org.springframework.format.annotation.DateTimeFormat; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.GetMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.PostMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestBody; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestMapping; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RequestParam; // Import delle dipendenze necessarie
import org.springframework.web.bind.annotation.RestController; // Import delle dipendenze necessarie
import org.springframework.web.context.request.async.DeferredResult; // Import delle dipendenze necessarie

import java.time.Instant; // Import delle dipendenze necessarie
import java.util.List; // Import delle dipendenze necessarie

@RestController // Contrassegna la classe come controller REST
@RequestMapping("/api/chat") // Imposta il percorso base degli endpoint
public class ChatController implements ChatApiContract { // Dichiarazione della classe controller

    private final ChatService chatService; // Definizione di una dipendenza iniettata

    public ChatController(ChatService chatService) { // Inizio di un metodo esposto dal controller
        this.chatService = chatService; // Inizializza il campo del controller
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/conversations") // Mapping per una richiesta GET
    public List<ChatConversationDTO> conversations(@RequestParam("userId") Long userId) { // Inizio di un metodo esposto dal controller
        return chatService.listConversations(userId); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/messages") // Mapping per una richiesta GET
    public List<ChatMessageDTO> messages(@RequestParam("userId") Long userId, // Firma di un metodo del controller
                                         @RequestParam("conversationId") String conversationId, // Istruzione di gestione del controller
                                         @RequestParam(value = "since", required = false) // Istruzione di gestione del controller
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Istruzione di gestione del controller
                                         Instant since) { // Istruzione di gestione del controller
        return chatService.getMessages(userId, conversationId, since); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @GetMapping("/poll") // Mapping per una richiesta GET
    public DeferredResult<List<ChatMessageDTO>> poll(@RequestParam("userId") Long userId, // Firma di un metodo del controller
                                                    @RequestParam("conversationId") String conversationId) { // Istruzione di gestione del controller
        DeferredResult<List<ChatMessageDTO>> deferredResult = new DeferredResult<>(30_000L); // Istruzione di gestione del controller
        chatService.registerConversationListener(userId, conversationId, deferredResult); // Istruzione di gestione del controller
        return deferredResult; // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller

    @Override // Sovrascrive un metodo dell interfaccia
    @PostMapping("/messages") // Mapping per una richiesta POST
    public ChatMessageDTO send(@Valid @RequestBody ChatMessageRequest request) { // Inizio di un metodo esposto dal controller
        return chatService.sendMessage(request); // Restituisce il risultato dell operazione
    } // Istruzione di gestione del controller
} // Istruzione di gestione del controller
