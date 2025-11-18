package com.example.client.model;

import com.example.common.dto.CommissionDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Modello JavaFX per le commissioni.
 */
public class CommissionModel {

    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> agentId = new SimpleObjectProperty<>();
    private final ObjectProperty<Long> contractId = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalCommission = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> paidCommission = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> pendingCommission = new SimpleObjectProperty<>();
    private final ObjectProperty<Instant> lastUpdated = new SimpleObjectProperty<>();

    public static CommissionModel fromDto(CommissionDTO dto) {
        CommissionModel model = new CommissionModel();
        model.setId(dto.getId());
        model.setAgentId(dto.getAgentId());
        model.setContractId(dto.getContractId());
        model.setTotalCommission(dto.getTotalCommission());
        model.setPaidCommission(dto.getPaidCommission());
        model.setPendingCommission(dto.getPendingCommission());
        model.setLastUpdated(dto.getLastUpdated());
        return model;
    }

    public CommissionDTO toDto() {
        return new CommissionDTO(getId(), getAgentId(), getContractId(), getTotalCommission(), getPaidCommission(), getPendingCommission(), getLastUpdated());
    }

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
