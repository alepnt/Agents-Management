package com.example.server.service;

import com.example.common.dto.InvoiceLineDTO;
import com.example.server.domain.InvoiceLine;
import com.example.server.repository.InvoiceLineRepository;
import com.example.server.service.mapper.InvoiceLineMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class InvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;

    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public List<InvoiceLineDTO> findAll() {
        return StreamSupport.stream(invoiceLineRepository.findAll().spliterator(), false)
                .map(InvoiceLineMapper::toDto)
                .toList();
    }

    public List<InvoiceLineDTO> findByInvoiceId(Long invoiceId) {
        return invoiceLineRepository.findByInvoiceIdOrderById(Objects.requireNonNull(invoiceId, "invoiceId must not be null")).stream()
                .map(InvoiceLineMapper::toDto)
                .toList();
    }

    public Optional<InvoiceLineDTO> findById(Long id) {
        return invoiceLineRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .map(InvoiceLineMapper::toDto);
    }

    @Transactional
    public InvoiceLineDTO create(InvoiceLineDTO dto) {
        InvoiceLine toSave = validateAndNormalize(null, Objects.requireNonNull(dto, "invoice line must not be null"));
        InvoiceLine saved = Objects.requireNonNull(invoiceLineRepository.save(toSave), "invoice line must not be null");
        return InvoiceLineMapper.toDto(saved);
    }

    @Transactional
    public Optional<InvoiceLineDTO> update(Long id, InvoiceLineDTO dto) {
        InvoiceLineDTO validated = Objects.requireNonNull(dto, "invoice line must not be null");
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        return invoiceLineRepository.findById(requiredId)
                .map(existing -> validateAndNormalize(existing.getId(), validated))
                .map(invoiceLineRepository::save)
                .map(InvoiceLineMapper::toDto);
    }

    @Transactional
    public boolean delete(Long id) {
        Long requiredId = Objects.requireNonNull(id, "id must not be null");
        if (!invoiceLineRepository.existsById(requiredId)) {
            return false;
        }
        invoiceLineRepository.deleteById(requiredId);
        return true;
    }

    private InvoiceLine validateAndNormalize(Long id, InvoiceLineDTO dto) {
        Long invoiceId = Objects.requireNonNull(dto.getInvoiceId(), "invoiceId must not be null");
        BigDecimal quantity = normalizeQuantity(dto.getQuantity());
        BigDecimal unitPrice = normalizePrice(dto.getUnitPrice());
        BigDecimal vatRate = normalizeVatRate(dto.getVatRate());
        String description = normalizeText(dto.getDescription());
        String articleCode = normalizeText(dto.getArticleCode());
        BigDecimal total = calculateTotal(quantity, unitPrice, vatRate);
        return new InvoiceLine(
                id,
                invoiceId,
                dto.getArticleId(),
                articleCode,
                description,
                quantity,
                unitPrice,
                vatRate,
                total
        );
    }

    private BigDecimal normalizeQuantity(BigDecimal quantity) {
        BigDecimal normalized = quantity != null ? quantity : BigDecimal.ONE;
        if (normalized.signum() <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }
        return normalized;
    }

    private BigDecimal normalizePrice(BigDecimal unitPrice) {
        BigDecimal normalized = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        if (normalized.signum() < 0) {
            throw new IllegalArgumentException("Il prezzo unitario non può essere negativo");
        }
        return normalized;
    }

    private BigDecimal normalizeVatRate(BigDecimal vatRate) {
        if (vatRate == null) {
            return BigDecimal.ZERO;
        }
        if (vatRate.signum() < 0) {
            throw new IllegalArgumentException("L'aliquota IVA non può essere negativa");
        }
        return vatRate;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal unitPrice, BigDecimal vatRate) {
        BigDecimal subtotal = unitPrice.multiply(quantity);
        if (vatRate == null) {
            return subtotal;
        }
        return subtotal.add(subtotal.multiply(vatRate));
    }
}
