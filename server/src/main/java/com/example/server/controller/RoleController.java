package com.example.server.controller;

import com.example.common.api.RoleApiContract;
import com.example.common.dto.RoleDTO;
import com.example.server.service.RoleService;
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
@RequestMapping("/api/roles")
public class RoleController implements RoleApiContract {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    @GetMapping
    public List<RoleDTO> listRoles() {
        return roleService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<RoleDTO> findById(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @Override
    @PostMapping
    public RoleDTO create(@RequestBody RoleDTO role) {
        return roleService.create(role);
    }

    @Override
    @PutMapping("/{id}")
    public RoleDTO update(@PathVariable Long id, @RequestBody RoleDTO role) {
        return roleService.update(id, role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruolo non trovato"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = roleService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruolo non trovato");
        }
    }
}
