package com.chebo16.metroit;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.enums.EquipmentStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EquipmentDAOTest {

    public static void main(String[] args) {
        EquipmentDAO equipmentDAO = new EquipmentDAO();

        Long createdEquipmentId = null;
        boolean equipmentDeleted = false;

        try {
            System.out.println(
                    "Testing EquipmentDAO CRUD operations..."
            );
            System.out.println();

            List<Equipment> equipmentBeforeInsert =
                    equipmentDAO.findAll();

            int countBeforeInsert =
                    equipmentBeforeInsert.size();

            System.out.println(
                    "1. findAll() completed successfully."
            );
            System.out.println(
                    "Equipment records before insert: "
                            + countBeforeInsert
            );
            System.out.println();

            String uniqueSuffix =
                    String.valueOf(System.currentTimeMillis());

            Equipment testEquipment = new Equipment(
                    "DAO-TEST-" + uniqueSuffix,
                    "Temporary DAO Test Device",
                    "Test device",
                    "Test Manufacturer",
                    "Test Model",
                    "DAO-SERIAL-" + uniqueSuffix,
                    "192.0.2.200",
                    "Temporary equipment created by EquipmentDAOTest."
            );

            createdEquipmentId =
                    equipmentDAO.insert(testEquipment);

            requireCondition(
                    createdEquipmentId != null
                            && createdEquipmentId > 0,
                    "The generated equipment ID is invalid."
            );

            final long equipmentId =
                    createdEquipmentId;

            System.out.println(
                    "2. insert() completed successfully."
            );
            System.out.println(
                    "Generated equipment ID: " + equipmentId
            );
            System.out.println();

            Optional<Equipment> insertedEquipmentOptional =
                    equipmentDAO.findById(equipmentId);

            requireCondition(
                    insertedEquipmentOptional.isPresent(),
                    "Inserted equipment was not found by ID."
            );

            Equipment insertedEquipment =
                    insertedEquipmentOptional.get();

            System.out.println(
                    "3. findById() completed successfully."
            );
            System.out.println();

            insertedEquipment.setName(
                    "Updated DAO Test Device"
            );
            insertedEquipment.setStatus(
                    EquipmentStatus.IN_REPAIR
            );
            insertedEquipment.setNotes(
                    "Temporary equipment updated by EquipmentDAOTest."
            );

            boolean updated =
                    equipmentDAO.update(insertedEquipment);

            requireCondition(
                    updated,
                    "Equipment update returned false."
            );

            Optional<Equipment> updatedEquipmentOptional =
                    equipmentDAO.findById(equipmentId);

            requireCondition(
                    updatedEquipmentOptional.isPresent(),
                    "Updated equipment was not found."
            );

            Equipment updatedEquipment =
                    updatedEquipmentOptional.get();

            requireCondition(
                    "Updated DAO Test Device".equals(
                            updatedEquipment.getName()
                    ),
                    "Equipment name was not updated."
            );

            requireCondition(
                    updatedEquipment.getStatus()
                            == EquipmentStatus.IN_REPAIR,
                    "Equipment status was not updated."
            );

            System.out.println(
                    "4. update() completed successfully."
            );
            System.out.println();

            equipmentDeleted =
                    equipmentDAO.delete(equipmentId);

            requireCondition(
                    equipmentDeleted,
                    "Equipment deletion returned false."
            );

            Optional<Equipment> deletedEquipment =
                    equipmentDAO.findById(equipmentId);

            requireCondition(
                    deletedEquipment.isEmpty(),
                    "Equipment still exists after deletion."
            );

            System.out.println(
                    "5. delete() completed successfully."
            );
            System.out.println(
                    "Deleted equipment ID: " + equipmentId
            );
            System.out.println();

            List<Equipment> equipmentAfterDelete =
                    equipmentDAO.findAll();

            int countAfterDelete =
                    equipmentAfterDelete.size();

            requireCondition(
                    countAfterDelete == countBeforeInsert,
                    "Equipment count changed after the CRUD test. "
                            + "Before: "
                            + countBeforeInsert
                            + ", after: "
                            + countAfterDelete
            );

            System.out.println(
                    "Equipment records after deletion: "
                            + countAfterDelete
            );
            System.out.println();

            System.out.println(
                    "All EquipmentDAO CRUD operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {
            System.err.println(
                    "EquipmentDAO SQL test failed."
            );
            System.err.println(
                    "SQL error code: "
                            + exception.getErrorCode()
            );
            System.err.println(
                    "SQL state: "
                            + exception.getSQLState()
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw new IllegalStateException(
                    "EquipmentDAO test failed because of a database error.",
                    exception
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "EquipmentDAO test failed."
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw exception;

        } finally {
            if (createdEquipmentId != null
                    && !equipmentDeleted) {

                try {
                    boolean cleanupSuccessful =
                            equipmentDAO.delete(
                                    createdEquipmentId
                            );

                    if (cleanupSuccessful) {
                        System.out.println();
                        System.out.println(
                                "Temporary equipment was "
                                        + "removed during cleanup."
                        );
                    }

                } catch (SQLException cleanupException) {
                    System.err.println();
                    System.err.println(
                            "Automatic cleanup failed."
                    );
                    System.err.println(
                            "Temporary equipment ID: "
                                    + createdEquipmentId
                    );
                    System.err.println(
                            "Reason: "
                                    + cleanupException.getMessage()
                    );
                }
            }
        }
    }

    private static void requireCondition(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}