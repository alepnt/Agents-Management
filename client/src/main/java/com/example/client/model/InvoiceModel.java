package com.example.client.model;
// Package dei modelli JavaFX lato client, utilizzati per binding con la UI.

import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoiceLineDTO;
import com.example.common.enums.InvoiceStatus;
// DTO e enum condivisi con il backend per rappresentare fatture e righe.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// JavaFX Properties, essenziali per aggiornamento automatico della UI.

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// Tipi usati per importi, date e collezioni.

/**
 * Modello JavaFX che incapsula InvoiceDTO per il binding con la vista.
 * Permette alla UI JavaFX di osservare modifiche sui campi della fattura.
 */
public class InvoiceModel {

    // ID univoco della fattura.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Numero fattura (es. "2025-00123").
    private final StringProperty number = new SimpleStringProperty();

    // ID del contratto collegato alla fattura.
    private final ObjectProperty<Long> contractId = new SimpleObjectProperty<>();

    // ID del cliente a cui la fattura appartiene.
    private final ObjectProperty<Long> customerId = new SimpleObjectProperty<>();

    // Nome cliente (comodo per tabelle e form).
    private final StringProperty customerName = new SimpleStringProperty();

    // Importo totale della fattura.
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    // Data di emissione.
    private final ObjectProperty<LocalDate> issueDate = new SimpleObjectProperty<>();

    // Data di scadenza.
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();

    // Stato della fattura (PAID, UNPAID, OVERDUE...), memorizzato come stringa.
    private final StringProperty status = new SimpleStringProperty();

    // Data di pagamento.
    private final ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();

    // Note aggiuntive dell’operatore.
    private final StringProperty notes = new SimpleStringProperty();

    // Righe fattura (non come JavaFX list, ma come DTO copiati).
    private List<InvoiceLineDTO> lines = new ArrayList<>();

    /**
     * Crea un InvoiceModel a partire da un InvoiceDTO (DTO → Model).
     */
    public static InvoiceModel fromDto(InvoiceDTO dto) {
        InvoiceModel model = new InvoiceModel();

        model.setId(dto.getId());
        model.setNumber(dto.getNumber());
        model.setContractId(dto.getContractId());
        model.setCustomerId(dto.getCustomerId());
        model.setCustomerName(dto.getCustomerName());
        model.setAmount(dto.getAmount());
        model.setIssueDate(dto.getIssueDate());
        model.setDueDate(dto.getDueDate());
        model.setPaymentDate(dto.getPaymentDate());
        model.setNotes(dto.getNotes());
        model.setLines(dto.getLines()); // Copia delle righe fattura.

        // Stato: enum → stringa se presente.
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus().name());
        }

        return model;
    }

    /**
     * Converte il modello JavaFX in un InvoiceDTO (Model → DTO).
     */
    public InvoiceDTO toDto() {
        // Converte lo stato stringa in enum.
        InvoiceStatus invoiceStatus = getStatus() != null && !getStatus().isBlank()
                ? InvoiceStatus.valueOf(getStatus())
                : null;

        return new InvoiceDTO(
                getId(),
                getNumber(),
                getContractId(),
                getCustomerId(),
                getCustomerName(),
                getAmount(),
                getIssueDate(),
                getDueDate(),
                invoiceStatus,
                getPaymentDate(),
                getNotes(),
                getLines() // ritorna una nuova copia
        );
    }

    // ===========================
    // GETTER / SETTER
    // + JavaFX Properties
    // ===========================

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public String getNumber() {
        return number.get();
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public StringProperty numberProperty() {
        return number;
    }

    public Long getContractId() {
        return contractId.get();
    }

    public void setContractId(Long contractId) {
        this.contractId.set(contractId);
    }

    public ObjectProperty<Long> contractIdProperty() {
        return contractId;
    }

    public Long getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(Long customerId) {
        this.customerId.set(customerId);
    }

    public ObjectProperty<Long> customerIdProperty() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public LocalDate getIssueDate() {
        return issueDate.get();
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate.set(issueDate);
    }

    public ObjectProperty<LocalDate> issueDateProperty() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public ObjectProperty<LocalDate> paymentDateProperty() {
        return paymentDate;
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public StringProperty notesProperty() {
        return notes;
    }

    /**
     * Restituisce una copia difensiva della lista di righe fattura.
     */
    public List<InvoiceLineDTO> getLines() {
        return new ArrayList<>(lines);
    }

    /**
     * Imposta una nuova lista di righe, copiandola per garantire isolamento.
     */
    public void setLines(List<InvoiceLineDTO> lines) {
        this.lines = lines != null ? new ArrayList<>(lines) : new ArrayList<>();
    }
}
