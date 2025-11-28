package com.example.client.model;
// Package in cui risiedono i modelli JavaFX legati al client.

import com.example.common.dto.ContractDTO;
// DTO condiviso con il server, rappresenta un contratto lato backend.

import com.example.common.enums.ContractStatus;
// Enum dello stato del contratto, definito nel modulo common.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Proprietà JavaFX: permettono binding automatico tra UI e modello.

import java.math.BigDecimal;
import java.time.LocalDate;
// BigDecimal per importi, LocalDate per date contrattuali.

/**
 * Modello JavaFX per i contratti.
 * Mappa ContractDTO in proprietà osservabili compatibili con TableView e form.
 */
public class ContractModel {

    // Identificativo univoco del contratto.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Identificativo dell’agente assegnato al contratto.
    private final ObjectProperty<Long> agentId = new SimpleObjectProperty<>();

    // Nome del cliente associato al contratto.
    private final StringProperty customerName = new SimpleStringProperty();

    // Descrizione sintetica del contratto.
    private final StringProperty description = new SimpleStringProperty();

    // Data di inizio contratto.
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();

    // Data di fine contratto.
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    // Valore economico complessivo del contratto (imponibile).
    private final ObjectProperty<BigDecimal> totalValue = new SimpleObjectProperty<>();

    // Stato del contratto (ACTIVE, CLOSED, CANCELED, ecc.), memorizzato come
    // stringa.
    private final StringProperty status = new SimpleStringProperty();

    /**
     * Costruisce un ContractModel partendo da ContractDTO.
     * Mapping DTO → Model JavaFX.
     */
    public static ContractModel fromDto(ContractDTO dto) {
        ContractModel model = new ContractModel(); // Nuova istanza modello.

        model.setId(dto.getId()); // ID contratto.
        model.setAgentId(dto.getAgentId()); // ID agente.
        model.setCustomerName(dto.getCustomerName()); // Nome cliente.
        model.setDescription(dto.getDescription()); // Descrizione.
        model.setStartDate(dto.getStartDate()); // Data inizio.
        model.setEndDate(dto.getEndDate()); // Data fine.
        model.setTotalValue(dto.getTotalValue()); // Valore totale.

        // Stato espresso come enum → lo convertiamo a stringa per JavaFX.
        if (dto.getStatus() != null) {
            model.setStatus(dto.getStatus().name());
        }

        return model;
    }

    /**
     * Converte il modello JavaFX in un DTO completo.
     * Mapping Model → DTO.
     */
    public ContractDTO toDto() {

        // Converte la stringa JavaFX in enum ContractStatus (se presente).
        ContractStatus contractStatus = getStatus() != null && !getStatus().isBlank()
                ? ContractStatus.valueOf(getStatus())
                : null;

        // Costruisce il DTO da inviare al backend.
        return new ContractDTO(
                getId(),
                getAgentId(),
                getCustomerName(),
                getDescription(),
                getStartDate(),
                getEndDate(),
                getTotalValue(),
                contractStatus);
    }

    // ===========================
    // GETTER / SETTER
    // + Proprietà JavaFX
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

    public Long getAgentId() {
        return agentId.get();
    }

    public void setAgentId(Long agentId) {
        this.agentId.set(agentId);
    }

    public ObjectProperty<Long> agentIdProperty() {
        return agentId;
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue.get();
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue.set(totalValue);
    }

    public ObjectProperty<BigDecimal> totalValueProperty() {
        return totalValue;
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
}
