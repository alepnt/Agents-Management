package com.example.server.controller;

import com.example.common.api.AgentApiContract;
import com.example.common.dto.AgentDTO;
import com.example.server.service.AgentService;
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
@RequestMapping("/api/agents")
public class AgentController implements AgentApiContract {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Override
    @GetMapping
    public List<AgentDTO> listAgents() {
        return agentService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<AgentDTO> findById(@PathVariable Long id) {
        return agentService.findById(id);
    }

    @Override
    @PostMapping
    public AgentDTO create(@RequestBody AgentDTO agent) {
        return agentService.create(agent);
    }

    @Override
    @PutMapping("/{id}")
    public AgentDTO update(@PathVariable Long id, @RequestBody AgentDTO agent) {
        return agentService.update(id, agent)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agente non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = agentService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agente non trovato");
        }
    }
}
