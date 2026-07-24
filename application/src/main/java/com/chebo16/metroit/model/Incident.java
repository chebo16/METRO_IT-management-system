package com.chebo16.metroit.model;

import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;

import java.time.LocalDateTime;

public class Incident {

    private Long id;
    private String title;
    private String description;
    private IncidentPriority priority;
    private IncidentStatus status;

    private Long equipmentId;
    private Long createdById;
    private Long assignedTechnicianId;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    private String solutionDescription;

    public Incident() {
    }

    public Incident(
            Long id,
            String title,
            String description,
            IncidentPriority priority,
            IncidentStatus status,
            Long equipmentId,
            Long createdById,
            Long assignedTechnicianId,
            LocalDateTime createdAt,
            LocalDateTime startedAt,
            LocalDateTime resolvedAt,
            LocalDateTime closedAt,
            String solutionDescription
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.equipmentId = equipmentId;
        this.createdById = createdById;
        this.assignedTechnicianId = assignedTechnicianId;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
        this.solutionDescription = solutionDescription;
    }

    public Incident(
            String title,
            String description,
            IncidentPriority priority,
            Long equipmentId,
            Long createdById,
            Long assignedTechnicianId
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = IncidentStatus.NEW;
        this.equipmentId = equipmentId;
        this.createdById = createdById;
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentPriority getPriority() {
        return priority;
    }

    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(Long assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getSolutionDescription() {
        return solutionDescription;
    }

    public void setSolutionDescription(String solutionDescription) {
        this.solutionDescription = solutionDescription;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", equipmentId=" + equipmentId +
                ", createdById=" + createdById +
                ", assignedTechnicianId=" + assignedTechnicianId +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", resolvedAt=" + resolvedAt +
                ", closedAt=" + closedAt +
                ", solutionDescription='" + solutionDescription + '\'' +
                '}';
    }
}