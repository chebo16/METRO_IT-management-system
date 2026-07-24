package com.chebo16.metroit.model;

import com.chebo16.metroit.model.enums.EquipmentStatus;

import java.time.LocalDateTime;

public class Equipment {

    private Long id;
    private String inventoryNumber;
    private String name;
    private String type;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String ipAddress;
    private EquipmentStatus status;
    private LocalDateTime createdAt;
    private String notes;

    public Equipment() {
    }

    public Equipment(
            Long id,
            String inventoryNumber,
            String name,
            String type,
            String manufacturer,
            String model,
            String serialNumber,
            String ipAddress,
            EquipmentStatus status,
            LocalDateTime createdAt,
            String notes
    ) {
        this.id = id;
        this.inventoryNumber = inventoryNumber;
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.ipAddress = ipAddress;
        this.status = status;
        this.createdAt = createdAt;
        this.notes = notes;
    }

    public Equipment(
            String inventoryNumber,
            String name,
            String type,
            String manufacturer,
            String model,
            String serialNumber,
            String ipAddress,
            String notes
    ) {
        this.inventoryNumber = inventoryNumber;
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.ipAddress = ipAddress;
        this.status = EquipmentStatus.ACTIVE;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", inventoryNumber='" + inventoryNumber + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", notes='" + notes + '\'' +
                '}';
    }
}