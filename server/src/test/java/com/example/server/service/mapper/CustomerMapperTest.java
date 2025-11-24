package com.example.server.service.mapper;

import com.example.common.dto.CustomerDTO;
import com.example.server.domain.Customer;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    @Test
    void shouldConvertEntityToDto() {
        Instant now = Instant.now();
        Customer customer = new Customer(10L, "Acme", "IT123", "RSSMRA80A01F205X",
                "info@acme.test", "+39010000000", "Via Roma 1", now.minusSeconds(60), now);

        CustomerDTO dto = CustomerMapper.toDto(customer);

        assertThat(dto)
                .extracting(CustomerDTO::getId, CustomerDTO::getName, CustomerDTO::getVatNumber, CustomerDTO::getTaxCode,
                        CustomerDTO::getEmail, CustomerDTO::getPhone, CustomerDTO::getAddress,
                        CustomerDTO::getCreatedAt, CustomerDTO::getUpdatedAt)
                .containsExactly(10L, "Acme", "IT123", "RSSMRA80A01F205X",
                        "info@acme.test", "+39010000000", "Via Roma 1", now.minusSeconds(60), now);
    }

    @Test
    void shouldReturnNullDtoWhenEntityIsNull() {
        assertThat(CustomerMapper.toDto(null)).isNull();
    }

    @Test
    void shouldConvertDtoToEntity() {
        Instant created = Instant.parse("2024-03-25T10:15:30Z");
        Instant updated = Instant.parse("2024-03-26T11:22:33Z");
        CustomerDTO dto = new CustomerDTO(20L, "Beta", "IT999", "CNTLCN90L01H501Y",
                "contatti@beta.test", "+3911223344", "Via Milano 2", created, updated);

        Customer entity = CustomerMapper.fromDto(dto);

        assertThat(entity)
                .extracting(Customer::getId, Customer::getName, Customer::getVatNumber, Customer::getTaxCode,
                        Customer::getEmail, Customer::getPhone, Customer::getAddress,
                        Customer::getCreatedAt, Customer::getUpdatedAt)
                .containsExactly(20L, "Beta", "IT999", "CNTLCN90L01H501Y",
                        "contatti@beta.test", "+3911223344", "Via Milano 2", created, updated);
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        assertThat(CustomerMapper.fromDto(null)).isNull();
    }
}
