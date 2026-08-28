package com.chebo16.metroit;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.MaintenanceRecordDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MaintenanceRecordDAOTest {

    public static void main(String[] args) {
        MaintenanceRecordDAO maintenanceRecordDAO =
                new MaintenanceRecordDAO();

        IncidentDAO incidentDAO =
                new IncidentDAO();

        EquipmentDAO equipmentDAO =
                new EquipmentDAO();

        UserDAO userDAO =
                new UserDAO();

        Long createdRecordId = null;
        boolean recordDeleted = false;

        try {
            System.out.println(
                    "Testing MaintenanceRecordDAO operations..."
            );
            System.out.println();

            List<User> technicians = userDAO.findAll()
                    .stream()
                    .filter(User::isActive)
                    .filter(user ->
                            user.getRole()
                                    == UserRole.TECHNICIAN
                    )
                    .toList();

            requireCondition(
                    !technicians.isEmpty(),
                    "No active TECHNICIAN user was found."
            );

            User selectedTechnician =
                    technicians.getFirst();

            List<Incident> incidents =
                    incidentDAO.findAll();

            requireCondition(
                    !incidents.isEmpty(),
                    "No incident records are available "
                            + "for the maintenance test."
            );

            Incident selectedIncident = incidents.stream()
                    .filter(incident ->
                            selectedTechnician.getId().equals(
                                    incident.getAssignedTechnicianId()
                            )
                    )
                    .findFirst()
                    .orElse(incidents.getFirst());

            requireCondition(
                    selectedIncident.getEquipmentId() != null,
                    "Selected incident has no equipment ID."
            );

            Equipment selectedEquipment =
                    equipmentDAO.findById(
                                    selectedIncident.getEquipmentId()
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Equipment related to the "
                                                    + "selected incident "
                                                    + "was not found."
                                    )
                            );

            requireCondition(
                    selectedIncident.getId() != null,
                    "Selected incident has no ID."
            );

            requireCondition(
                    selectedEquipment.getId() != null,
                    "Selected equipment has no ID."
            );

            requireCondition(
                    selectedTechnician.getId() != null,
                    "Selected technician has no ID."
            );

            final long incidentId =
                    selectedIncident.getId();

            final long equipmentId =
                    selectedEquipment.getId();

            final long technicianId =
                    selectedTechnician.getId();

            System.out.println(
                    "Selected incident ID: " + incidentId
            );
            System.out.println(
                    "Selected equipment ID: " + equipmentId
            );
            System.out.println(
                    "Selected technician ID: " + technicianId
            );
            System.out.println();

            List<MaintenanceRecord> recordsBeforeInsert =
                    maintenanceRecordDAO.findAll();

            int countBeforeInsert =
                    recordsBeforeInsert.size();

            System.out.println(
                    "1. findAll() completed successfully."
            );
            System.out.println(
                    "Maintenance records before insert: "
                            + countBeforeInsert
            );
            System.out.println();

            MaintenanceRecord testRecord =
                    new MaintenanceRecord(
                            incidentId,
                            equipmentId,
                            technicianId,
                            "Temporary diagnostic work "
                                    + "performed during "
                                    + "MaintenanceRecordDAOTest.",
                            null,
                            MaintenanceResult.PARTIALLY_COMPLETED
                    );

            createdRecordId =
                    maintenanceRecordDAO.insert(testRecord);

            requireCondition(
                    createdRecordId != null
                            && createdRecordId > 0,
                    "Generated maintenance record ID "
                            + "is invalid."
            );

            final long recordId =
                    createdRecordId;

            System.out.println(
                    "2. insert() completed successfully."
            );
            System.out.println(
                    "Generated maintenance record ID: "
                            + recordId
            );
            System.out.println();

            Optional<MaintenanceRecord> insertedRecordOptional =
                    maintenanceRecordDAO.findById(recordId);

            requireCondition(
                    insertedRecordOptional.isPresent(),
                    "Inserted maintenance record "
                            + "was not found by ID."
            );

            MaintenanceRecord insertedRecord =
                    insertedRecordOptional.get();

            requireCondition(
                    insertedRecord.getResult()
                            == MaintenanceResult.PARTIALLY_COMPLETED,
                    "Inserted maintenance result is incorrect."
            );

            requireCondition(
                    insertedRecord.getPerformedAt() != null,
                    "performedAt was not generated by MySQL."
            );

            System.out.println(
                    "3. findById() completed successfully."
            );
            System.out.println();

            List<MaintenanceRecord> incidentRecords =
                    maintenanceRecordDAO.findByIncidentId(
                            incidentId
                    );

            requireCondition(
                    containsRecord(
                            incidentRecords,
                            recordId
                    ),
                    "Inserted record was not found "
                            + "by incident ID."
            );

            System.out.println(
                    "4. findByIncidentId() "
                            + "completed successfully."
            );
            System.out.println(
                    "Records for incident: "
                            + incidentRecords.size()
            );
            System.out.println();

            List<MaintenanceRecord> equipmentRecords =
                    maintenanceRecordDAO.findByEquipmentId(
                            equipmentId
                    );

            requireCondition(
                    containsRecord(
                            equipmentRecords,
                            recordId
                    ),
                    "Inserted record was not found "
                            + "by equipment ID."
            );

            System.out.println(
                    "5. findByEquipmentId() "
                            + "completed successfully."
            );
            System.out.println(
                    "Records for equipment: "
                            + equipmentRecords.size()
            );
            System.out.println();

            List<MaintenanceRecord> technicianRecords =
                    maintenanceRecordDAO.findByTechnicianId(
                            technicianId
                    );

            requireCondition(
                    containsRecord(
                            technicianRecords,
                            recordId
                    ),
                    "Inserted record was not found "
                            + "by technician ID."
            );

            System.out.println(
                    "6. findByTechnicianId() "
                            + "completed successfully."
            );
            System.out.println(
                    "Records for technician: "
                            + technicianRecords.size()
            );
            System.out.println();

            insertedRecord.setWorkDescription(
                    "Diagnostic work was completed "
                            + "and the equipment was tested."
            );

            insertedRecord.setReplacedComponents(
                    "Temporary CAT6 Ethernet cable"
            );

            insertedRecord.setResult(
                    MaintenanceResult.SUCCESS
            );

            boolean updated =
                    maintenanceRecordDAO.update(
                            insertedRecord
                    );

            requireCondition(
                    updated,
                    "Maintenance record update "
                            + "returned false."
            );

            MaintenanceRecord updatedRecord =
                    maintenanceRecordDAO.findById(recordId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated maintenance "
                                                    + "record was not found."
                                    )
                            );

            requireCondition(
                    updatedRecord.getResult()
                            == MaintenanceResult.SUCCESS,
                    "Maintenance result was not updated."
            );

            requireCondition(
                    "Temporary CAT6 Ethernet cable".equals(
                            updatedRecord.getReplacedComponents()
                    ),
                    "Replaced components were not updated."
            );

            requireCondition(
                    updatedRecord.getWorkDescription()
                            .startsWith(
                                    "Diagnostic work was completed"
                            ),
                    "Work description was not updated."
            );

            requireCondition(
                    updatedRecord.getPerformedAt() != null,
                    "performedAt was lost during update."
            );

            System.out.println(
                    "7. update() completed successfully."
            );
            System.out.println();

            recordDeleted =
                    maintenanceRecordDAO.delete(recordId);

            requireCondition(
                    recordDeleted,
                    "Maintenance record deletion returned false."
            );

            requireCondition(
                    maintenanceRecordDAO
                            .findById(recordId)
                            .isEmpty(),
                    "Maintenance record still exists "
                            + "after deletion."
            );

            System.out.println(
                    "8. delete() completed successfully."
            );
            System.out.println(
                    "Deleted maintenance record ID: "
                            + recordId
            );
            System.out.println();

            int countAfterDelete =
                    maintenanceRecordDAO
                            .findAll()
                            .size();

            requireCondition(
                    countAfterDelete == countBeforeInsert,
                    "Maintenance record count changed "
                            + "after the test. Before: "
                            + countBeforeInsert
                            + ", after: "
                            + countAfterDelete
            );

            System.out.println(
                    "Maintenance records after deletion: "
                            + countAfterDelete
            );
            System.out.println();

            System.out.println(
                    "9. All MaintenanceRecordDAO operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {
            System.err.println(
                    "MaintenanceRecordDAO SQL test failed."
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
                    "MaintenanceRecordDAO test failed "
                            + "because of a database error.",
                    exception
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "MaintenanceRecordDAO test failed."
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw exception;

        } finally {
            if (createdRecordId != null
                    && !recordDeleted) {

                try {
                    boolean cleanupSuccessful =
                            maintenanceRecordDAO.delete(
                                    createdRecordId
                            );

                    if (cleanupSuccessful) {
                        System.out.println();
                        System.out.println(
                                "Temporary maintenance record "
                                        + "was removed during "
                                        + "automatic cleanup."
                        );
                    }

                } catch (SQLException cleanupException) {
                    System.err.println();
                    System.err.println(
                            "Automatic maintenance record "
                                    + "cleanup failed."
                    );
                    System.err.println(
                            "Temporary record ID: "
                                    + createdRecordId
                    );
                    System.err.println(
                            "Reason: "
                                    + cleanupException.getMessage()
                    );
                }
            }
        }
    }

    private static boolean containsRecord(
            List<MaintenanceRecord> records,
            long recordId
    ) {
        for (MaintenanceRecord record : records) {
            if (record.getId() != null
                    && record.getId() == recordId) {
                return true;
            }
        }

        return false;
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