package com.chebo16.metroit.service;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.MaintenanceRecordDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class MaintenanceRecordService {

    private final MaintenanceRecordDAO maintenanceRecordDAO;
    private final IncidentDAO incidentDAO;
    private final EquipmentDAO equipmentDAO;
    private final UserDAO userDAO;

    public MaintenanceRecordService() {
        this(
                new MaintenanceRecordDAO(),
                new IncidentDAO(),
                new EquipmentDAO(),
                new UserDAO()
        );
    }

    public MaintenanceRecordService(
            MaintenanceRecordDAO maintenanceRecordDAO,
            IncidentDAO incidentDAO,
            EquipmentDAO equipmentDAO,
            UserDAO userDAO
    ) {
        this.maintenanceRecordDAO = Objects.requireNonNull(
                maintenanceRecordDAO,
                "MaintenanceRecordDAO must not be null."
        );

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

    public List<MaintenanceRecord> getAllRecords() {
        try {
            return maintenanceRecordDAO.findAll();
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load maintenance records.",
                    exception
            );
        }
    }

    public MaintenanceRecord getRecordById(long recordId) {
        validateId(recordId, "Maintenance record ID");

        try {
            return maintenanceRecordDAO.findById(recordId)
                    .orElseThrow(() -> new NotFoundException(
                            "Maintenance record was not found: " + recordId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load maintenance record with ID: "
                            + recordId,
                    exception
            );
        }
    }

    public List<MaintenanceRecord> getRecordsByIncident(long incidentId) {
        validateIncidentExists(incidentId);

        try {
            return maintenanceRecordDAO.findByIncidentId(incidentId);
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load maintenance records for incident ID: "
                            + incidentId,
                    exception
            );
        }
    }

    public List<MaintenanceRecord> getRecordsByEquipment(long equipmentId) {
        validateEquipmentExists(equipmentId);

        try {
            return maintenanceRecordDAO.findByEquipmentId(equipmentId);
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load maintenance history for equipment ID: "
                            + equipmentId,
                    exception
            );
        }
    }

    public List<MaintenanceRecord> getRecordsByTechnician(
            long technicianId
    ) {
        validateTechnician(technicianId, false);

        try {
            return maintenanceRecordDAO.findByTechnicianId(technicianId);
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load maintenance records for technician ID: "
                            + technicianId,
                    exception
            );
        }
    }

    public MaintenanceRecord createRecord(MaintenanceRecord record) {
        Objects.requireNonNull(
                record,
                "Maintenance record must not be null."
        );

        normalizeRecord(record);
        validateRecordFields(record);
        validateRecordRelations(record);

        try {
            long generatedId = maintenanceRecordDAO.insert(record);

            return maintenanceRecordDAO.findById(generatedId)
                    .orElseThrow(() -> new ServiceException(
                            "Maintenance record was created, "
                                    + "but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to create maintenance record.",
                    exception
            );
        }
    }

    public MaintenanceRecord updateRecord(MaintenanceRecord record) {
        Objects.requireNonNull(
                record,
                "Maintenance record must not be null."
        );

        if (record.getId() == null) {
            throw new ValidationException(
                    "Maintenance record ID is required for update."
            );
        }

        validateId(record.getId(), "Maintenance record ID");

        MaintenanceRecord existingRecord =
                getRecordById(record.getId());

        normalizeRecord(record);
        validateRecordFields(record);
        validateRecordRelations(record);

        record.setPerformedAt(existingRecord.getPerformedAt());

        try {
            boolean updated = maintenanceRecordDAO.update(record);

            if (!updated) {
                throw new NotFoundException(
                        "Maintenance record was not found: "
                                + record.getId()
                );
            }

            return maintenanceRecordDAO.findById(record.getId())
                    .orElseThrow(() -> new ServiceException(
                            "Maintenance record was updated, "
                                    + "but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to update maintenance record with ID: "
                            + record.getId(),
                    exception
            );
        }
    }

    private void validateRecordRelations(MaintenanceRecord record) {
        Incident incident =
                validateIncidentExists(record.getIncidentId());

        validateEquipmentExists(record.getEquipmentId());

        validateTechnician(
                record.getTechnicianId(),
                true
        );

        if (!record.getEquipmentId().equals(
                incident.getEquipmentId()
        )) {
            throw new ValidationException(
                    "Maintenance equipment does not match "
                            + "the equipment assigned to incident "
                            + incident.getId()
                            + "."
            );
        }

        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new ValidationException(
                    "Maintenance work can only be added or changed "
                            + "for an incident with IN_PROGRESS status."
            );
        }

        Long assignedTechnicianId =
                incident.getAssignedTechnicianId();

        if (assignedTechnicianId == null) {
            throw new ValidationException(
                    "The incident must have an assigned technician "
                            + "before maintenance work can be recorded."
            );
        }

        if (!assignedTechnicianId.equals(
                record.getTechnicianId()
        )) {
            throw new ValidationException(
                    "Maintenance technician does not match "
                            + "the technician assigned to incident "
                            + incident.getId()
                            + "."
            );
        }
    }

    private Incident validateIncidentExists(Long incidentId) {
        validateId(incidentId, "Incident ID");

        try {
            return incidentDAO.findById(incidentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Incident was not found: " + incidentId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate incident ID: " + incidentId,
                    exception
            );
        }
    }

    private void validateEquipmentExists(Long equipmentId) {
        validateId(equipmentId, "Equipment ID");

        try {
            equipmentDAO.findById(equipmentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Equipment was not found: " + equipmentId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate equipment ID: " + equipmentId,
                    exception
            );
        }
    }

    private void validateTechnician(
            Long technicianId,
            boolean activeRequired
    ) {
        validateId(technicianId, "Technician ID");

        try {
            User technician = userDAO.findById(technicianId)
                    .orElseThrow(() -> new NotFoundException(
                            "Technician was not found: " + technicianId
                    ));

            if (technician.getRole() != UserRole.TECHNICIAN) {
                throw new ValidationException(
                        "Selected user is not a technician: "
                                + technicianId
                );
            }

            if (activeRequired && !technician.isActive()) {
                throw new ValidationException(
                        "Selected technician is inactive: "
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

    private void validateRecordFields(MaintenanceRecord record) {
        validateId(record.getIncidentId(), "Incident ID");
        validateId(record.getEquipmentId(), "Equipment ID");
        validateId(record.getTechnicianId(), "Technician ID");

        requireText(
                record.getWorkDescription(),
                "Work description"
        );

        if (record.getResult() == null) {
            throw new ValidationException(
                    "Maintenance result must not be null."
            );
        }
    }

    private void normalizeRecord(MaintenanceRecord record) {
        record.setWorkDescription(
                trimRequired(record.getWorkDescription())
        );

        record.setReplacedComponents(
                trimToNull(record.getReplacedComponents())
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