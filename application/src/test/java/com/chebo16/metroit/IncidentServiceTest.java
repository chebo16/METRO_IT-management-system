package com.chebo16.metroit;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.IncidentService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class IncidentServiceTest {

    public static void main(String[] args) {

        IncidentService incidentService =
                new IncidentService();

        IncidentDAO incidentDAO =
                new IncidentDAO();

        EquipmentDAO equipmentDAO =
                new EquipmentDAO();

        UserDAO userDAO =
                new UserDAO();

        Long createdIncidentId = null;
        boolean cleanupCompleted = false;

        try {
            System.out.println(
                    "Testing IncidentService operations..."
            );

            System.out.println();

            // PREPARATION: SELECT RELATED RECORDS

            Equipment selectedEquipment =
                    equipmentDAO.findAll()
                            .stream()
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "No equipment record "
                                                    + "was found."
                                    )
                            );

            List<User> users =
                    userDAO.findAll();

            User administrator = users.stream()
                    .filter(User::isActive)
                    .filter(user ->
                            user.getRole()
                                    == UserRole.ADMIN
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No active ADMIN user "
                                            + "was found."
                            )
                    );

            User technician = users.stream()
                    .filter(User::isActive)
                    .filter(user ->
                            user.getRole()
                                    == UserRole.TECHNICIAN
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No active TECHNICIAN user "
                                            + "was found."
                            )
                    );

            final long equipmentId =
                    selectedEquipment.getId();

            final long administratorId =
                    administrator.getId();

            final long technicianId =
                    technician.getId();

            System.out.println(
                    "Selected equipment ID: "
                            + equipmentId
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

            // 1. READ ALL

            int countBeforeCreate =
                    incidentService
                            .getAllIncidents()
                            .size();

            System.out.println(
                    "1. getAllIncidents() "
                            + "completed successfully."
            );

            System.out.println(
                    "Incidents before create: "
                            + countBeforeCreate
            );

            System.out.println();

            // 2. CREATE AND NORMALIZE

            String uniqueSuffix =
                    String.valueOf(
                            System.currentTimeMillis()
                    );

            String expectedTitle =
                    "Temporary Service Incident "
                            + uniqueSuffix;

            Incident newIncident = new Incident(
                    "  " + expectedTitle + "  ",
                    "  Temporary incident created "
                            + "by IncidentServiceTest.  ",
                    IncidentPriority.HIGH,
                    equipmentId,
                    administratorId,
                    null
            );

            /*
             * These lifecycle values are intentionally incorrect.
             * createIncident() must replace them with NEW state.
             */
            newIncident.setStatus(
                    IncidentStatus.CLOSED
            );

            newIncident.setStartedAt(
                    LocalDateTime.now().minusHours(3)
            );

            newIncident.setResolvedAt(
                    LocalDateTime.now().minusHours(2)
            );

            newIncident.setClosedAt(
                    LocalDateTime.now().minusHours(1)
            );

            newIncident.setSolutionDescription(
                    "This value must be cleared."
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
                    expectedTitle.equals(
                            createdIncident.getTitle()
                    ),
                    "Incident title was not normalized."
            );

            requireCondition(
                    "Temporary incident created "
                            .concat(
                                    "by IncidentServiceTest."
                            )
                            .equals(
                                    createdIncident
                                            .getDescription()
                            ),
                    "Incident description "
                            + "was not normalized."
            );

            requireCondition(
                    createdIncident.getStatus()
                            == IncidentStatus.NEW,
                    "New incident must have NEW status."
            );

            requireCondition(
                    createdIncident.getStartedAt() == null,
                    "startedAt must be null "
                            + "for a new incident."
            );

            requireCondition(
                    createdIncident.getResolvedAt() == null,
                    "resolvedAt must be null "
                            + "for a new incident."
            );

            requireCondition(
                    createdIncident.getClosedAt() == null,
                    "closedAt must be null "
                            + "for a new incident."
            );

            requireCondition(
                    createdIncident
                            .getSolutionDescription() == null,
                    "Solution description must be null "
                            + "for a new incident."
            );

            System.out.println(
                    "2. createIncident() "
                            + "completed successfully."
            );

            System.out.println(
                    "Generated incident ID: "
                            + incidentId
            );

            System.out.println(
                    "Initial status: "
                            + createdIncident.getStatus()
            );

            System.out.println();

            // 3. READ BY ID

            Incident loadedIncident =
                    incidentService.getIncidentById(
                            incidentId
                    );

            requireCondition(
                    loadedIncident.getId() != null
                            && loadedIncident.getId()
                            == incidentId,
                    "getIncidentById() returned "
                            + "an unexpected incident."
            );

            System.out.println(
                    "3. getIncidentById() "
                            + "completed successfully."
            );

            System.out.println("Loaded incident:");
            System.out.println(loadedIncident);

            System.out.println();

            // 4. READ BY STATUS

            List<Incident> newIncidents =
                    incidentService.getIncidentsByStatus(
                            IncidentStatus.NEW
                    );

            requireCondition(
                    containsIncident(
                            newIncidents,
                            incidentId
                    ),
                    "Created incident was not found "
                            + "by NEW status."
            );

            System.out.println(
                    "4. getIncidentsByStatus(NEW) "
                            + "completed successfully."
            );

            System.out.println(
                    "NEW incidents found: "
                            + newIncidents.size()
            );

            System.out.println();

            // 5. INVALID NEW -> RESOLVED TRANSITION

            expectValidationException(
                    () -> incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            "Invalid direct resolution."
                    ),
                    "Invalid NEW → RESOLVED transition "
                            + "was not rejected."
            );

            System.out.println(
                    "5. Invalid NEW → RESOLVED transition "
                            + "was rejected successfully."
            );

            System.out.println();

            // 6. IN_PROGRESS WITHOUT TECHNICIAN

            expectValidationException(
                    () -> incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    ),
                    "IN_PROGRESS without a technician "
                            + "was not rejected."
            );

            System.out.println(
                    "6. IN_PROGRESS without a technician "
                            + "was rejected successfully."
            );

            System.out.println();

            // 7. ASSIGN ADMIN AS TECHNICIAN

            expectValidationException(
                    () -> incidentService.assignTechnician(
                            incidentId,
                            administratorId
                    ),
                    "ADMIN assignment as technician "
                            + "was not rejected."
            );

            System.out.println(
                    "7. ADMIN assignment as technician "
                            + "was rejected successfully."
            );

            System.out.println();

            // 8. ASSIGN TECHNICIAN

            Incident assignedIncident =
                    incidentService.assignTechnician(
                            incidentId,
                            technicianId
                    );

            requireCondition(
                    assignedIncident
                            .getAssignedTechnicianId() != null
                            && assignedIncident
                            .getAssignedTechnicianId()
                            == technicianId,
                    "Technician was not assigned."
            );

            System.out.println(
                    "8. assignTechnician() "
                            + "completed successfully."
            );

            System.out.println(
                    "Assigned technician ID: "
                            + assignedIncident
                            .getAssignedTechnicianId()
            );

            System.out.println();

            // 9. READ BY TECHNICIAN

            List<Incident> technicianIncidents =
                    incidentService
                            .getIncidentsByTechnician(
                                    technicianId
                            );

            requireCondition(
                    containsIncident(
                            technicianIncidents,
                            incidentId
                    ),
                    "Assigned incident was not found "
                            + "by technician."
            );

            System.out.println(
                    "9. getIncidentsByTechnician() "
                            + "completed successfully."
            );

            System.out.println(
                    "Technician incidents found: "
                            + technicianIncidents.size()
            );

            System.out.println();

            // 10. UNASSIGN TECHNICIAN

            Incident unassignedIncident =
                    incidentService.unassignTechnician(
                            incidentId
                    );

            requireCondition(
                    unassignedIncident
                            .getAssignedTechnicianId() == null,
                    "Technician was not removed."
            );

            System.out.println(
                    "10. unassignTechnician() "
                            + "completed successfully."
            );

            System.out.println();

            // ASSIGN AGAIN FOR LIFECYCLE TEST

            assignedIncident =
                    incidentService.assignTechnician(
                            incidentId,
                            technicianId
                    );

            // 11. UPDATE GENERAL INFORMATION

            assignedIncident.setTitle(
                    "  Updated Service Incident "
                            + uniqueSuffix
                            + "  "
            );

            assignedIncident.setDescription(
                    "  Incident information was updated "
                            + "by IncidentServiceTest.  "
            );

            assignedIncident.setPriority(
                    IncidentPriority.CRITICAL
            );

            /*
             * General update must not change the lifecycle.
             */
            assignedIncident.setStatus(
                    IncidentStatus.CLOSED
            );

            assignedIncident.setClosedAt(
                    LocalDateTime.now()
            );

            assignedIncident.setSolutionDescription(
                    "This lifecycle value must be ignored."
            );

            Incident updatedIncident =
                    incidentService.updateIncident(
                            assignedIncident
                    );

            requireCondition(
                    updatedIncident.getTitle().startsWith(
                            "Updated Service Incident"
                    ),
                    "Incident title was not updated."
            );

            requireCondition(
                    updatedIncident.getPriority()
                            == IncidentPriority.CRITICAL,
                    "Incident priority was not updated."
            );

            requireCondition(
                    updatedIncident.getStatus()
                            == IncidentStatus.NEW,
                    "General update changed "
                            + "the incident status."
            );

            requireCondition(
                    updatedIncident.getClosedAt() == null,
                    "General update changed closedAt."
            );

            System.out.println(
                    "11. updateIncident() "
                            + "completed successfully."
            );

            System.out.println("Updated incident:");
            System.out.println(updatedIncident);

            System.out.println();

            // 12. NEW -> IN_PROGRESS

            Incident inProgressIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    );

            requireCondition(
                    inProgressIncident.getStatus()
                            == IncidentStatus.IN_PROGRESS,
                    "Incident status was not changed "
                            + "to IN_PROGRESS."
            );

            requireCondition(
                    inProgressIncident.getStartedAt() != null,
                    "startedAt was not generated."
            );

            requireCondition(
                    inProgressIncident.getResolvedAt() == null,
                    "resolvedAt must be null "
                            + "for IN_PROGRESS."
            );

            System.out.println(
                    "12. NEW → IN_PROGRESS "
                            + "completed successfully."
            );

            System.out.println(
                    "Started at: "
                            + inProgressIncident.getStartedAt()
            );

            System.out.println();

            // 13. INVALID IN_PROGRESS -> CLOSED

            expectValidationException(
                    () -> incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.CLOSED,
                            "Invalid direct closure."
                    ),
                    "Invalid IN_PROGRESS → CLOSED "
                            + "transition was not rejected."
            );

            System.out.println(
                    "13. Invalid IN_PROGRESS → CLOSED "
                            + "transition was rejected successfully."
            );

            System.out.println();

            // 14. RESOLVED WITHOUT SOLUTION

            expectValidationException(
                    () -> incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            "   "
                    ),
                    "RESOLVED without a solution "
                            + "was not rejected."
            );

            System.out.println(
                    "14. RESOLVED without a solution "
                            + "was rejected successfully."
            );

            System.out.println();

            // 15. IN_PROGRESS -> RESOLVED

            String firstSolution =
                    "Temporary incident was resolved "
                            + "during the service test.";

            Incident resolvedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            "  " + firstSolution + "  "
                    );

            requireCondition(
                    resolvedIncident.getStatus()
                            == IncidentStatus.RESOLVED,
                    "Incident status was not changed "
                            + "to RESOLVED."
            );

            requireCondition(
                    resolvedIncident.getResolvedAt() != null,
                    "resolvedAt was not generated."
            );

            requireCondition(
                    firstSolution.equals(
                            resolvedIncident
                                    .getSolutionDescription()
                    ),
                    "Solution description "
                            + "was not normalized."
            );

            System.out.println(
                    "15. IN_PROGRESS → RESOLVED "
                            + "completed successfully."
            );

            System.out.println(
                    "Resolved at: "
                            + resolvedIncident.getResolvedAt()
            );

            System.out.println();

            // 16. RESOLVED -> IN_PROGRESS

            Incident reopenedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    );

            requireCondition(
                    reopenedIncident.getStatus()
                            == IncidentStatus.IN_PROGRESS,
                    "Resolved incident was not reopened."
            );

            requireCondition(
                    reopenedIncident.getResolvedAt() == null,
                    "resolvedAt was not cleared "
                            + "after reopening."
            );

            requireCondition(
                    reopenedIncident
                            .getSolutionDescription() == null,
                    "Solution description was not cleared "
                            + "after reopening."
            );

            System.out.println(
                    "16. RESOLVED → IN_PROGRESS "
                            + "completed successfully."
            );

            System.out.println();

            // 17. RESOLVE AGAIN

            String finalSolution =
                    "The incident was diagnosed, repaired "
                            + "and tested successfully.";

            resolvedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            finalSolution
                    );

            requireCondition(
                    resolvedIncident.getStatus()
                            == IncidentStatus.RESOLVED,
                    "Incident was not resolved again."
            );

            System.out.println(
                    "17. Second resolution "
                            + "completed successfully."
            );

            System.out.println();

            // 18. RESOLVED -> CLOSED

            Incident closedIncident =
                    incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.CLOSED,
                            finalSolution
                    );

            requireCondition(
                    closedIncident.getStatus()
                            == IncidentStatus.CLOSED,
                    "Incident status was not changed "
                            + "to CLOSED."
            );

            requireCondition(
                    closedIncident.getStartedAt() != null,
                    "startedAt is missing "
                            + "for closed incident."
            );

            requireCondition(
                    closedIncident.getResolvedAt() != null,
                    "resolvedAt is missing "
                            + "for closed incident."
            );

            requireCondition(
                    closedIncident.getClosedAt() != null,
                    "closedAt was not generated."
            );

            System.out.println(
                    "18. RESOLVED → CLOSED "
                            + "completed successfully."
            );

            System.out.println(
                    "Closed at: "
                            + closedIncident.getClosedAt()
            );

            System.out.println();

            // 19. CLOSED INCIDENT CANNOT BE EDITED

            closedIncident.setTitle(
                    "Attempted closed incident update"
            );

            expectValidationException(
                    () -> incidentService.updateIncident(
                            closedIncident
                    ),
                    "Closed incident update "
                            + "was not rejected."
            );

            System.out.println(
                    "19. Closed incident update "
                            + "was rejected successfully."
            );

            System.out.println();

            // 20. CLOSED INCIDENT CANNOT BE REASSIGNED

            expectValidationException(
                    () -> incidentService.assignTechnician(
                            incidentId,
                            technicianId
                    ),
                    "Closed incident technician assignment "
                            + "was not rejected."
            );

            expectValidationException(
                    () -> incidentService.unassignTechnician(
                            incidentId
                    ),
                    "Closed incident technician removal "
                            + "was not rejected."
            );

            System.out.println(
                    "20. Closed incident technician changes "
                            + "were rejected successfully."
            );

            System.out.println();

            // 21. CLOSED INCIDENT STATUS CANNOT CHANGE

            expectValidationException(
                    () -> incidentService.changeStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    ),
                    "Closed incident status change "
                            + "was not rejected."
            );

            System.out.println(
                    "21. Closed incident status change "
                            + "was rejected successfully."
            );

            System.out.println();

            // 22. INVALID ID

            expectValidationException(
                    () -> incidentService
                            .getIncidentById(0),
                    "Invalid incident ID "
                            + "was not rejected."
            );

            System.out.println(
                    "22. Invalid incident ID "
                            + "was rejected successfully."
            );

            System.out.println();

            // 23. NOT FOUND

            expectNotFoundException(
                    () -> incidentService.getIncidentById(
                            Long.MAX_VALUE
                    ),
                    "Missing incident did not produce "
                            + "NotFoundException."
            );

            System.out.println(
                    "23. Missing incident produced "
                            + "NotFoundException successfully."
            );

            System.out.println();

            // 24. CLEANUP

            cleanupCompleted =
                    incidentDAO.delete(incidentId);

            requireCondition(
                    cleanupCompleted,
                    "Temporary incident cleanup "
                            + "returned false."
            );

            expectNotFoundException(
                    () -> incidentService.getIncidentById(
                            incidentId
                    ),
                    "Incident still exists after cleanup."
            );

            System.out.println(
                    "24. Temporary incident "
                            + "was deleted successfully."
            );

            System.out.println(
                    "Deleted incident ID: "
                            + incidentId
            );

            System.out.println();

            // 25. FINAL COUNT

            int countAfterCleanup =
                    incidentService
                            .getAllIncidents()
                            .size();

            requireCondition(
                    countAfterCleanup
                            == countBeforeCreate,
                    "Incident count changed after the test. "
                            + "Before: "
                            + countBeforeCreate
                            + ", after: "
                            + countAfterCleanup
            );

            System.out.println(
                    "Incidents after cleanup: "
                            + countAfterCleanup
            );

            System.out.println();

            System.out.println(
                    "All IncidentService operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {

            System.err.println(
                    "IncidentService cleanup SQL error."
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
                    "IncidentService test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

        } finally {

            if (createdIncidentId != null
                    && !cleanupCompleted) {

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

    private static boolean containsIncident(
            List<Incident> incidents,
            long incidentId
    ) {

        for (Incident incident : incidents) {

            if (incident.getId() != null
                    && incident.getId() == incidentId) {

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