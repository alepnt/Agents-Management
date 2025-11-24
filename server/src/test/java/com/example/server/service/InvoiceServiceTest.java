package com.example.server.service;

import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Invoice;
import com.example.server.domain.InvoiceLine;
import com.example.server.domain.Article;
import com.example.server.repository.InvoiceLineRepository;
import com.example.server.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceLineRepository invoiceLineRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ArticleService articleService;

    @Mock
    private DocumentHistoryService documentHistoryService;

    @Mock
    private CommissionService commissionService;

    @Mock
    private StatisticsService statisticsService;

    private InvoiceService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceService(invoiceRepository, invoiceLineRepository, customerService, articleService,
                documentHistoryService, commissionService, statisticsService);
    }

    @Test
    void shouldDefaultMissingPaymentData() {
        Long invoiceId = 5L;
        Invoice existing = new Invoice(invoiceId, 10L, "INV-5", 1L, "Cliente",
                new BigDecimal("120.50"), LocalDate.parse("2024-01-10"), LocalDate.parse("2024-02-10"),
                InvoiceStatus.SENT, null, null, null, null);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(existing));
        when(invoiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(invoiceLineRepository.findByInvoiceIdOrderById(invoiceId)).thenReturn(List.of());

        InvoicePaymentRequest request = new InvoicePaymentRequest(null, null);
        Optional<InvoiceDTO> result = service.registerPayment(invoiceId, request);

        assertThat(result).isPresent();
        InvoiceDTO paid = result.orElseThrow();
        assertThat(paid.getPaymentDate()).isEqualTo(LocalDate.now());
        assertThat(paid.getAmount()).isEqualTo(existing.getAmount());

        verify(documentHistoryService).log(eq(DocumentType.INVOICE), eq(invoiceId),
                eq(DocumentAction.PAYMENT_REGISTERED),
                eq("Pagamento registrato il " + paid.getPaymentDate()));
        verify(commissionService).updateAfterPayment(eq(existing.getContractId()), eq(existing.getAmount()),
                eq(existing.getAmount()));
    }

    @Test
    void shouldCreateInvoiceWithGeneratedNumberAndCalculatedLines() {
        InvoiceDTO request = new InvoiceDTO(null, null, 9L, 3L, null,
                null, LocalDate.parse("2024-05-01"), LocalDate.parse("2024-06-01"),
                InvoiceStatus.DRAFT, null, "Note", List.of(
                new InvoiceLineDTO(null, null, 7L, null, "   descrizione   ", new BigDecimal("2"), null,
                        new BigDecimal("0.22"), null),
                new InvoiceLineDTO(null, null, null, "ART-1", null, null, new BigDecimal("10"), null,
                        new BigDecimal("5.5"))
        ));

        when(customerService.require(3L)).thenReturn(new com.example.server.domain.Customer(3L, "Cliente", null, null,
                null, null, null, null, null));
        when(articleService.require(7L)).thenReturn(new Article(7L, "ART-7", "Articolo", "Descrizione",
                new BigDecimal("12.50"), new BigDecimal("0.22"), "pz", null, null));
        when(invoiceRepository.save(any())).thenAnswer(invocation -> {
            Invoice saved = invocation.getArgument(0);
            return saved.withId(11L);
        });
        when(invoiceLineRepository.findByInvoiceIdOrderById(11L)).thenReturn(List.of(
                new InvoiceLine(1L, 11L, 7L, "ART-7", "descrizione", new BigDecimal("2"),
                        new BigDecimal("12.50"), new BigDecimal("0.22"), new BigDecimal("30.50"))
        ));

        InvoiceDTO created = service.create(request);

        assertThat(created.getId()).isEqualTo(11L);
        assertThat(created.getNumber()).startsWith("INV-");
        assertThat(created.getAmount()).isEqualByComparingTo(new BigDecimal("40.50"));
        verify(invoiceLineRepository).deleteByInvoiceId(11L);
        verify(invoiceLineRepository).saveAll(anyList());
        verify(documentHistoryService).log(eq(DocumentType.INVOICE), eq(11L), eq(DocumentAction.CREATED), any());
        verify(statisticsService).clearCache();
    }

    @Test
    void shouldUpdateInvoiceAndLogStatusChange() {
        Invoice existing = new Invoice(4L, 8L, "INV-4", 2L, "Old customer", new BigDecimal("100"),
                LocalDate.parse("2024-01-01"), LocalDate.parse("2024-02-01"), InvoiceStatus.DRAFT, null, null, null, null);
        InvoiceDTO update = new InvoiceDTO(null, "NEW", 8L, 5L, null, new BigDecimal("150"),
                LocalDate.parse("2024-03-01"), LocalDate.parse("2024-04-01"), InvoiceStatus.SENT,
                null, "Updated", List.of());

        when(invoiceRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(invoiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(invoiceLineRepository.findByInvoiceIdOrderById(4L)).thenReturn(List.of());
        when(customerService.require(5L)).thenReturn(new com.example.server.domain.Customer(5L, "Customer", null, null,
                null, null, null, null, null));

        Optional<InvoiceDTO> updated = service.update(4L, update);

        assertThat(updated).isPresent();
        InvoiceDTO dto = updated.orElseThrow();
        assertThat(dto.getStatus()).isEqualTo(InvoiceStatus.SENT);
        verify(documentHistoryService).log(DocumentType.INVOICE, 4L, DocumentAction.UPDATED, "Fattura aggiornata");
        verify(documentHistoryService).log(DocumentType.INVOICE, 4L, DocumentAction.STATUS_CHANGED,
                "Stato cambiato da DRAFT a SENT");
        verify(statisticsService).clearCache();
    }

    @Test
    void shouldDeleteInvoiceWhenPresent() {
        Invoice existing = new Invoice(13L, 1L, "INV-13", 1L, "Cliente", BigDecimal.ONE,
                LocalDate.now(), LocalDate.now(), InvoiceStatus.DRAFT, null, null, null, null);
        when(invoiceRepository.findById(13L)).thenReturn(Optional.of(existing));

        boolean deleted = service.delete(13L);

        assertThat(deleted).isTrue();
        verify(invoiceRepository).deleteById(13L);
        verify(invoiceLineRepository).deleteByInvoiceId(13L);
        verify(documentHistoryService).log(DocumentType.INVOICE, 13L, DocumentAction.DELETED, "Fattura eliminata");
        verify(statisticsService).clearCache();
    }

    @Test
    void shouldReturnFalseWhenInvoiceToDeleteMissing() {
        when(invoiceRepository.findById(77L)).thenReturn(Optional.empty());

        boolean deleted = service.delete(77L);

        assertThat(deleted).isFalse();
    }

    @Test
    void shouldRejectInvalidInvoiceLines() {
        InvoiceLineDTO invalidQuantity = new InvoiceLineDTO(null, null, null, null, null, BigDecimal.ZERO, BigDecimal.ONE,
                BigDecimal.ZERO, null);
        InvoiceDTO dto = new InvoiceDTO(null, null, 1L, 1L, null, null, LocalDate.now(), LocalDate.now(),
                InvoiceStatus.DRAFT, null, null, List.of(invalidQuantity));

        when(customerService.require(1L)).thenReturn(new com.example.server.domain.Customer(1L, "Customer", null, null,
                null, null, null, null, null));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La quantità deve essere maggiore di zero");

        InvoiceLineDTO negativePrice = new InvoiceLineDTO(null, null, null, null, null, BigDecimal.ONE, new BigDecimal("-1"),
                null, null);
        dto.setLines(List.of(negativePrice));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Il prezzo unitario non può essere negativo");
    }
}
