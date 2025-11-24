package com.example.server.service.mapper;

import com.example.common.dto.CustomerDTO;
import com.example.server.domain.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    @ParameterizedTest
    @MethodSource("entityToDtoArguments")
    void shouldConvertEntityToDto(Customer customer) {
        CustomerDTO dto = CustomerMapper.toDto(customer);

        assertThat(dto)
                .extracting(CustomerDTO::getId, CustomerDTO::getName, CustomerDTO::getVatNumber, CustomerDTO::getTaxCode,
                        CustomerDTO::getEmail, CustomerDTO::getPhone, CustomerDTO::getAddress,
                        CustomerDTO::getCreatedAt, CustomerDTO::getUpdatedAt)
                .containsExactly(customer.getId(), customer.getName(), customer.getVatNumber(), customer.getTaxCode(),
                        customer.getEmail(), customer.getPhone(), customer.getAddress(),
                        customer.getCreatedAt(), customer.getUpdatedAt());
    }

    @Test
    void shouldReturnNullDtoWhenEntityIsNull() {
        assertThat(CustomerMapper.toDto(null)).isNull();
    }

    @ParameterizedTest
    @MethodSource("dtoToEntityArguments")
    void shouldConvertDtoToEntity(CustomerDTO dto) {
        Customer entity = CustomerMapper.fromDto(dto);

        assertThat(entity)
                .extracting(Customer::getId, Customer::getName, Customer::getVatNumber, Customer::getTaxCode,
                        Customer::getEmail, Customer::getPhone, Customer::getAddress,
                        Customer::getCreatedAt, Customer::getUpdatedAt)
                .containsExactly(dto.getId(), dto.getName(), dto.getVatNumber(), dto.getTaxCode(),
                        dto.getEmail(), dto.getPhone(), dto.getAddress(), dto.getCreatedAt(), dto.getUpdatedAt());
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        assertThat(CustomerMapper.fromDto(null)).isNull();
    }

    private static Stream<Arguments> entityToDtoArguments() {
        Instant now = Instant.now();
        return Stream.of(
                Arguments.of(new Customer(10L, "Acme", "IT123", "RSSMRA80A01F205X",
                        "info@acme.test", "+39010000000", "Via Roma 1", now.minusSeconds(60), now)),
                Arguments.of(new Customer(null, "", null, null,
                        null, null, null, null, null))
        );
    }

    private static Stream<Arguments> dtoToEntityArguments() {
        Instant created = Instant.parse("2024-03-25T10:15:30Z");
        Instant updated = Instant.parse("2024-03-26T11:22:33Z");
        return Stream.of(
                Arguments.of(new CustomerDTO(20L, "Beta", "IT999", "CNTLCN90L01H501Y",
                        "contatti@beta.test", "+3911223344", "Via Milano 2", created, updated)),
                Arguments.of(new CustomerDTO(null, null, null, null, null, null, null, null, null))
        );
    }
}
