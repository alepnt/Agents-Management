package com.example.client.model;
// Package dedicato ai modelli JavaFX che rappresentano dati legati all’anagrafica clienti.

import com.example.common.dto.CustomerDTO;
// DTO condiviso con il backend che rappresenta un cliente.

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
// Import per le JavaFX properties, fondamentali per il binding UI.

/**
 * Modello JavaFX per il binding dell'anagrafica clienti.
 * Converte CustomerDTO in proprietà osservabili per TableView, TextField, ecc.
 */
public class CustomerModel {

    // Identificativo univoco del cliente.
    private final ObjectProperty<Long> id = new SimpleObjectProperty<>();

    // Ragione sociale / nome del cliente.
    private final StringProperty name = new SimpleStringProperty();

    // Partita IVA del cliente (se azienda).
    private final StringProperty vatNumber = new SimpleStringProperty();

    // Codice fiscale (persone fisiche o aziende).
    private final StringProperty taxCode = new SimpleStringProperty();

    // Email di contatto.
    private final StringProperty email = new SimpleStringProperty();

    // Numero di telefono.
    private final StringProperty phone = new SimpleStringProperty();

    // Indirizzo completo.
    private final StringProperty address = new SimpleStringProperty();

    /**
     * Converte un CustomerDTO in CustomerModel.
     * Mapping DTO → Model (JavaFX).
     */
    public static CustomerModel fromDto(CustomerDTO dto) {
        CustomerModel model = new CustomerModel(); // Istanza vuota.

        model.setId(dto.getId()); // ID cliente.
        model.setName(dto.getName()); // Nome / Ragione sociale.
        model.setVatNumber(dto.getVatNumber()); // Partita IVA.
        model.setTaxCode(dto.getTaxCode()); // Codice fiscale.
        model.setEmail(dto.getEmail()); // Email.
        model.setPhone(dto.getPhone()); // Telefono.
        model.setAddress(dto.getAddress()); // Indirizzo.

        return model;
    }

    /**
     * Converte il modello JavaFX in un CustomerDTO.
     * Mapping Model → DTO.
     * Gli ultimi due campi vengono lasciati null perché non utilizzati nel client.
     */
    public CustomerDTO toDto() {
        return new CustomerDTO(
                getId(),
                getName(),
                getVatNumber(),
                getTaxCode(),
                getEmail(),
                getPhone(),
                getAddress(),
                null, // Placeholder per eventuali campi estesi.
                null);
    }

    // ===========================
    // GETTER / SETTER
    // + Proprietà osservabili
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getVatNumber() {
        return vatNumber.get();
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber.set(vatNumber);
    }

    public StringProperty vatNumberProperty() {
        return vatNumber;
    }

    public String getTaxCode() {
        return taxCode.get();
    }

    public void setTaxCode(String taxCode) {
        this.taxCode.set(taxCode);
    }

    public StringProperty taxCodeProperty() {
        return taxCode;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    /**
     * Rappresentazione leggibile del cliente, utile in ComboBox o log.
     */
    @Override
    public String toString() {
        return getName() != null ? getName() : "";
    }
}
