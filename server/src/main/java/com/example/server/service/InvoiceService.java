package com.example.server.service; // Package declaration for the server service layer

import com.example.common.dto.DocumentHistoryDTO; // DTO representing a document history entry
import com.example.common.dto.InvoiceDTO; // DTO representing an invoice
import com.example.common.dto.InvoiceLineDTO; // DTO representing a single invoice line
import com.example.common.dto.InvoicePaymentRequest; // DTO for payment registration requests
import com.example.common.enums.DocumentAction; // Enum for document actions
import com.example.common.enums.DocumentType; // Enum for document types
import com.example.common.enums.InvoiceStatus; // Enum for invoice statuses
import com.example.server.domain.Invoice; // Domain entity for invoices
import com.example.server.domain.InvoiceLine; // Domain entity for invoice lines
import com.example.server.repository.InvoiceLineRepository; // Repository for invoice lines
import com.example.server.repository.InvoiceRepository; // Repository for invoices
import com.example.server.service.mapper.DocumentHistoryMapper; // Mapper for document history conversions
import com.example.server.service.mapper.InvoiceMapper; // Mapper for invoice conversions
import org.springframework.stereotype.Service; // Spring stereotype indicating a service component
import org.springframework.transaction.annotation.Transactional; // Annotation to manage transactions
import org.springframework.util.StringUtils; // Utility class for string handling

import java.math.BigDecimal; // BigDecimal for monetary values
import java.time.LocalDate; // LocalDate for date handling
import java.util.ArrayList; // ArrayList implementation
import java.util.List; // List interface
import java.util.Objects; // Utility for null checks
import java.util.Optional; // Optional wrapper type

@Service // Marks the class as a Spring service bean
public class InvoiceService { // Service handling invoice operations

    private final InvoiceRepository invoiceRepository; // Repository dependency for invoices
    private final InvoiceLineRepository invoiceLineRepository; // Repository dependency for invoice lines
    private final CustomerService customerService; // Service to manage customers
    private final ArticleService articleService; // Service to manage articles
    private final DocumentHistoryService documentHistoryService; // Service to log document history
    private final CommissionService commissionService; // Service to manage commissions
    private final StatisticsService statisticsService; // Service to manage cached statistics

    public InvoiceService(InvoiceRepository invoiceRepository, // Constructor injecting invoice repository
                          InvoiceLineRepository invoiceLineRepository, // Constructor injecting invoice line repository
                          CustomerService customerService, // Constructor injecting customer service
                          ArticleService articleService, // Constructor injecting article service
                          DocumentHistoryService documentHistoryService, // Constructor injecting document history service
                          CommissionService commissionService, // Constructor injecting commission service
                          StatisticsService statisticsService) { // Constructor injecting statistics service
        this.invoiceRepository = invoiceRepository; // Assign invoice repository
        this.invoiceLineRepository = invoiceLineRepository; // Assign invoice line repository
        this.customerService = customerService; // Assign customer service
        this.articleService = articleService; // Assign article service
        this.documentHistoryService = documentHistoryService; // Assign document history service
        this.commissionService = commissionService; // Assign commission service
        this.statisticsService = statisticsService; // Assign statistics service
    } // End constructor

