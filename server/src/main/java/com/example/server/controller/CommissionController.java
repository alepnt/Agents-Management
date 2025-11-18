package com.example.server.controller;

import com.example.common.api.CommissionApiContract;
import com.example.common.dto.CommissionDTO;
import com.example.server.service.CommissionService;
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
@RequestMapping("/api/commissions")
public class CommissionController implements CommissionApiContract {

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @Override
    @GetMapping
    public List<CommissionDTO> listCommissions() {
        return commissionService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<CommissionDTO> findById(@PathVariable Long id) {
        return commissionService.findById(id);
    }

    @Override
    @PostMapping
    public CommissionDTO create(@RequestBody CommissionDTO commissionDTO) {
        return commissionService.create(commissionDTO);
    }

    @Override
    @PutMapping("/{id}")
    public CommissionDTO update(@PathVariable Long id, @RequestBody CommissionDTO commissionDTO) {
        return commissionService.update(id, commissionDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commissione non trovata"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = commissionService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commissione non trovata");
        }
    }
}
