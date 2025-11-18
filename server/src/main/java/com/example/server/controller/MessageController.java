package com.example.server.controller;

import com.example.common.api.MessageApiContract;
import com.example.common.dto.MessageDTO;
import com.example.server.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApiContract {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    @GetMapping
    public List<MessageDTO> listMessages() {
        return messageService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<MessageDTO> findById(@PathVariable Long id) {
        return messageService.findById(id);
    }

    @Override
    @PostMapping
    public MessageDTO create(@RequestBody MessageDTO message) {
        return messageService.create(message);
    }

    @Override
    @PutMapping("/{id}")
    public MessageDTO update(@PathVariable Long id, @RequestBody MessageDTO message) {
        return messageService.update(id, message)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Messaggio non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = messageService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Messaggio non trovato");
        }
    }
}
