package com.example.server.controller;

import com.example.common.api.TeamApiContract;
import com.example.common.dto.TeamDTO;
import com.example.server.service.TeamService;
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
@RequestMapping("/api/teams")
public class TeamController implements TeamApiContract {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    @GetMapping
    public List<TeamDTO> listTeams() {
        return teamService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<TeamDTO> findById(@PathVariable Long id) {
        return teamService.findById(id);
    }

    @Override
    @PostMapping
    public TeamDTO create(@RequestBody TeamDTO team) {
        return teamService.create(team);
    }

    @Override
    @PutMapping("/{id}")
    public TeamDTO update(@PathVariable Long id, @RequestBody TeamDTO team) {
        return teamService.update(id, team)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = teamService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team non trovato");
        }
    }
}
