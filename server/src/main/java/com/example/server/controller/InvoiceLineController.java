package com.example.server.controller;

import com.example.common.api.InvoiceLineApiContract;
import com.example.common.dto.InvoiceLineDTO;
import com.example.server.service.InvoiceLineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice-lines")
public class InvoiceLineController implements InvoiceLineApiContract {

    private final InvoiceLineService invoiceLineService;

    public InvoiceLineController(InvoiceLineService invoiceLineService) {
        this.invoiceLineService = invoiceLineService;
    }

    @Override
    @GetMapping
    public List<InvoiceLineDTO> listInvoiceLines(@RequestParam(value = "invoiceId", required = false) Long invoiceId) {
        if (invoiceId != null) {
            return invoiceLineService.findByInvoiceId(invoiceId);
        }
        return invoiceLineService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    public Optional<InvoiceLineDTO> findById(@PathVariable Long id) {
        return invoiceLineService.findById(id);
    }

    @Override
    @PostMapping
    public InvoiceLineDTO create(@RequestBody InvoiceLineDTO invoiceLine) {
        return invoiceLineService.create(invoiceLine);
    }

    @Override
    @PutMapping("/{id}")
    public InvoiceLineDTO update(@PathVariable Long id, @RequestBody InvoiceLineDTO invoiceLine) {
        return invoiceLineService.update(id, invoiceLine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Riga fattura non trovata"));
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        boolean deleted = invoiceLineService.delete(id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Riga fattura non trovata");
        }
    }
}
