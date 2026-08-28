package com.chebo16.metroit.service;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.enums.EquipmentStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class EquipmentService {

    private final EquipmentDAO equipmentDAO;

    public EquipmentService() {
        this(new EquipmentDAO());
    }

    public EquipmentService(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = Objects.requireNonNull(
                equipmentDAO,
                "EquipmentDAO must not be null."
        );
    }

    public List<Equipment> getAllEquipment() {
        try {
            return equipmentDAO.findAll();
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load equipment.",
                    exception
            );
        }
    }

    public Equipment getEquipmentById(long equipmentId) {
        validateId(equipmentId);

        try {
            return equipmentDAO.findById(equipmentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Equipment was not found: " + equipmentId
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load equipment with ID: " + equipmentId,
                    exception
            );
        }
    }

    public Equipment createEquipment(Equipment equipment) {
        Objects.requireNonNull(
                equipment,
                "Equipment must not be null."
        );

        normalizeEquipment(equipment);

        if (equipment.getStatus() == null) {
            equipment.setStatus(EquipmentStatus.ACTIVE);
        }

        validateEquipment(equipment);
        validateUniqueFields(equipment, null);

        try {
            long generatedId = equipmentDAO.insert(equipment);

            return equipmentDAO.findById(generatedId)
                    .orElseThrow(() -> new ServiceException(
                            "Equipment was created, but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to create equipment.",
                    exception
            );
        }
    }

    public Equipment updateEquipment(Equipment equipment) {
        Objects.requireNonNull(
                equipment,
                "Equipment must not be null."
        );

        if (equipment.getId() == null) {
            throw new ValidationException(
                    "Equipment ID is required for update."
            );
        }

        validateId(equipment.getId());
        getEquipmentById(equipment.getId());

        normalizeEquipment(equipment);
        validateEquipment(equipment);
        validateUniqueFields(equipment, equipment.getId());

        try {
            boolean updated = equipmentDAO.update(equipment);

            if (!updated) {
                throw new NotFoundException(
                        "Equipment was not found: " + equipment.getId()
                );
            }

            return equipmentDAO.findById(equipment.getId())
                    .orElseThrow(() -> new ServiceException(
                            "Equipment was updated, but could not be reloaded."
                    ));
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to update equipment with ID: "
                            + equipment.getId(),
                    exception
            );
        }
    }

    public Equipment changeStatus(
            long equipmentId,
            EquipmentStatus newStatus
    ) {
        Objects.requireNonNull(
                newStatus,
                "Equipment status must not be null."
        );

        Equipment equipment = getEquipmentById(equipmentId);
        equipment.setStatus(newStatus);

        return updateEquipment(equipment);
    }

    public Equipment decommissionEquipment(long equipmentId) {
        return changeStatus(
                equipmentId,
                EquipmentStatus.DECOMMISSIONED
        );
    }

    private void validateUniqueFields(
            Equipment equipment,
            Long currentEquipmentId
    ) {
        List<Equipment> existingEquipment;

        try {
            existingEquipment = equipmentDAO.findAll();
        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate equipment uniqueness.",
                    exception
            );
        }

        for (Equipment existing : existingEquipment) {
            if (currentEquipmentId != null
                    && currentEquipmentId.equals(existing.getId())) {
                continue;
            }

            if (equipment.getInventoryNumber().equalsIgnoreCase(
                    existing.getInventoryNumber()
            )) {
                throw new ValidationException(
                        "Inventory number already exists: "
                                + equipment.getInventoryNumber()
                );
            }

            String serialNumber = equipment.getSerialNumber();
            String existingSerialNumber = existing.getSerialNumber();

            if (serialNumber != null
                    && existingSerialNumber != null
                    && serialNumber.equalsIgnoreCase(existingSerialNumber)) {

                throw new ValidationException(
                        "Serial number already exists: " + serialNumber
                );
            }
        }
    }

    private void validateEquipment(Equipment equipment) {
        requireText(
                equipment.getInventoryNumber(),
                "Inventory number"
        );

        requireText(
                equipment.getName(),
                "Equipment name"
        );

        requireText(
                equipment.getType(),
                "Equipment type"
        );

        Objects.requireNonNull(
                equipment.getStatus(),
                "Equipment status must not be null."
        );

        validateMaximumLength(
                equipment.getInventoryNumber(),
                50,
                "Inventory number"
        );

        validateMaximumLength(
                equipment.getName(),
                100,
                "Equipment name"
        );

        validateMaximumLength(
                equipment.getType(),
                50,
                "Equipment type"
        );

        validateMaximumLength(
                equipment.getManufacturer(),
                100,
                "Manufacturer"
        );

        validateMaximumLength(
                equipment.getModel(),
                100,
                "Model"
        );

        validateMaximumLength(
                equipment.getSerialNumber(),
                100,
                "Serial number"
        );

        validateMaximumLength(
                equipment.getIpAddress(),
                45,
                "IP address"
        );
    }

    private void normalizeEquipment(Equipment equipment) {
        equipment.setInventoryNumber(
                trimRequired(equipment.getInventoryNumber())
        );

        equipment.setName(
                trimRequired(equipment.getName())
        );

        equipment.setType(
                trimRequired(equipment.getType())
        );

        equipment.setManufacturer(
                trimToNull(equipment.getManufacturer())
        );

        equipment.setModel(
                trimToNull(equipment.getModel())
        );

        equipment.setSerialNumber(
                trimToNull(equipment.getSerialNumber())
        );

        equipment.setIpAddress(
                trimToNull(equipment.getIpAddress())
        );

        equipment.setNotes(
                trimToNull(equipment.getNotes())
        );
    }

    private void validateId(long equipmentId) {
        if (equipmentId <= 0) {
            throw new ValidationException(
                    "Equipment ID must be greater than zero."
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