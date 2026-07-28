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
            System.out.println("Testing EquipmentDAO CRUD operations...");
            System.out.println();

            // READ ALL

            List<Equipment> equipmentBeforeInsert =
                    equipmentDAO.findAll();

            int countBeforeInsert = equipmentBeforeInsert.size();

            System.out.println("1. findAll() completed successfully.");
            System.out.println(
                    "Equipment records before insert: "
                            + countBeforeInsert
            );
            System.out.println();

            // CREATE

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

            if (createdEquipmentId <= 0) {
                throw new IllegalStateException(
                        "The generated equipment ID is invalid."
                );
            }

            System.out.println("2. insert() completed successfully.");
            System.out.println(
                    "Generated equipment ID: "
                            + createdEquipmentId
            );
            System.out.println();

            // READ BY ID

            Optional<Equipment> insertedEquipmentOptional =
                    equipmentDAO.findById(createdEquipmentId);

            if (insertedEquipmentOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Inserted equipment was not found by ID."
                );
            }

            Equipment insertedEquipment =
                    insertedEquipmentOptional.get();

            System.out.println("3. findById() completed successfully.");
            System.out.println("Inserted equipment:");
            System.out.println(insertedEquipment);
            System.out.println();

            // UPDATE

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

            if (!updated) {
                throw new IllegalStateException(
                        "Equipment update returned false."
                );
            }

            Optional<Equipment> updatedEquipmentOptional =
                    equipmentDAO.findById(createdEquipmentId);

            if (updatedEquipmentOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Updated equipment was not found."
                );
            }

            Equipment updatedEquipment =
                    updatedEquipmentOptional.get();

            if (!"Updated DAO Test Device".equals(
                    updatedEquipment.getName()
            )) {
                throw new IllegalStateException(
                        "Equipment name was not updated."
                );
            }

            if (updatedEquipment.getStatus()
                    != EquipmentStatus.IN_REPAIR) {

                throw new IllegalStateException(
                        "Equipment status was not updated."
                );
            }

            System.out.println("4. update() completed successfully.");
            System.out.println("Updated equipment:");
            System.out.println(updatedEquipment);
            System.out.println();

            // DELETE

            equipmentDeleted =
                    equipmentDAO.delete(createdEquipmentId);

            if (!equipmentDeleted) {
                throw new IllegalStateException(
                        "Equipment deletion returned false."
                );
            }

            Optional<Equipment> deletedEquipment =
                    equipmentDAO.findById(createdEquipmentId);

            if (deletedEquipment.isPresent()) {
                throw new IllegalStateException(
                        "Equipment still exists after deletion."
                );
            }

            System.out.println("5. delete() completed successfully.");
            System.out.println(
                    "Deleted equipment ID: "
                            + createdEquipmentId
            );
            System.out.println();

            // FINAL COUNT CHECK

            List<Equipment> equipmentAfterDelete =
                    equipmentDAO.findAll();

            int countAfterDelete =
                    equipmentAfterDelete.size();

            if (countAfterDelete != countBeforeInsert) {
                throw new IllegalStateException(
                        "Equipment count changed after the CRUD test. "
                                + "Before: "
                                + countBeforeInsert
                                + ", after: "
                                + countAfterDelete
                );
            }

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

        } catch (RuntimeException exception) {

            System.err.println(
                    "EquipmentDAO test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

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
}
