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

            // PREPARATION: SELECT RELATED RECORDS

            List<User> technicians = userDAO.findAll()
                    .stream()
                    .filter(User::isActive)
                    .filter(user ->
                            user.getRole()
                                    == UserRole.TECHNICIAN
                    )
                    .toList();

            if (technicians.isEmpty()) {
                throw new IllegalStateException(
                        "No active TECHNICIAN user was found."
                );
            }

            User selectedTechnician =
                    technicians.getFirst();

            List<Incident> incidents =
                    incidentDAO.findAll();

            if (incidents.isEmpty()) {
                throw new IllegalStateException(
                        "No incident records are available "
                                + "for the maintenance test."
                );
            }

            Incident selectedIncident = incidents.stream()
                    .filter(incident ->
                            selectedTechnician.getId().equals(
                                    incident
                                            .getAssignedTechnicianId()
                            )
                    )
                    .findFirst()
                    .orElse(incidents.getFirst());

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

            System.out.println(
                    "Selected incident ID: "
                            + selectedIncident.getId()
            );

            System.out.println(
                    "Selected equipment ID: "
                            + selectedEquipment.getId()
            );

            System.out.println(
                    "Selected technician ID: "
                            + selectedTechnician.getId()
            );

            System.out.println();

            // READ ALL

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

            // CREATE

            MaintenanceRecord testRecord =
                    new MaintenanceRecord(
                            selectedIncident.getId(),
                            selectedEquipment.getId(),
                            selectedTechnician.getId(),
                            "Temporary diagnostic work "
                                    + "performed during "
                                    + "MaintenanceRecordDAOTest.",
                            null,
                            MaintenanceResult.PARTIALLY_COMPLETED
                    );

            createdRecordId =
                    maintenanceRecordDAO.insert(testRecord);

            long recordId =
                    createdRecordId;

            if (recordId <= 0) {
                throw new IllegalStateException(
                        "Generated maintenance record ID "
                                + "is invalid."
                );
            }

            System.out.println(
                    "2. insert() completed successfully."
            );

            System.out.println(
                    "Generated maintenance record ID: "
                            + recordId
            );

            System.out.println();

            // READ BY ID

            Optional<MaintenanceRecord> insertedRecordOptional =
                    maintenanceRecordDAO.findById(recordId);

            if (insertedRecordOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Inserted maintenance record "
                                + "was not found by ID."
                );
            }

            MaintenanceRecord insertedRecord =
                    insertedRecordOptional.get();

            if (insertedRecord.getResult()
                    != MaintenanceResult
                    .PARTIALLY_COMPLETED) {

                throw new IllegalStateException(
                        "Inserted maintenance result "
                                + "is incorrect."
                );
            }

            if (insertedRecord.getPerformedAt() == null) {
                throw new IllegalStateException(
                        "performedAt was not generated "
                                + "by MySQL."
                );
            }

            System.out.println(
                    "3. findById() completed successfully."
            );

            System.out.println("Inserted maintenance record:");
            System.out.println(insertedRecord);
            System.out.println();

            // READ BY INCIDENT ID

            List<MaintenanceRecord> incidentRecords =
                    maintenanceRecordDAO.findByIncidentId(
                            selectedIncident.getId()
                    );

            if (!containsRecord(
                    incidentRecords,
                    recordId
            )) {
                throw new IllegalStateException(
                        "Inserted record was not found "
                                + "by incident ID."
                );
            }

            System.out.println(
                    "4. findByIncidentId() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for incident: "
                            + incidentRecords.size()
            );

            System.out.println();

            // READ BY EQUIPMENT ID

            List<MaintenanceRecord> equipmentRecords =
                    maintenanceRecordDAO.findByEquipmentId(
                            selectedEquipment.getId()
                    );

            if (!containsRecord(
                    equipmentRecords,
                    recordId
            )) {
                throw new IllegalStateException(
                        "Inserted record was not found "
                                + "by equipment ID."
                );
            }

            System.out.println(
                    "5. findByEquipmentId() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for equipment: "
                            + equipmentRecords.size()
            );

            System.out.println();

            // READ BY TECHNICIAN ID

            List<MaintenanceRecord> technicianRecords =
                    maintenanceRecordDAO.findByTechnicianId(
                            selectedTechnician.getId()
                    );

            if (!containsRecord(
                    technicianRecords,
                    recordId
            )) {
                throw new IllegalStateException(
                        "Inserted record was not found "
                                + "by technician ID."
                );
            }

            System.out.println(
                    "6. findByTechnicianId() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for technician: "
                            + technicianRecords.size()
            );

            System.out.println();

            // UPDATE

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

            if (!updated) {
                throw new IllegalStateException(
                        "Maintenance record update "
                                + "returned false."
                );
            }

            MaintenanceRecord updatedRecord =
                    maintenanceRecordDAO.findById(recordId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated maintenance "
                                                    + "record was "
                                                    + "not found."
                                    )
                            );

            if (updatedRecord.getResult()
                    != MaintenanceResult.SUCCESS) {

                throw new IllegalStateException(
                        "Maintenance result "
                                + "was not updated."
                );
            }

            if (!"Temporary CAT6 Ethernet cable".equals(
                    updatedRecord.getReplacedComponents()
            )) {
                throw new IllegalStateException(
                        "Replaced components "
                                + "were not updated."
                );
            }

            if (!updatedRecord
                    .getWorkDescription()
                    .startsWith(
                            "Diagnostic work was completed"
                    )) {

                throw new IllegalStateException(
                        "Work description "
                                + "was not updated."
                );
            }

            System.out.println(
                    "7. update() completed successfully."
            );

            System.out.println("Updated maintenance record:");
            System.out.println(updatedRecord);
            System.out.println();

            // DELETE TEMPORARY RECORD

            recordDeleted =
                    maintenanceRecordDAO.delete(recordId);

            if (!recordDeleted) {
                throw new IllegalStateException(
                        "Maintenance record deletion "
                                + "returned false."
                );
            }

            if (maintenanceRecordDAO
                    .findById(recordId)
                    .isPresent()) {

                throw new IllegalStateException(
                        "Maintenance record still exists "
                                + "after deletion."
                );
            }

            System.out.println(
                    "8. delete() completed successfully."
            );

            System.out.println(
                    "Deleted maintenance record ID: "
                            + recordId
            );

            System.out.println();

            // FINAL COUNT CHECK

            int countAfterDelete =
                    maintenanceRecordDAO
                            .findAll()
                            .size();

            if (countAfterDelete != countBeforeInsert) {
                throw new IllegalStateException(
                        "Maintenance record count changed "
                                + "after the test. Before: "
                                + countBeforeInsert
                                + ", after: "
                                + countAfterDelete
                );
            }

            System.out.println(
                    "Maintenance records after deletion: "
                            + countAfterDelete
            );

            System.out.println();

            System.out.println(
                    "All MaintenanceRecordDAO operations "
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

        } catch (RuntimeException exception) {

            System.err.println(
                    "MaintenanceRecordDAO test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

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
}
