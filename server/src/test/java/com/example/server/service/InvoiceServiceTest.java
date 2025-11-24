package com.example.server.service;

import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import com.example.server.domain.Invoice;
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
import static org.mockito.ArgumentMatchers.any;
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
                InvoiceStatus.ISSUED, null, null, null, null);

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
}
