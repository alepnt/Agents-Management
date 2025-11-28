package com.example.client.model;
// Package dei modelli JavaFX usati nella UI del client.

import com.example.common.dto.CommissionDTO;
// DTO condiviso col backend che rappresenta i dati di una commissione.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
// JavaFX Property system: permette binding automatico negli elementi UI.

import java.math.BigDecimal;
import java.time.Instant;
// BigDecimal per valori numerici precisi, Instant per timestamp.

/**
 * Modello JavaFX per le commissioni.
 * Traduce CommissionDTO in proprietà osservabili per la UI.
 */
public class CommissionModel {

    // Identificativo della commissione.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Identificativo dell'agente associato alla commissione.
    private final ObjectProperty<Long> agentId = new SimpleObjectProperty<>();

    // Identificativo del contratto da cui deriva la commissione.
    private final ObjectProperty<Long> contractId = new SimpleObjectProperty<>();

    // Commissione totale calcolata sul contratto.
    private final ObjectProperty<BigDecimal> totalCommission = new SimpleObjectProperty<>();

    // Quota di commissione già pagata.
    private final ObjectProperty<BigDecimal> paidCommission = new SimpleObjectProperty<>();

    // Quota di commissione ancora da liquidare.
    private final ObjectProperty<BigDecimal> pendingCommission = new SimpleObjectProperty<>();

    // Timestamp dell'ultimo aggiornamento.
    private final ObjectProperty<Instant> lastUpdated = new SimpleObjectProperty<>();

    /**
     * Converte un DTO in un modello JavaFX.
     * Mapping DTO → Model.
     */
    public static CommissionModel fromDto(CommissionDTO dto) {
        CommissionModel model = new CommissionModel(); // Nuova istanza.

        model.setId(dto.getId()); // Setta id.
        model.setAgentId(dto.getAgentId()); // Id agente.
        model.setContractId(dto.getContractId()); // Id contratto.
        model.setTotalCommission(dto.getTotalCommission()); // Totale.
        model.setPaidCommission(dto.getPaidCommission()); // Pagata.
        model.setPendingCommission(dto.getPendingCommission()); // Da pagare.
        model.setLastUpdated(dto.getLastUpdated()); // Ultimo update.

        return model;
    }

    /**
     * Converte il modello JavaFX in DTO per il backend.
     * Mapping Model → DTO.
     */
    public CommissionDTO toDto() {
        return new CommissionDTO(
                getId(),
                getAgentId(),
                getContractId(),
                getTotalCommission(),
                getPaidCommission(),
                getPendingCommission(),
                getLastUpdated());
    }

    // Getter e setter + proprietà osservabili (JavaFX standard pattern).

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

    public Long getContractId() {
        return contractId.get();
    }

    public void setContractId(Long contractId) {
        this.contractId.set(contractId);
    }

    public ObjectProperty<Long> contractIdProperty() {
        return contractId;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission.get();
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission.set(totalCommission);
    }

    public ObjectProperty<BigDecimal> totalCommissionProperty() {
        return totalCommission;
    }

    public BigDecimal getPaidCommission() {
        return paidCommission.get();
    }

    public void setPaidCommission(BigDecimal paidCommission) {
        this.paidCommission.set(paidCommission);
    }

    public ObjectProperty<BigDecimal> paidCommissionProperty() {
        return paidCommission;
    }

    public BigDecimal getPendingCommission() {
        return pendingCommission.get();
    }

    public void setPendingCommission(BigDecimal pendingCommission) {
        this.pendingCommission.set(pendingCommission);
    }

    public ObjectProperty<BigDecimal> pendingCommissionProperty() {
        return pendingCommission;
    }

    public Instant getLastUpdated() {
        return lastUpdated.get();
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated.set(lastUpdated);
    }

    public ObjectProperty<Instant> lastUpdatedProperty() {
        return lastUpdated;
    }
}
