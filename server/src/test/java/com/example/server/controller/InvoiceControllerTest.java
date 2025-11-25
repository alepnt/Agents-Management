package com.example.server.controller;

import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.server.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @Test
    @DisplayName("Create invoice returns persisted payload")
    void createInvoice() throws Exception {
        InvoiceDTO request = new InvoiceDTO(null, "INV-1", 1L, 2L, "Customer", BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.DRAFT, null, null, List.of());
        InvoiceDTO saved = new InvoiceDTO(5L, "INV-1", 1L, 2L, "Customer", BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.DRAFT, null, null, List.of());
        when(invoiceService.create(any(InvoiceDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/invoices")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.number").value("INV-1"));
    }

    @Test
    @DisplayName("Update invoice returns 404 when service does not find entity")
    void updateInvoiceNotFound() throws Exception {
        when(invoiceService.update(eq(9L), any(InvoiceDTO.class))).thenReturn(Optional.empty());

        InvoiceDTO request = new InvoiceDTO(null, "INV-9", 1L, 2L, "Customer", BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.DRAFT, null, null, List.of());

        mockMvc.perform(put("/api/invoices/9")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Register payment propagates not found errors")
    void registerPaymentNotFound() throws Exception {
        when(invoiceService.registerPayment(eq(3L), any(InvoicePaymentRequest.class))).thenReturn(Optional.empty());

        InvoicePaymentRequest paymentRequest = new InvoicePaymentRequest(LocalDate.now(), BigDecimal.ONE);

        mockMvc.perform(post("/api/invoices/3/payments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Register payment returns updated invoice")
    void registerPayment() throws Exception {
        InvoiceDTO updated = new InvoiceDTO(3L, "INV-3", 1L, 2L, "Customer", BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusDays(30), InvoiceStatus.PAID, LocalDate.now(), null, List.of());
        when(invoiceService.registerPayment(eq(3L), any(InvoicePaymentRequest.class))).thenReturn(Optional.of(updated));

        InvoicePaymentRequest paymentRequest = new InvoicePaymentRequest(LocalDate.now(), BigDecimal.ONE);

        mockMvc.perform(post("/api/invoices/3/payments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("History endpoint returns list of events")
    void history() throws Exception {
        List<DocumentHistoryDTO> history = List.of(
                new DocumentHistoryDTO(1L, DocumentType.INVOICE, 10L, DocumentAction.CREATED, "Created", Instant.EPOCH)
        );
        when(invoiceService.history(10L)).thenReturn(history);

        mockMvc.perform(get("/api/invoices/10/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentType").value("INVOICE"))
                .andExpect(jsonPath("$[0].action").value("CREATED"));
    }

    @Test
    @DisplayName("Controller maps service exceptions to HTTP status codes")
    void serviceErrorPropagation() throws Exception {
        when(invoiceService.findById(1L)).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Boom"));

        mockMvc.perform(get("/api/invoices/1"))
                .andExpect(status().isInternalServerError());
    }
}
