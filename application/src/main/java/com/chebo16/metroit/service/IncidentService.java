package com.chebo16.metroit.service;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class IncidentService {

    private final IncidentDAO incidentDAO;
    private final EquipmentDAO equipmentDAO;
    private final UserDAO userDAO;

    public IncidentService() {
        this(
                new IncidentDAO(),
                new EquipmentDAO(),
                new UserDAO()
        );
    }

    public IncidentService(
            IncidentDAO incidentDAO,
            EquipmentDAO equipmentDAO,
            UserDAO userDAO
    ) {
        this.incidentDAO = Objects.requireNonNull(
                incidentDAO,
                "IncidentDAO must not be null."
        );

        this.equipmentDAO = Objects.requireNonNull(
                equipmentDAO,
                "EquipmentDAO must not be null."
        );

        this.userDAO = Objects.requireNonNull(
                userDAO,
                "UserDAO must not be null."
        );
    }

    public List<Incident> getAllIncidents() {
        try {
            return incidentDAO.findAll();
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load incidents.",
                    exception
            );
        }
    }

    public Incident getIncidentById(long incidentId) {
        validateId(incidentId, "Incident ID");

        try {
            return incidentDAO.findById(incidentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Incident was not found: " + incidentId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load incident with ID: " + incidentId,
                    exception
            );
        }
    }

    public List<Incident> getIncidentsByStatus(IncidentStatus status) {
        if (status == null) {
            throw new ValidationException(
                    "Incident status must not be null."
            );
        }

        try {
            return incidentDAO.findByStatus(status);
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load incidents by status: " + status,
                    exception
            );
        }
    }

    public List<Incident> getIncidentsByTechnician(long technicianId) {
        validateId(technicianId, "Technician ID");
        validateActiveTechnician(technicianId);

        try {
            return incidentDAO.findByTechnicianId(technicianId);
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load incidents assigned to technician ID: "
                            + technicianId,
                    exception
            );
        }
    }

    public Incident createIncident(Incident incident) {
        Objects.requireNonNull(
                incident,
                "Incident must not be null."
        );

        normalizeIncident(incident);
        validateIncidentFields(incident);

        validateEquipmentExists(incident.getEquipmentId());

        validateUserExists(
                incident.getCreatedById(),
                "Incident author"
        );

        if (incident.getAssignedTechnicianId() != null) {
            validateActiveTechnician(
                    incident.getAssignedTechnicianId()
            );
        }

        incident.setStatus(IncidentStatus.NEW);
        incident.setStartedAt(null);
        incident.setResolvedAt(null);
        incident.setClosedAt(null);
        incident.setSolutionDescription(null);

        try {
            long generatedId = incidentDAO.insert(incident);

            return incidentDAO.findById(generatedId)
                    .orElseThrow(() -> new ServiceException(
                            "Incident was created, but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to create incident.",
                    exception
            );
        }
    }

    public Incident updateIncident(Incident incident) {
        Objects.requireNonNull(
                incident,
                "Incident must not be null."
        );

        if (incident.getId() == null) {
            throw new ValidationException(
                    "Incident ID is required for update."
            );
        }

        validateId(incident.getId(), "Incident ID");

        Incident existingIncident =
                getIncidentById(incident.getId());

        if (existingIncident.getStatus() == IncidentStatus.CLOSED) {
            throw new ValidationException(
                    "Closed incident cannot be edited."
            );
        }

        if (existingIncident.getStatus() == IncidentStatus.RESOLVED
                && !Objects.equals(
                existingIncident.getAssignedTechnicianId(),
                incident.getAssignedTechnicianId()
        )) {
            throw new ValidationException(
                    "Technician assignment cannot be changed "
                            + "for a resolved incident."
            );
        }

        normalizeIncident(incident);
        validateIncidentFields(incident);

        validateEquipmentExists(incident.getEquipmentId());

        validateUserExists(
                incident.getCreatedById(),
                "Incident author"
        );

        if (incident.getAssignedTechnicianId() != null) {
            validateActiveTechnician(
                    incident.getAssignedTechnicianId()
            );
        }

        incident.setStatus(existingIncident.getStatus());
        incident.setCreatedAt(existingIncident.getCreatedAt());
        incident.setStartedAt(existingIncident.getStartedAt());
        incident.setResolvedAt(existingIncident.getResolvedAt());
        incident.setClosedAt(existingIncident.getClosedAt());
        incident.setSolutionDescription(
                existingIncident.getSolutionDescription()
        );

        try {
            boolean updated = incidentDAO.update(incident);

            if (!updated) {
                throw new NotFoundException(
                        "Incident was not found: " + incident.getId()
                );
            }

            return incidentDAO.findById(incident.getId())
                    .orElseThrow(() -> new ServiceException(
                            "Incident was updated, but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to update incident with ID: "
                            + incident.getId(),
                    exception
            );
        }
    }

    public Incident assignTechnician(
            long incidentId,
            long technicianId
    ) {
        validateId(incidentId, "Incident ID");
        validateId(technicianId, "Technician ID");

        Incident incident = getIncidentById(incidentId);

        validateTechnicianAssignmentAllowed(incident);
        validateActiveTechnician(technicianId);

        try {
            boolean assigned = incidentDAO.assignTechnician(
                    incidentId,
                    technicianId
            );

            if (!assigned) {
                throw new NotFoundException(
                        "Incident was not found: " + incidentId
                );
            }

            return incidentDAO.findById(incidentId)
                    .orElseThrow(() -> new ServiceException(
                            "Technician was assigned, "
                                    + "but the incident could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to assign technician "
                            + technicianId
                            + " to incident "
                            + incidentId,
                    exception
            );
        }
    }

    public Incident unassignTechnician(long incidentId) {
        validateId(incidentId, "Incident ID");

        Incident incident = getIncidentById(incidentId);

        validateTechnicianAssignmentAllowed(incident);

        try {
            boolean updated = incidentDAO.assignTechnician(
                    incidentId,
                    null
            );

            if (!updated) {
                throw new NotFoundException(
                        "Incident was not found: " + incidentId
                );
            }

            return incidentDAO.findById(incidentId)
                    .orElseThrow(() -> new ServiceException(
                            "Technician was removed, "
                                    + "but the incident could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to remove technician from incident ID: "
                            + incidentId,
                    exception
            );
        }
    }

    public Incident changeStatus(
            long incidentId,
            IncidentStatus newStatus,
            String solutionDescription
    ) {
        validateId(incidentId, "Incident ID");

        if (newStatus == null) {
            throw new ValidationException(
                    "New incident status must not be null."
            );
        }

        Incident incident = getIncidentById(incidentId);
        IncidentStatus currentStatus = incident.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        String normalizedSolution =
                trimToNull(solutionDescription);

        if (newStatus == IncidentStatus.RESOLVED
                || newStatus == IncidentStatus.CLOSED) {

            requireText(
                    normalizedSolution,
                    "Solution description"
            );
        }

        if (newStatus == IncidentStatus.IN_PROGRESS
                && incident.getAssignedTechnicianId() == null) {

            throw new ValidationException(
                    "A technician must be assigned before "
                            + "the incident can be moved to IN_PROGRESS."
            );
        }

        try {
            boolean updated = incidentDAO.updateStatus(
                    incidentId,
                    newStatus,
                    normalizedSolution
            );

            if (!updated) {
                throw new NotFoundException(
                        "Incident was not found: " + incidentId
                );
            }

            return incidentDAO.findById(incidentId)
                    .orElseThrow(() -> new ServiceException(
                            "Incident status was updated, "
                                    + "but the incident could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to change incident status for incident ID: "
                            + incidentId,
                    exception
            );
        }
    }

    private void validateStatusTransition(
            IncidentStatus currentStatus,
            IncidentStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            throw new ValidationException(
                    "Incident already has status: " + currentStatus
            );
        }

        boolean allowed = switch (currentStatus) {
            case NEW ->
                    newStatus == IncidentStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                    newStatus == IncidentStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == IncidentStatus.IN_PROGRESS
                            || newStatus == IncidentStatus.CLOSED;

            case CLOSED -> false;
        };

        if (!allowed) {
            throw new ValidationException(
                    "Invalid incident status transition: "
                            + currentStatus
                            + " → "
                            + newStatus
            );
        }
    }

    private void validateTechnicianAssignmentAllowed(
            Incident incident
    ) {
        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            throw new ValidationException(
                    "Technician assignment cannot be changed "
                            + "for a resolved incident."
            );
        }

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new ValidationException(
                    "Technician assignment cannot be changed "
                            + "for a closed incident."
            );
        }
    }

    private void validateEquipmentExists(Long equipmentId) {
        validateId(equipmentId, "Equipment ID");

        try {
            Equipment equipment = equipmentDAO.findById(equipmentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Equipment was not found: " + equipmentId
                    ));

            if (equipment.getId() == null) {
                throw new NotFoundException(
                        "Equipment was not found: " + equipmentId
                );
            }
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate equipment ID: " + equipmentId,
                    exception
            );
        }
    }

    private void validateUserExists(
            Long userId,
            String fieldName
    ) {
        validateId(userId, fieldName + " ID");

        try {
            userDAO.findById(userId)
                    .orElseThrow(() -> new NotFoundException(
                            fieldName + " was not found: " + userId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate "
                            + fieldName.toLowerCase()
                            + " ID: "
                            + userId,
                    exception
            );
        }
    }

    private void validateActiveTechnician(Long technicianId) {
        validateId(technicianId, "Technician ID");

        try {
            User technician = userDAO.findById(technicianId)
                    .orElseThrow(() -> new NotFoundException(
                            "Technician was not found: " + technicianId
                    ));

            if (technician.getRole() != UserRole.TECHNICIAN) {
                throw new ValidationException(
                        "Assigned user is not a technician: "
                                + technicianId
                );
            }

            if (!technician.isActive()) {
                throw new ValidationException(
                        "Assigned technician is inactive: "
                                + technicianId
                );
            }
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate technician ID: "
                            + technicianId,
                    exception
            );
        }
    }

    private void validateIncidentFields(Incident incident) {
        requireText(
                incident.getTitle(),
                "Incident title"
        );

        requireText(
                incident.getDescription(),
                "Incident description"
        );

        if (incident.getPriority() == null) {
            throw new ValidationException(
                    "Incident priority must not be null."
            );
        }

        validateMaximumLength(
                incident.getTitle(),
                150,
                "Incident title"
        );
    }

    private void normalizeIncident(Incident incident) {
        incident.setTitle(
                trimRequired(incident.getTitle())
        );

        incident.setDescription(
                trimRequired(incident.getDescription())
        );

        incident.setSolutionDescription(
                trimToNull(incident.getSolutionDescription())
        );
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new ValidationException(
                    fieldName + " must be greater than zero."
            );
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " must not be empty."
            );
        }
    }

    private void validateMaximumLength(
            String value,
            int maximumLength,
            String fieldName
    ) {
        if (value != null && value.length() > maximumLength) {
            throw new ValidationException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }
    }

    private String trimRequired(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            return null;
        }

        return trimmedValue;
    }
}