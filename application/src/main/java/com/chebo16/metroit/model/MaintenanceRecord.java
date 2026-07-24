package com.chebo16.metroit.model;

import com.chebo16.metroit.model.enums.MaintenanceResult;

import java.time.LocalDateTime;

public class MaintenanceRecord {

    private Long id;
    private Long incidentId;
    private Long equipmentId;
    private Long technicianId;
    private String workDescription;
    private String replacedComponents;
    private MaintenanceResult result;
    private LocalDateTime performedAt;

    public MaintenanceRecord() {
    }

    public MaintenanceRecord(
            Long id,
            Long incidentId,
            Long equipmentId,
            Long technicianId,
            String workDescription,
            String replacedComponents,
            MaintenanceResult result,
            LocalDateTime performedAt
    ) {
        this.id = id;
        this.incidentId = incidentId;
        this.equipmentId = equipmentId;
        this.technicianId = technicianId;
        this.workDescription = workDescription;
        this.replacedComponents = replacedComponents;
        this.result = result;
        this.performedAt = performedAt;
    }

    public MaintenanceRecord(
            Long incidentId,
            Long equipmentId,
            Long technicianId,
            String workDescription,
            String replacedComponents,
            MaintenanceResult result
    ) {
        this.incidentId = incidentId;
        this.equipmentId = equipmentId;
        this.technicianId = technicianId;
        this.workDescription = workDescription;
        this.replacedComponents = replacedComponents;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public String getReplacedComponents() {
        return replacedComponents;
    }

    public void setReplacedComponents(String replacedComponents) {
        this.replacedComponents = replacedComponents;
    }

    public MaintenanceResult getResult() {
        return result;
    }

    public void setResult(MaintenanceResult result) {
        this.result = result;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    @Override
    public String toString() {
        return "MaintenanceRecord{" +
                "id=" + id +
                ", incidentId=" + incidentId +
                ", equipmentId=" + equipmentId +
                ", technicianId=" + technicianId +
                ", workDescription='" + workDescription + '\'' +
                ", replacedComponents='" + replacedComponents + '\'' +
                ", result=" + result +
                ", performedAt=" + performedAt +
                '}';
    }
}