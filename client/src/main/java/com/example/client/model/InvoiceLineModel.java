package com.example.client.model;
// Package dedicato ai modelli lato client (JavaFX).

import com.example.common.dto.InvoiceLineDTO;
// DTO condiviso per rappresentare una riga fattura.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Proprietà JavaFX utilizzate per binding dinamico nella UI.

import java.math.BigDecimal;
import java.math.RoundingMode;
// BigDecimal per calcoli finanziari precisi.

/**
 * Modello di riga fattura utilizzato nel client.
 * Include proprietà osservabili e calcolo automatico del totale.
 */
public class InvoiceLineModel {

    // Identificativo della riga fattura.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // ID articolo associato alla riga.
    private final ObjectProperty<Long> articleId = new SimpleObjectProperty<>();

    // Codice articolo (per visualizzazione).
    private final StringProperty articleCode = new SimpleStringProperty();

    // Nome articolo (non sempre valorizzato).
    private final StringProperty articleName = new SimpleStringProperty();

    // Descrizione libera della riga.
    private final StringProperty description = new SimpleStringProperty();

    // Quantità (default = 1).
    private final ObjectProperty<BigDecimal> quantity = new SimpleObjectProperty<>(BigDecimal.ONE);

    // Prezzo unitario.
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);

    // Aliquota IVA (es. 0.22).
    private final ObjectProperty<BigDecimal> vatRate = new SimpleObjectProperty<>(BigDecimal.ZERO);

    // Totale riga (read-only). Viene ricalcolato automaticamente.
    private final ReadOnlyObjectWrapper<BigDecimal> total = new ReadOnlyObjectWrapper<>(BigDecimal.ZERO);

    /**
     * Costruttore: aggiunge listeners per ricalcolare il totale automaticamente.
     */
    public InvoiceLineModel() {
        // Ricalcolo quando cambia la quantità.
        quantity.addListener((obs, oldValue, newValue) -> recalculateTotal());

        // Ricalcolo quando cambia il prezzo unitario.
        unitPrice.addListener((obs, oldValue, newValue) -> recalculateTotal());

        // Ricalcolo quando cambia l'aliquota IVA.
        vatRate.addListener((obs, oldValue, newValue) -> recalculateTotal());
    }

    /**
     * Converte un DTO in InvoiceLineModel (DTO → Model).
     */
    public static InvoiceLineModel fromDto(InvoiceLineDTO dto) {
        InvoiceLineModel model = new InvoiceLineModel();
        model.setId(dto.getId());
        model.setArticleId(dto.getArticleId());
        model.setArticleCode(dto.getArticleCode());
        model.setDescription(dto.getDescription());
        model.setQuantity(dto.getQuantity());
        model.setUnitPrice(dto.getUnitPrice());
        model.setVatRate(dto.getVatRate());
        model.setTotal(dto.getTotal()); // Valore iniziale totale.
        return model;
    }

    /**
     * Converte il modello JavaFX in un DTO (Model → DTO).
     */
    public InvoiceLineDTO toDto() {
        return new InvoiceLineDTO(
                getId(),
                null, // id fattura (non impostato lato client)
                getArticleId(),
                getArticleCode(),
                getDescription(),
                getQuantity(),
                getUnitPrice(),
                getVatRate(),
                getTotal());
    }

    /**
     * Ricalcola il totale della riga:
     *
     * totale = quantità × prezzo + IVA
     *
     * Arrotondamento HALF_UP a 2 decimali (standard commerciale).
     */
    public void recalculateTotal() {
        BigDecimal qty = getQuantity() != null ? getQuantity() : BigDecimal.ZERO;
        BigDecimal price = getUnitPrice() != null ? getUnitPrice() : BigDecimal.ZERO;
        BigDecimal rate = getVatRate() != null ? getVatRate() : BigDecimal.ZERO;

        // Subtotale senza IVA.
        BigDecimal subtotal = price.multiply(qty).setScale(2, RoundingMode.HALF_UP);

        // Importo IVA.
        BigDecimal vatAmount = rate.compareTo(BigDecimal.ZERO) > 0
                ? subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Totale finale.
        total.set(subtotal.add(vatAmount));
    }

    // ========================
    // GETTER / SETTER
    // + JavaFX Property
    // ========================

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public Long getArticleId() {
        return articleId.get();
    }

    public void setArticleId(Long articleId) {
        this.articleId.set(articleId);
    }

    public ObjectProperty<Long> articleIdProperty() {
        return articleId;
    }

    public String getArticleCode() {
        return articleCode.get();
    }

    public void setArticleCode(String articleCode) {
        this.articleCode.set(articleCode);
    }

    public StringProperty articleCodeProperty() {
        return articleCode;
    }

    public String getArticleName() {
        return articleName.get();
    }

    public void setArticleName(String articleName) {
        this.articleName.set(articleName);
    }

    public StringProperty articleNameProperty() {
        return articleName;
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

    public BigDecimal getQuantity() {
        return quantity.get();
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity.set(quantity);
    }

    public ObjectProperty<BigDecimal> quantityProperty() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return unitPrice;
    }

    public BigDecimal getVatRate() {
        return vatRate.get();
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate.set(vatRate);
    }

    public ObjectProperty<BigDecimal> vatRateProperty() {
        return vatRate;
    }

    public BigDecimal getTotal() {
        return total.get();
    }

    public void setTotal(BigDecimal value) {
        total.set(value != null ? value : BigDecimal.ZERO);
    }

    public ReadOnlyObjectProperty<BigDecimal> totalProperty() {
        return total.getReadOnlyProperty();
    }
}
