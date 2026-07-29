package com.chebo16.metroit;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.MaintenanceRecordDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.IncidentService;
import com.chebo16.metroit.service.MaintenanceRecordService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceRecordServiceTest {

    public static void main(String[] args) {

        MaintenanceRecordService maintenanceService =
                new MaintenanceRecordService();

        IncidentService incidentService =
                new IncidentService();

        MaintenanceRecordDAO maintenanceRecordDAO =
                new MaintenanceRecordDAO();

        IncidentDAO incidentDAO =
                new IncidentDAO();

        EquipmentDAO equipmentDAO =
                new EquipmentDAO();

        UserDAO userDAO =
                new UserDAO();

        Long createdIncidentId = null;
        Long createdRecordId = null;

        boolean recordDeleted = false;
        boolean incidentDeleted = false;

        try {
            System.out.println(
                    "Testing MaintenanceRecordService operations..."
            );

            System.out.println();

            // PREPARATION: SELECT RELATED RECORDS

            List<Equipment> equipmentList =
                    equipmentDAO.findAll();

            if (equipmentList.size() < 2) {
                throw new IllegalStateException(
                        "At least two equipment records "
                                + "are required for this test."
                );
            }

            Equipment selectedEquipment =
                    equipmentList.getFirst();

            Equipment differentEquipment =
                    equipmentList.stream()
                            .filter(equipment ->
                                    !equipment.getId().equals(
                                            selectedEquipment.getId()
                                    )
                            )
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "A second equipment record "
                                                    + "was not found."
                                    )
                            );

            List<User> users =
                    userDAO.findAll();

            User administrator = users.stream()
                    .filter(User::isActive)
                    .filter(user ->
                            user.getRole() == UserRole.ADMIN
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No active ADMIN user was found."
                            )
                    );

            List<User> technicians = users.stream()
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

            final long equipmentId =
                    selectedEquipment.getId();

            final long differentEquipmentId =
                    differentEquipment.getId();

            final long administratorId =
                    administrator.getId();

            final long technicianId =
                    selectedTechnician.getId();

            System.out.println(
                    "Selected equipment ID: "
                            + equipmentId
            );

            System.out.println(
                    "Different equipment ID: "
                            + differentEquipmentId
            );

            System.out.println(
                    "Selected administrator ID: "
                            + administratorId
            );

            System.out.println(
                    "Selected technician ID: "
                            + technicianId
            );

            System.out.println();

            // 1. READ ALL RECORDS

            int recordsBeforeCreate =
                    maintenanceService
                            .getAllRecords()
                            .size();

            int incidentsBeforeCreate =
                    incidentService
                            .getAllIncidents()
                            .size();

            System.out.println(
                    "1. getAllRecords() completed successfully."
            );

            System.out.println(
                    "Maintenance records before create: "
                            + recordsBeforeCreate
            );

            System.out.println(
                    "Incidents before create: "
                            + incidentsBeforeCreate
            );

            System.out.println();

            // 2. CREATE TEMPORARY INCIDENT

            String uniqueSuffix =
                    String.valueOf(
                            System.currentTimeMillis()
                    );

            Incident newIncident = new Incident(
                    "Temporary maintenance service incident "
                            + uniqueSuffix,
                    "Temporary incident created for "
                            + "MaintenanceRecordServiceTest.",
                    IncidentPriority.MEDIUM,
                    equipmentId,
                    administratorId,
                    technicianId
            );

            Incident createdIncident =
                    incidentService.createIncident(
                            newIncident
                    );

            createdIncidentId =
                    createdIncident.getId();

            if (createdIncidentId == null
                    || createdIncidentId <= 0) {

                throw new IllegalStateException(
                        "Generated incident ID is invalid."
                );
            }

            final long incidentId =
                    createdIncidentId;

            requireCondition(
                    createdIncident.getStatus()
                            == IncidentStatus.NEW,
                    "Temporary incident must have NEW status."
            );

            requireCondition(
                    createdIncident
                            .getAssignedTechnicianId() != null
                            && createdIncident
                            .getAssignedTechnicianId()
                            == technicianId,
                    "Technician was not assigned "
                            + "to the temporary incident."
            );

            System.out.println(
                    "2. Temporary incident "
                            + "was created successfully."
            );

            System.out.println(
                    "Generated incident ID: "
                            + incidentId
            );

            System.out.println(
                    "Initial incident status: "
                            + createdIncident.getStatus()
            );

            System.out.println();

            // 3. MAINTENANCE FOR NEW INCIDENT MUST FAIL

            MaintenanceRecord newStatusRecord =
                    new MaintenanceRecord(
                            incidentId,
                            equipmentId,
                            technicianId,
                            "Temporary work for a NEW incident.",
                            null,
                            MaintenanceResult.PARTIALLY_COMPLETED
                    );

            expectValidationException(
                    () -> maintenanceService.createRecord(
                            newStatusRecord
                    ),
                    "Maintenance for a NEW incident "
                            + "was not rejected."
            );

            System.out.println(
                    "3. Maintenance for a NEW incident "
                            + "was rejected successfully."
            );

            System.out.println();

            // 4. MOVE INCIDENT TO IN_PROGRESS

            Incident inProgressIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    );

            requireCondition(
                    inProgressIncident.getStatus()
                            == IncidentStatus.IN_PROGRESS,
                    "Incident was not moved to IN_PROGRESS."
            );

            requireCondition(
                    inProgressIncident.getStartedAt() != null,
                    "Incident startedAt was not generated."
            );

            System.out.println(
                    "4. Incident was moved to IN_PROGRESS "
                            + "successfully."
            );

            System.out.println(
                    "Started at: "
                            + inProgressIncident.getStartedAt()
            );

            System.out.println();

            // 5. CREATE AND NORMALIZE MAINTENANCE RECORD

            MaintenanceRecord newRecord =
                    new MaintenanceRecord(
                            incidentId,
                            equipmentId,
                            technicianId,
                            "  Temporary diagnostic work "
                                    + "was performed.  ",
                            "   ",
                            MaintenanceResult
                                    .PARTIALLY_COMPLETED
                    );

            MaintenanceRecord createdRecord =
                    maintenanceService.createRecord(
                            newRecord
                    );

            createdRecordId =
                    createdRecord.getId();

            if (createdRecordId == null
                    || createdRecordId <= 0) {

                throw new IllegalStateException(
                        "Generated maintenance record ID "
                                + "is invalid."
                );
            }

            final long recordId =
                    createdRecordId;

            requireCondition(
                    "Temporary diagnostic work "
                            .concat("was performed.")
                            .equals(
                                    createdRecord
                                            .getWorkDescription()
                            ),
                    "Work description was not normalized."
            );

            requireCondition(
                    createdRecord
                            .getReplacedComponents() == null,
                    "Blank replaced components "
                            + "were not converted to null."
            );

            requireCondition(
                    createdRecord.getResult()
                            == MaintenanceResult
                            .PARTIALLY_COMPLETED,
                    "Maintenance result is incorrect."
            );

            requireCondition(
                    createdRecord.getPerformedAt() != null,
                    "performedAt was not generated by MySQL."
            );

            System.out.println(
                    "5. createRecord() completed successfully."
            );

            System.out.println(
                    "Generated maintenance record ID: "
                            + recordId
            );

            System.out.println(
                    "Performed at: "
                            + createdRecord.getPerformedAt()
            );

            System.out.println();

            // 6. READ BY ID

            MaintenanceRecord loadedRecord =
                    maintenanceService.getRecordById(
                            recordId
                    );

            requireCondition(
                    loadedRecord.getId() != null
                            && loadedRecord.getId()
                            == recordId,
                    "getRecordById() returned "
                            + "an unexpected record."
            );

            System.out.println(
                    "6. getRecordById() "
                            + "completed successfully."
            );

            System.out.println("Loaded maintenance record:");
            System.out.println(loadedRecord);

            System.out.println();

            // 7. READ BY INCIDENT

            List<MaintenanceRecord> incidentRecords =
                    maintenanceService
                            .getRecordsByIncident(
                                    incidentId
                            );

            requireCondition(
                    containsRecord(
                            incidentRecords,
                            recordId
                    ),
                    "Created record was not found "
                            + "by incident ID."
            );

            System.out.println(
                    "7. getRecordsByIncident() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for temporary incident: "
                            + incidentRecords.size()
            );

            System.out.println();

            // 8. READ BY EQUIPMENT

            List<MaintenanceRecord> equipmentRecords =
                    maintenanceService
                            .getRecordsByEquipment(
                                    equipmentId
                            );

            requireCondition(
                    containsRecord(
                            equipmentRecords,
                            recordId
                    ),
                    "Created record was not found "
                            + "by equipment ID."
            );

            System.out.println(
                    "8. getRecordsByEquipment() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for equipment: "
                            + equipmentRecords.size()
            );

            System.out.println();

            // 9. READ BY TECHNICIAN

            List<MaintenanceRecord> technicianRecords =
                    maintenanceService
                            .getRecordsByTechnician(
                                    technicianId
                            );

            requireCondition(
                    containsRecord(
                            technicianRecords,
                            recordId
                    ),
                    "Created record was not found "
                            + "by technician ID."
            );

            System.out.println(
                    "9. getRecordsByTechnician() "
                            + "completed successfully."
            );

            System.out.println(
                    "Records for technician: "
                            + technicianRecords.size()
            );

            System.out.println();

            // 10. EQUIPMENT MISMATCH VALIDATION

            MaintenanceRecord wrongEquipmentRecord =
                    new MaintenanceRecord(
                            incidentId,
                            differentEquipmentId,
                            technicianId,
                            "Work with incorrect equipment.",
                            null,
                            MaintenanceResult.FAILED
                    );

            expectValidationException(
                    () -> maintenanceService.createRecord(
                            wrongEquipmentRecord
                    ),
                    "Equipment mismatch "
                            + "was not rejected."
            );

            System.out.println(
                    "10. Equipment mismatch "
                            + "was rejected successfully."
            );

            System.out.println();

            // 11. ADMIN CANNOT BE MAINTENANCE TECHNICIAN

            MaintenanceRecord administratorRecord =
                    new MaintenanceRecord(
                            incidentId,
                            equipmentId,
                            administratorId,
                            "Work incorrectly assigned "
                                    + "to an administrator.",
                            null,
                            MaintenanceResult.FAILED
                    );

            expectValidationException(
                    () -> maintenanceService.createRecord(
                            administratorRecord
                    ),
                    "ADMIN as maintenance technician "
                            + "was not rejected."
            );

            System.out.println(
                    "11. ADMIN as maintenance technician "
                            + "was rejected successfully."
            );

            System.out.println();

            // 12. DIFFERENT TECHNICIAN VALIDATION

            if (technicians.size() > 1) {

                User differentTechnician =
                        technicians.get(1);

                final long differentTechnicianId =
                        differentTechnician.getId();

                MaintenanceRecord wrongTechnicianRecord =
                        new MaintenanceRecord(
                                incidentId,
                                equipmentId,
                                differentTechnicianId,
                                "Work performed by a technician "
                                        + "not assigned to the incident.",
                                null,
                                MaintenanceResult.FAILED
                        );

                expectValidationException(
                        () -> maintenanceService.createRecord(
                                wrongTechnicianRecord
                        ),
                        "Technician mismatch "
                                + "was not rejected."
                );

                System.out.println(
                        "12. Technician mismatch "
                                + "was rejected successfully."
                );

            } else {
                System.out.println(
                        "12. Technician mismatch test "
                                + "was skipped because only one "
                                + "active technician exists."
                );
            }

            System.out.println();

            // 13. UPDATE RECORD

            LocalDateTime originalPerformedAt =
                    loadedRecord.getPerformedAt();

            loadedRecord.setWorkDescription(
                    "  Diagnostic work was completed "
                            + "and the equipment was tested.  "
            );

            loadedRecord.setReplacedComponents(
                    "  Temporary CAT6 Ethernet cable  "
            );

            loadedRecord.setResult(
                    MaintenanceResult.SUCCESS
            );

            MaintenanceRecord updatedRecord =
                    maintenanceService.updateRecord(
                            loadedRecord
                    );

            requireCondition(
                    "Diagnostic work was completed "
                            .concat(
                                    "and the equipment was tested."
                            )
                            .equals(
                                    updatedRecord
                                            .getWorkDescription()
                            ),
                    "Work description was not updated "
                            + "or normalized."
            );

            requireCondition(
                    "Temporary CAT6 Ethernet cable".equals(
                            updatedRecord
                                    .getReplacedComponents()
                    ),
                    "Replaced components were not updated "
                            + "or normalized."
            );

            requireCondition(
                    updatedRecord.getResult()
                            == MaintenanceResult.SUCCESS,
                    "Maintenance result was not updated."
            );

            requireCondition(
                    originalPerformedAt.equals(
                            updatedRecord.getPerformedAt()
                    ),
                    "performedAt changed during update."
            );

            System.out.println(
                    "13. updateRecord() "
                            + "completed successfully."
            );

            System.out.println("Updated maintenance record:");
            System.out.println(updatedRecord);

            System.out.println();

            // 14. INVALID RECORD ID

            expectValidationException(
                    () -> maintenanceService
                            .getRecordById(0),
                    "Invalid maintenance record ID "
                            + "was not rejected."
            );

            System.out.println(
                    "14. Invalid maintenance record ID "
                            + "was rejected successfully."
            );

            System.out.println();

            // 15. MISSING RECORD

            expectNotFoundException(
                    () -> maintenanceService.getRecordById(
                            Long.MAX_VALUE
                    ),
                    "Missing maintenance record "
                            + "did not produce NotFoundException."
            );

            System.out.println(
                    "15. Missing maintenance record produced "
                            + "NotFoundException successfully."
            );

            System.out.println();

            // 16. RESOLVE AND CLOSE INCIDENT

            String solutionDescription =
                    "The temporary incident was diagnosed, "
                            + "repaired and tested successfully.";

            Incident resolvedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            solutionDescription
                    );

            requireCondition(
                    resolvedIncident.getStatus()
                            == IncidentStatus.RESOLVED,
                    "Incident was not resolved."
            );

            Incident closedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.CLOSED,
                            solutionDescription
                    );

            requireCondition(
                    closedIncident.getStatus()
                            == IncidentStatus.CLOSED,
                    "Incident was not closed."
            );

            System.out.println(
                    "16. Temporary incident was resolved "
                            + "and closed successfully."
            );

            System.out.println(
                    "Closed at: "
                            + closedIncident.getClosedAt()
            );

            System.out.println();

            // 17. CLOSED INCIDENT RECORD CANNOT BE UPDATED

            updatedRecord.setWorkDescription(
                    "Attempted update after incident closure."
            );

            expectValidationException(
                    () -> maintenanceService.updateRecord(
                            updatedRecord
                    ),
                    "Maintenance update for a closed incident "
                            + "was not rejected."
            );

            System.out.println(
                    "17. Maintenance update for a closed incident "
                            + "was rejected successfully."
            );

            System.out.println();

            // 18. CLEANUP MAINTENANCE RECORD

            recordDeleted =
                    maintenanceRecordDAO.delete(recordId);

            requireCondition(
                    recordDeleted,
                    "Temporary maintenance record cleanup "
                            + "returned false."
            );

            expectNotFoundException(
                    () -> maintenanceService.getRecordById(
                            recordId
                    ),
                    "Maintenance record still exists "
                            + "after cleanup."
            );

            System.out.println(
                    "18. Temporary maintenance record "
                            + "was deleted successfully."
            );

            System.out.println(
                    "Deleted maintenance record ID: "
                            + recordId
            );

            System.out.println();

            // 19. CLEANUP INCIDENT

            incidentDeleted =
                    incidentDAO.delete(incidentId);

            requireCondition(
                    incidentDeleted,
                    "Temporary incident cleanup "
                            + "returned false."
            );

            expectNotFoundException(
                    () -> incidentService.getIncidentById(
                            incidentId
                    ),
                    "Temporary incident still exists "
                            + "after cleanup."
            );

            System.out.println(
                    "19. Temporary incident "
                            + "was deleted successfully."
            );

            System.out.println(
                    "Deleted incident ID: "
                            + incidentId
            );

            System.out.println();

            // 20. FINAL COUNT CHECK

            int recordsAfterCleanup =
                    maintenanceService
                            .getAllRecords()
                            .size();

            int incidentsAfterCleanup =
                    incidentService
                            .getAllIncidents()
                            .size();

            requireCondition(
                    recordsAfterCleanup
                            == recordsBeforeCreate,
                    "Maintenance record count changed "
                            + "after the test. Before: "
                            + recordsBeforeCreate
                            + ", after: "
                            + recordsAfterCleanup
            );

            requireCondition(
                    incidentsAfterCleanup
                            == incidentsBeforeCreate,
                    "Incident count changed after the test. "
                            + "Before: "
                            + incidentsBeforeCreate
                            + ", after: "
                            + incidentsAfterCleanup
            );

            System.out.println(
                    "Maintenance records after cleanup: "
                            + recordsAfterCleanup
            );

            System.out.println(
                    "Incidents after cleanup: "
                            + incidentsAfterCleanup
            );

            System.out.println();

            System.out.println(
                    "All MaintenanceRecordService operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {

            System.err.println(
                    "MaintenanceRecordService cleanup SQL error."
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
                    "MaintenanceRecordService test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

        } finally {

            /*
             * Maintenance record must be deleted before
             * its related incident because of foreign keys.
             */
            if (createdRecordId != null
                    && !recordDeleted) {

                try {
                    boolean removed =
                            maintenanceRecordDAO.delete(
                                    createdRecordId
                            );

                    if (removed) {
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

            if (createdIncidentId != null
                    && !incidentDeleted) {

                try {
                    boolean removed =
                            incidentDAO.delete(
                                    createdIncidentId
                            );

                    if (removed) {
                        System.out.println();

                        System.out.println(
                                "Temporary incident was removed "
                                        + "during automatic cleanup."
                        );
                    }

                } catch (SQLException cleanupException) {

                    System.err.println();

                    System.err.println(
                            "Automatic incident cleanup failed."
                    );

                    System.err.println(
                            "Temporary incident ID: "
                                    + createdIncidentId
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

    private static void expectValidationException(
            Runnable operation,
            String failureMessage
    ) {

        try {
            operation.run();

        } catch (ValidationException exception) {
            return;
        }

        throw new IllegalStateException(
                failureMessage
        );
    }

    private static void expectNotFoundException(
            Runnable operation,
            String failureMessage
    ) {

        try {
            operation.run();

        } catch (NotFoundException exception) {
            return;
        }

        throw new IllegalStateException(
                failureMessage
        );
    }

    private static void requireCondition(
            boolean condition,
            String message
    ) {

        if (!condition) {
            throw new IllegalStateException(
                    message
            );
        }
    }
}