    public List<InvoiceDTO> findAll() { // Retrieve all invoices
        return invoiceRepository.findAllByOrderByIssueDateDesc().stream() // Fetch all invoices sorted by issue date
                .map(invoice -> { // Map each invoice to a DTO
                    Long invoiceId = Objects.requireNonNull(invoice.getId(), "invoice id must not be null"); // Ensure invoice id is present
                    return InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoiceId)); // Map invoice with its lines
                }) // Close mapping function
                .toList(); // Collect to immutable list
    } // End findAll

    public Optional<InvoiceDTO> findById(Long id) { // Retrieve a single invoice by id
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Find invoice or empty
                .map(invoice -> { // Map found invoice to DTO
                    Long invoiceId = Objects.requireNonNull(invoice.getId(), "invoice id must not be null"); // Validate id
                    return InvoiceMapper.toDto(invoice, invoiceLineRepository.findByInvoiceIdOrderById(invoiceId)); // Convert to DTO with lines
                }); // Return optional DTO
    } // End findById

    @Transactional // Execute method within a transaction
    public InvoiceDTO create(InvoiceDTO dto) { // Create a new invoice
        InvoiceDTO requiredDto = Objects.requireNonNull(dto, "invoice must not be null"); // Validate incoming DTO
        Invoice source = Objects.requireNonNull(InvoiceMapper.fromDto(requiredDto), "mapped invoice must not be null"); // Map DTO to entity
        Long customerId = Objects.requireNonNull(source.getCustomerId(), "customerId must not be null"); // Extract and validate customer id
        String customerName = customerService.require(customerId).getName(); // Retrieve customer name
        List<InvoiceLine> lines = prepareInvoiceLines(requiredDto.getLines()); // Build invoice lines from DTO
        BigDecimal amount = determineAmount(source.getAmount(), lines); // Calculate invoice amount
        String number = StringUtils.hasText(source.getNumber()) ? source.getNumber() : generateNumber(); // Determine invoice number
        Invoice invoice = new Invoice( // Create invoice entity
                null, // id
                source.getContractId(), // contract reference
                number, // invoice number
                customerId, // customer id
                customerName, // customer name
                amount, // total amount
                source.getIssueDate(), // issue date
                source.getDueDate(), // due date
                source.getStatus() != null ? source.getStatus() : InvoiceStatus.DRAFT, // invoice status
                source.getPaymentDate(), // payment date
                source.getNotes(), // notes
                null, // created at
                null // updated at
        ); // End invoice construction
        Invoice saved = Objects.requireNonNull(invoiceRepository.save(invoice), "saved invoice must not be null"); // Persist invoice
        Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null"); // Capture generated id
        replaceInvoiceLines(savedId, lines); // Persist invoice lines
        documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.CREATED, // Log creation history
                "Fattura creata: " + saved.getNumber()); // Message for history
        statisticsService.clearCache(); // Invalidate cached statistics
        return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId)); // Return saved invoice DTO
    } // End create

    @Transactional // Execute within transaction
    public Optional<InvoiceDTO> update(Long id, InvoiceDTO dto) { // Update an existing invoice
        InvoiceDTO requiredDto = Objects.requireNonNull(dto, "invoice must not be null"); // Validate DTO
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Find invoice by id
                .map(existing -> { // Map found invoice to updated version
                    Invoice updatedSource = Objects.requireNonNull(InvoiceMapper.fromDto(requiredDto), // Map DTO to entity
                            "mapped invoice must not be null"); // Validate mapping result
                    Long customerId = Objects.requireNonNull(updatedSource.getCustomerId(), "customerId must not be null"); // Validate customer id
                    String customerName = customerService.require(customerId).getName(); // Fetch customer name
                    List<InvoiceLine> lines = prepareInvoiceLines(requiredDto.getLines()); // Prepare updated lines
                    BigDecimal amount = determineAmount(updatedSource.getAmount(), lines); // Recalculate amount
                    Invoice updated = new Invoice( // Build updated invoice entity
                            existing.getId(), // preserve id
                            updatedSource.getContractId(), // updated contract
                            StringUtils.hasText(updatedSource.getNumber()) ? updatedSource.getNumber() : existing.getNumber(), // invoice number
                            customerId, // customer id
                            customerName, // customer name
                            amount, // amount
                            updatedSource.getIssueDate(), // issue date
                            updatedSource.getDueDate(), // due date
                            updatedSource.getStatus() != null ? updatedSource.getStatus() : existing.getStatus(), // status
                            updatedSource.getPaymentDate(), // payment date
                            updatedSource.getNotes(), // notes
                            existing.getCreatedAt(), // original creation timestamp
                            existing.getUpdatedAt() // previous update timestamp
                    ); // End updated invoice creation
                    Invoice saved = Objects.requireNonNull(invoiceRepository.save(updated), "saved invoice must not be null"); // Persist updated invoice
                    Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null"); // Validate id
                    replaceInvoiceLines(savedId, lines); // Replace lines for invoice
                    documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.UPDATED, "Fattura aggiornata"); // Log update
                    if (existing.getStatus() != saved.getStatus()) { // Check status change
                        documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.STATUS_CHANGED, // Log status change
                                "Stato cambiato da " + existing.getStatus() + " a " + saved.getStatus()); // Status change message
                    } // End status change check
                    statisticsService.clearCache(); // Invalidate statistics cache
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId)); // Return updated DTO
                }); // End optional mapping
    } // End update

    @Transactional // Execute within transaction
    public boolean delete(Long id) { // Delete invoice by id
        Long requiredId = Objects.requireNonNull(id, "id must not be null"); // Validate id
        return invoiceRepository.findById(requiredId) // Attempt to find invoice
                .map(invoice -> { // If present
                    invoiceRepository.deleteById(requiredId); // Delete invoice record
                    invoiceLineRepository.deleteByInvoiceId(requiredId); // Delete related invoice lines
                    documentHistoryService.log(DocumentType.INVOICE, // Log deletion
                            Objects.requireNonNull(invoice.getId(), "invoice id must not be null"), // Validate invoice id
                            DocumentAction.DELETED, // Action type
                            "Fattura eliminata"); // Message
                    statisticsService.clearCache(); // Invalidate statistics cache
                    return true; // Indicate success
                }) // End map
                .orElse(false); // Return false if invoice not found
    } // End delete

    @Transactional // Execute within transaction
    public Optional<InvoiceDTO> registerPayment(Long id, InvoicePaymentRequest paymentRequest) { // Register invoice payment
        InvoicePaymentRequest requiredRequest = Objects.requireNonNull(paymentRequest, // Validate request
                "paymentRequest must not be null"); // Error message
        LocalDate paymentDate = requiredRequest.getPaymentDate() != null ? requiredRequest.getPaymentDate() : LocalDate.now(); // Determine payment date
        return invoiceRepository.findById(Objects.requireNonNull(id, "id must not be null")) // Find invoice by id
                .map(invoice -> { // If found
                    Invoice saved = invoiceRepository.save(invoice.registerPayment(paymentDate, InvoiceStatus.PAID)); // Save paid invoice
                    Long savedId = Objects.requireNonNull(saved.getId(), "invoice id must not be null"); // Validate id
                    documentHistoryService.log(DocumentType.INVOICE, savedId, DocumentAction.PAYMENT_REGISTERED, // Log payment registration
                            "Pagamento registrato il " + paymentDate); // Message
                    commissionService.updateAfterPayment(saved.getContractId(), saved.getAmount(), // Update commissions
                            requiredRequest.getAmountPaid() != null ? requiredRequest.getAmountPaid() : saved.getAmount()); // Determine paid amount
                    statisticsService.clearCache(); // Invalidate statistics cache
                    return InvoiceMapper.toDto(saved, invoiceLineRepository.findByInvoiceIdOrderById(savedId)); // Return DTO
                }); // End optional mapping
    } // End registerPayment

    public List<DocumentHistoryDTO> history(Long id) { // Retrieve invoice history
        return documentHistoryService.list(DocumentType.INVOICE, Objects.requireNonNull(id, "id must not be null")).stream() // Fetch history entries
                .map(DocumentHistoryMapper::toDto) // Map to DTOs
                .toList(); // Collect to list
    } // End history

    private String generateNumber() { // Generate invoice number
        long timestamp = System.currentTimeMillis(); // Current timestamp
        return "INV-" + timestamp; // Build invoice number prefix
    } // End generateNumber

    private List<InvoiceLine> prepareInvoiceLines(List<InvoiceLineDTO> lineDTOs) { // Convert DTO lines to entities
        if (lineDTOs == null || lineDTOs.isEmpty()) { // Handle null or empty lists
            return List.of(); // Return empty list
        } // End empty check
        List<InvoiceLine> lines = new ArrayList<>(); // Prepare mutable list
        for (InvoiceLineDTO dto : lineDTOs) { // Iterate over DTO lines
            if (dto == null) { // Skip null DTOs
                continue; // Continue loop
            } // End null check
            var article = dto.getArticleId() != null ? articleService.require(dto.getArticleId()) : null; // Load article when id present
            BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : BigDecimal.ONE; // Determine quantity
            if (quantity.signum() <= 0) { // Validate quantity
                throw new IllegalArgumentException("La quantità deve essere maggiore di zero"); // Throw error for invalid quantity
            } // End quantity validation
            BigDecimal unitPrice = dto.getUnitPrice() != null // Determine unit price
                    ? dto.getUnitPrice() // Use provided price
                    : (article != null ? article.getUnitPrice() : BigDecimal.ZERO); // Fall back to article price or zero
            if (unitPrice.signum() < 0) { // Validate price
                throw new IllegalArgumentException("Il prezzo unitario non può essere negativo"); // Throw error for negative price
            } // End price validation
            BigDecimal vatRate = dto.getVatRate() != null // Determine VAT rate
                    ? dto.getVatRate() // Use provided VAT
                    : (article != null ? article.getVatRate() : BigDecimal.ZERO); // Fall back to article VAT or zero
            String description = StringUtils.hasText(dto.getDescription()) // Determine description
                    ? dto.getDescription().trim() // Trim provided description
                    : (article != null ? article.getName() : null); // Fall back to article name
            String articleCode = article != null ? article.getCode() : dto.getArticleCode(); // Determine article code
            BigDecimal total = calculateTotal(quantity, unitPrice, vatRate); // Calculate line total
            lines.add(new InvoiceLine( // Add new invoice line
                    dto.getId(), // line id
                    null, // invoice id will be set later
                    article != null ? article.getId() : dto.getArticleId(), // article id
                    articleCode, // article code
                    description, // description
                    quantity, // quantity
                    unitPrice, // unit price
                    vatRate, // VAT rate
                    total // total amount
            )); // End invoice line creation
        } // End loop
        return lines; // Return prepared lines
    } // End prepareInvoiceLines

    private BigDecimal determineAmount(BigDecimal requestedAmount, List<InvoiceLine> lines) { // Determine invoice amount
        if (lines != null && !lines.isEmpty()) { // If lines present
            return lines.stream() // Stream lines
                    .map(InvoiceLine::getTotal) // Extract totals
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum totals
        } // End line presence check
        return requestedAmount != null ? requestedAmount : BigDecimal.ZERO; // Fallback to requested or zero
    } // End determineAmount

    private void replaceInvoiceLines(Long invoiceId, List<InvoiceLine> lines) { // Replace lines for given invoice
        Long requiredInvoiceId = Objects.requireNonNull(invoiceId, "invoiceId must not be null"); // Validate invoice id
        invoiceLineRepository.deleteByInvoiceId(requiredInvoiceId); // Remove existing lines
        if (lines == null || lines.isEmpty()) { // If no lines to save
            return; // Exit method
        } // End empty check
        List<InvoiceLine> toSave = lines.stream() // Prepara le righe da salvare in un'unica operazione
                .map(line -> Objects.requireNonNull(line, "invoice line must not be null")) // Valida ciascuna riga
                .map(line -> Objects.requireNonNull(line.withInvoice(requiredInvoiceId), "invoice line must not be null")) // Aggiunge l'id fattura
                .toList(); // Colleziona le righe aggiornate
        invoiceLineRepository.saveAll(toSave); // Salva tutte le righe con una sola chiamata al repository
    } // End replaceInvoiceLines

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal unitPrice, BigDecimal vatRate) { // Calculate total line amount
        BigDecimal subtotal = unitPrice.multiply(quantity); // Compute subtotal
        if (vatRate == null) { // If VAT not provided
            return subtotal; // Return subtotal
        } // End VAT check
        return subtotal.add(subtotal.multiply(vatRate)); // Add VAT to subtotal
    } // End calculateTotal
} // End InvoiceService class
