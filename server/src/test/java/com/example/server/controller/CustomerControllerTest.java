package com.example.server.controller;

import com.example.common.dto.CustomerDTO;
import com.example.server.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List customers returns payload from service")
    void listCustomers() throws Exception {
        CustomerDTO alpha = new CustomerDTO(1L, "Alpha", "VAT1", "TC1", "a@example.com", "123", "Street 1", Instant.EPOCH, Instant.EPOCH);
        CustomerDTO beta = new CustomerDTO(2L, "Beta", "VAT2", "TC2", "b@example.com", "321", "Street 2", Instant.EPOCH, null);
        when(customerService.findAll()).thenReturn(List.of(alpha, beta));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[1].vatNumber").value("VAT2"));
    }

    @Test
    @DisplayName("Find customer by id delegates to service")
    void findById() throws Exception {
        CustomerDTO dto = new CustomerDTO(9L, "Delta", "VAT9", "TC9", "d@example.com", "987", "Street 9", Instant.EPOCH, null);
        when(customerService.findById(9L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/customers/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Delta"));
    }

    @Test
    @DisplayName("Create customer returns created DTO")
    void createCustomer() throws Exception {
        CustomerDTO request = new CustomerDTO(null, "New", "VATN", "TCN", "n@example.com", "000", "Somewhere", Instant.EPOCH, null);
        CustomerDTO saved = new CustomerDTO(15L, "New", "VATN", "TCN", "n@example.com", "000", "Somewhere", Instant.EPOCH, null);
        when(customerService.create(any(CustomerDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15));
    }

    @Test
    @DisplayName("Update returns payload when present")
    void updateCustomer() throws Exception {
        CustomerDTO update = new CustomerDTO(null, "Updated", "VATU", "TCU", "u@example.com", "999", "Updated street", Instant.EPOCH, null);
        CustomerDTO saved = new CustomerDTO(3L, "Updated", "VATU", "TCU", "u@example.com", "999", "Updated street", Instant.EPOCH, null);
        when(customerService.update(eq(3L), any(CustomerDTO.class))).thenReturn(Optional.of(saved));

        mockMvc.perform(put("/api/customers/3")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("Update returns 404 when service reports missing entity")
    void updateCustomerNotFound() throws Exception {
        when(customerService.update(eq(77L), any(CustomerDTO.class))).thenReturn(Optional.empty());

        CustomerDTO update = new CustomerDTO(null, "Missing", "VATX", "TCX", "x@example.com", "111", "Nowhere", Instant.EPOCH, null);

        mockMvc.perform(put("/api/customers/77")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete maps missing entity to 404")
    void deleteCustomerNotFound() throws Exception {
        when(customerService.delete(101L)).thenReturn(false);

        mockMvc.perform(delete("/api/customers/101"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Service exceptions surface as HTTP errors")
    void createCustomerValidationError() throws Exception {
        when(customerService.create(any(CustomerDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate VAT"));

        CustomerDTO request = new CustomerDTO(null, "New", "VATN", "TCN", "n@example.com", "000", "Somewhere", Instant.EPOCH, null);

        mockMvc.perform(post("/api/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
