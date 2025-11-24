package com.example.server.service.mapper;

import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Invoice;
import com.example.server.domain.InvoiceLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class InvoiceMapperTest {

    @ParameterizedTest
    @MethodSource("invoiceToDtoArguments")
    void shouldConvertInvoiceToDto(Invoice invoice, List<InvoiceLine> lines, int expectedLines) {
        InvoiceDTO dto = InvoiceMapper.toDto(invoice, lines);

        assertThat(dto)
                .extracting(InvoiceDTO::getId, InvoiceDTO::getNumber, InvoiceDTO::getContractId, InvoiceDTO::getCustomerId,
                        InvoiceDTO::getCustomerName, InvoiceDTO::getAmount, InvoiceDTO::getIssueDate, InvoiceDTO::getDueDate,
                        InvoiceDTO::getStatus, InvoiceDTO::getPaymentDate, InvoiceDTO::getNotes)
                .containsExactly(invoice.getId(), invoice.getNumber(), invoice.getContractId(), invoice.getCustomerId(),
                        invoice.getCustomerName(), invoice.getAmount(), invoice.getIssueDate(), invoice.getDueDate(),
                        invoice.getStatus(), invoice.getPaymentDate(), invoice.getNotes());
        assertThat(dto.getLines()).hasSize(expectedLines);
        if (lines != null && !lines.isEmpty()) {
            assertThat(dto.getLines())
                    .extracting(InvoiceLineDTO::getId, InvoiceLineDTO::getInvoiceId, InvoiceLineDTO::getArticleId,
                            InvoiceLineDTO::getArticleCode, InvoiceLineDTO::getDescription, InvoiceLineDTO::getQuantity,
                            InvoiceLineDTO::getUnitPrice, InvoiceLineDTO::getVatRate, InvoiceLineDTO::getTotal)
                    .containsExactly(lines.stream()
                            .map(line -> tuple(line.getId(), line.getInvoiceId(), line.getArticleId(), line.getArticleCode(),
                                    line.getDescription(), line.getQuantity(), line.getUnitPrice(), line.getVatRate(),
                                    line.getTotal()))
                            .toArray(Object[]::new));
        }
    }

    @Test
    void shouldReturnNullDtoWhenInvoiceIsNull() {
        assertThat(InvoiceMapper.toDto(null, List.of())).isNull();
    }

    @ParameterizedTest
    @MethodSource("dtoToEntityArguments")
    void shouldConvertDtoToEntity(InvoiceDTO dto) {
        Invoice entity = InvoiceMapper.fromDto(dto);

        assertThat(entity)
                .extracting(Invoice::getId, Invoice::getContractId, Invoice::getNumber, Invoice::getCustomerId,
                        Invoice::getCustomerName, Invoice::getAmount, Invoice::getIssueDate, Invoice::getDueDate,
                        Invoice::getStatus, Invoice::getPaymentDate, Invoice::getNotes, Invoice::getCreatedAt, Invoice::getUpdatedAt)
                .containsExactly(dto.getId(), dto.getContractId(), dto.getNumber(), dto.getCustomerId(),
                        dto.getCustomerName(), dto.getAmount(), dto.getIssueDate(), dto.getDueDate(),
                        dto.getStatus(), dto.getPaymentDate(), dto.getNotes(), null, null);
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        assertThat(InvoiceMapper.fromDto(null)).isNull();
    }

    private static Stream<Arguments> invoiceToDtoArguments() {
        Invoice invoiceWithDates = new Invoice(1L, 9L, "2024-INV-001", 3L, "Acme", new BigDecimal("100.50"),
                LocalDate.parse("2024-04-10"), LocalDate.parse("2024-05-10"), InvoiceStatus.SENT,
                LocalDate.parse("2024-04-15"), "note", Instant.parse("2024-04-01T10:00:00Z"),
                Instant.parse("2024-04-02T10:00:00Z"));
        Invoice invoiceWithNulls = new Invoice(2L, null, null, null, null, null, null, null, InvoiceStatus.DRAFT,
                null, null, null, null);
        InvoiceLine line = new InvoiceLine(5L, 1L, 7L, "AR-01", "Test line", BigDecimal.ONE, new BigDecimal("10.00"),
                new BigDecimal("22.00"), new BigDecimal("12.20"));

        return Stream.of(
                Arguments.of(invoiceWithDates, List.of(line), 1),
                Arguments.of(invoiceWithNulls, null, 0),
                Arguments.of(invoiceWithDates, List.of(), 0)
        );
    }

    private static Stream<Arguments> dtoToEntityArguments() {
        InvoiceDTO withPayment = new InvoiceDTO(3L, "2024-INV-002", 10L, 4L, "Beta",
                new BigDecimal("500.00"), LocalDate.parse("2024-03-01"), LocalDate.parse("2024-03-31"),
                InvoiceStatus.PAID, LocalDate.parse("2024-03-15"), "ok", List.of());
        InvoiceDTO withNulls = new InvoiceDTO(null, null, null, null, null, null, null, null,
                InvoiceStatus.CANCELLED, null, null, List.of());

        return Stream.of(
                Arguments.of(withPayment),
                Arguments.of(withNulls)
        );
    }
}
