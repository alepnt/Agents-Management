package com.example.server.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void shouldCreateNewCustomerWithNullIdAndTimestamps() {
        Customer customer = Customer.create("Gamma", "IT456", "VRDLNZ85C10H501F",
                "gamma@test.it", "+3955512345", "Via Napoli 10");

        assertThat(customer.getId()).isNull();
        assertThat(customer.getCreatedAt()).isNull();
        assertThat(customer.getUpdatedAt()).isNull();
        assertThat(customer.getName()).isEqualTo("Gamma");
        assertThat(customer.getVatNumber()).isEqualTo("IT456");
        assertThat(customer.getTaxCode()).isEqualTo("VRDLNZ85C10H501F");
        assertThat(customer.getEmail()).isEqualTo("gamma@test.it");
        assertThat(customer.getPhone()).isEqualTo("+3955512345");
        assertThat(customer.getAddress()).isEqualTo("Via Napoli 10");
    }

    @Test
    void withIdShouldReturnCopyWithProvidedIdentifier() {
        Instant now = Instant.now();
        Customer original = new Customer(null, "Sigma", "IT000", "PRVBNN70A01C351E",
                "sigma@test.it", "+3900000000", "Via Firenze 3", now.minusSeconds(120), now);

        Customer withId = original.withId(42L);

        assertThat(withId.getId()).isEqualTo(42L);
        assertThat(withId.getName()).isEqualTo(original.getName());
        assertThat(withId.getVatNumber()).isEqualTo(original.getVatNumber());
        assertThat(withId.getTaxCode()).isEqualTo(original.getTaxCode());
        assertThat(withId.getEmail()).isEqualTo(original.getEmail());
        assertThat(withId.getPhone()).isEqualTo(original.getPhone());
        assertThat(withId.getAddress()).isEqualTo(original.getAddress());
        assertThat(withId.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(withId.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
    }

    @Test
    void updateFromShouldCopyUpdatableFields() {
        Instant createdAt = Instant.parse("2024-03-01T09:00:00Z");
        Instant updatedAt = Instant.parse("2024-03-05T10:15:30Z");
        Customer persisted = new Customer(7L, "Omega", "IT111", "CNTLST81C10H501F",
                "omega@test.it", "+398888888", "Via Torino 7", createdAt, updatedAt);

        Customer incoming = Customer.create("Nuovo Nome", "IT222", "CNTLST81C10H501F",
                "new@test.it", "+397777777", "Via Torino 9");

        Customer updated = persisted.updateFrom(incoming);

        assertThat(updated.getId()).isEqualTo(persisted.getId());
        assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updated.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(updated.getName()).isEqualTo("Nuovo Nome");
        assertThat(updated.getVatNumber()).isEqualTo("IT222");
        assertThat(updated.getTaxCode()).isEqualTo("CNTLST81C10H501F");
        assertThat(updated.getEmail()).isEqualTo("new@test.it");
        assertThat(updated.getPhone()).isEqualTo("+397777777");
        assertThat(updated.getAddress()).isEqualTo("Via Torino 9");
    }

    @Test
    void equalityShouldDependOnId() {
        Customer first = Customer.create("Alpha", "VAT", "TAX", "a@test", "000", "Addr").withId(1L);
        Customer second = Customer.create("Beta", "VAT2", "TAX2", "b@test", "111", "Addr2").withId(1L);
        Customer third = Customer.create("Gamma", "VAT3", "TAX3", "c@test", "222", "Addr3").withId(2L);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(third);
    }
}
