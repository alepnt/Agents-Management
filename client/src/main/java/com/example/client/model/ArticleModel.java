package com.example.client.model;
// Package che contiene i modelli JavaFX usati lato client.

import com.example.common.dto.ArticleDTO;
// DTO condiviso usato per rappresentare un articolo tra client e server.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import delle proprietà JavaFX che rendono i campi osservabili.

import java.math.BigDecimal;
// BigDecimal per prezzi e valori numerici precisi.

/**
 * Modello JavaFX per la gestione del catalogo articoli.
 * Converte l'ArticleDTO in proprietà JavaFX per TableView, form e binding.
 */
public class ArticleModel {

    // Identificativo univoco dell’articolo (osservabile).
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Codice articolo (osservabile, usato spesso nelle tabelle).
    private final StringProperty code = new SimpleStringProperty();

    // Nome articolo.
    private final StringProperty name = new SimpleStringProperty();

    // Descrizione estesa dell’articolo.
    private final StringProperty description = new SimpleStringProperty();

    // Prezzo unitario (BigDecimal) come proprietà osservabile.
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>();

    // Aliquota IVA come BigDecimal (es. 0.22).
    private final ObjectProperty<BigDecimal> vatRate = new SimpleObjectProperty<>();

    // Unità di misura (es. "pz", "kg", "l").
    private final StringProperty unitOfMeasure = new SimpleStringProperty();

    /**
     * Costruisce un ArticleModel a partire da un ArticleDTO.
     * Mapping DTO → JavaFX Model.
     */
    public static ArticleModel fromDto(ArticleDTO dto) {
        ArticleModel model = new ArticleModel(); // Istanzia il modello.

        model.setId(dto.getId()); // Setta id.
        model.setCode(dto.getCode()); // Setta il codice.
        model.setName(dto.getName()); // Setta il nome.
        model.setDescription(dto.getDescription()); // Descrizione.
        model.setUnitPrice(dto.getUnitPrice()); // Prezzo unitario.
        model.setVatRate(dto.getVatRate()); // Aliquota IVA.
        model.setUnitOfMeasure(dto.getUnitOfMeasure()); // Unità di misura.

        return model;
    }

    /**
     * Converte il modello JavaFX in un DTO per invio al backend.
     * Nota: gli ultimi due campi vengono settati a null (placeholder).
     */
    public ArticleDTO toDto() {
        return new ArticleDTO(
                getId(),
                getCode(),
                getName(),
                getDescription(),
                getUnitPrice(),
                getVatRate(),
                getUnitOfMeasure(),
                null, // Campi aggiuntivi non usati nel client.
                null);
    }

    // Getter per id.
    public Long getId() {
        return id.get();
    }

    // Setter per id.
    public void setId(Long id) {
        this.id.set(id);
    }

    // Espone la proprietà id per binding JavaFX.
    public ObjectProperty<Long> idProperty() {
        return id;
    }

    // Getter per il codice articolo.
    public String getCode() {
        return code.get();
    }

    // Setter per codice articolo.
    public void setCode(String code) {
        this.code.set(code);
    }

    // Proprietà codice.
    public StringProperty codeProperty() {
        return code;
    }

    // Getter nome articolo.
    public String getName() {
        return name.get();
    }

    // Setter nome articolo.
    public void setName(String name) {
        this.name.set(name);
    }

    // Proprietà osservabile nome.
    public StringProperty nameProperty() {
        return name;
    }

    // Getter descrizione.
    public String getDescription() {
        return description.get();
    }

    // Setter descrizione.
    public void setDescription(String description) {
        this.description.set(description);
    }

    // Proprietà descrizione.
    public StringProperty descriptionProperty() {
        return description;
    }

    // Getter prezzo unitario.
    public BigDecimal getUnitPrice() {
        return unitPrice.get();
    }

    // Setter prezzo unitario.
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice.set(unitPrice);
    }

    // Proprietà osservabile prezzo.
    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return unitPrice;
    }

    // Getter aliquota IVA.
    public BigDecimal getVatRate() {
        return vatRate.get();
    }

    // Setter aliquota IVA.
    public void setVatRate(BigDecimal vatRate) {
        this.vatRate.set(vatRate);
    }

    // Proprietà osservabile IVA.
    public ObjectProperty<BigDecimal> vatRateProperty() {
        return vatRate;
    }

    // Getter unità di misura.
    public String getUnitOfMeasure() {
        return unitOfMeasure.get();
    }

    // Setter unità di misura.
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure.set(unitOfMeasure);
    }

    // Proprietà unità di misura.
    public StringProperty unitOfMeasureProperty() {
        return unitOfMeasure;
    }

    /**
     * Rappresentazione testuale usata nelle ComboBox e debug.
     * Se disponibile mostra "CODICE - Nome".
     * Altrimenti solo il nome.
     */
    @Override
    public String toString() {
        if (getCode() != null && !getCode().isBlank()) {
            return getCode() + " - " + (getName() != null ? getName() : "");
        }
        return getName() != null ? getName() : "";
    }
}
