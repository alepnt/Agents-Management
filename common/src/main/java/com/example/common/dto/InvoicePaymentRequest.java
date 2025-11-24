package com.example.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Payload per registrare un pagamento di fattura.
 */
public class InvoicePaymentRequest {

    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amountPaid;

    public InvoicePaymentRequest() {
    }

    public InvoicePaymentRequest(LocalDate paymentDate, BigDecimal amountPaid) {
        this.paymentDate = paymentDate;
        this.amountPaid = amountPaid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoicePaymentRequest that = (InvoicePaymentRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
