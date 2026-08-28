package com.chebo16.metroit;

import com.chebo16.metroit.dao.EquipmentDAO;
import com.chebo16.metroit.dao.IncidentDAO;
import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IncidentDAOTest {

    public static void main(String[] args) {
        IncidentDAO incidentDAO = new IncidentDAO();
        EquipmentDAO equipmentDAO = new EquipmentDAO();
        UserDAO userDAO = new UserDAO();

        Long createdIncidentId = null;
        boolean incidentDeleted = false;

        try {
            System.out.println(
                    "Testing IncidentDAO operations..."
            );
            System.out.println();

            List<Equipment> equipmentList =
                    equipmentDAO.findAll();

            requireCondition(
                    !equipmentList.isEmpty(),
                    "No equipment records are available "
                            + "for the incident test."
            );

            Equipment selectedEquipment =
                    equipmentList.getFirst();

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

            System.out.println(
                    "Selected equipment ID: "
                            + selectedEquipment.getId()
            );
            System.out.println(
                    "Selected administrator ID: "
                            + administrator.getId()
            );
            System.out.println(
                    "Selected technician ID: "
                            + technician.getId()
            );
            System.out.println();

            List<Incident> incidentsBeforeInsert =
                    incidentDAO.findAll();

            int countBeforeInsert =
                    incidentsBeforeInsert.size();

            System.out.println(
                    "1. findAll() completed successfully."
            );
            System.out.println(
                    "Incidents before insert: "
                            + countBeforeInsert
            );
            System.out.println();

            String uniqueSuffix =
                    String.valueOf(System.currentTimeMillis());

            Incident testIncident = new Incident(
                    "Temporary DAO incident " + uniqueSuffix,
                    "Temporary incident created "
                            + "by IncidentDAOTest.",
                    IncidentPriority.MEDIUM,
                    selectedEquipment.getId(),
                    administrator.getId(),
                    null
            );

            createdIncidentId =
                    incidentDAO.insert(testIncident);

            requireCondition(
                    createdIncidentId != null
                            && createdIncidentId > 0,
                    "Generated incident ID is invalid."
            );

            final long incidentId =
                    createdIncidentId;

            System.out.println(
                    "2. insert() completed successfully."
            );
            System.out.println(
                    "Generated incident ID: " + incidentId
            );
            System.out.println();

            Optional<Incident> insertedIncidentOptional =
                    incidentDAO.findById(incidentId);

            requireCondition(
                    insertedIncidentOptional.isPresent(),
                    "Inserted incident was not found by ID."
            );

            Incident insertedIncident =
                    insertedIncidentOptional.get();

            requireCondition(
                    insertedIncident.getStatus()
                            == IncidentStatus.NEW,
                    "New incident does not have NEW status."
            );

            System.out.println(
                    "3. findById() completed successfully."
            );
            System.out.println();

            List<Incident> newIncidents =
                    incidentDAO.findByStatus(
                            IncidentStatus.NEW
                    );

            requireCondition(
                    containsIncident(
                            newIncidents,
                            incidentId
                    ),
                    "Inserted incident was not found "
                            + "by NEW status."
            );

            System.out.println(
                    "4. findByStatus(NEW) "
                            + "completed successfully."
            );
            System.out.println(
                    "NEW incidents found: "
                            + newIncidents.size()
            );
            System.out.println();

            boolean technicianAssigned =
                    incidentDAO.assignTechnician(
                            incidentId,
                            technician.getId()
                    );

            requireCondition(
                    technicianAssigned,
                    "Technician assignment returned false."
            );

            Incident assignedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Incident was not found "
                                                    + "after technician "
                                                    + "assignment."
                                    )
                            );

            requireCondition(
                    technician.getId().equals(
                            assignedIncident
                                    .getAssignedTechnicianId()
                    ),
                    "Technician ID was not saved."
            );

            System.out.println(
                    "5. assignTechnician() "
                            + "completed successfully."
            );
            System.out.println(
                    "Assigned technician ID: "
                            + assignedIncident
                            .getAssignedTechnicianId()
            );
            System.out.println();

            List<Incident> technicianIncidents =
                    incidentDAO.findByTechnicianId(
                            technician.getId()
                    );

            requireCondition(
                    containsIncident(
                            technicianIncidents,
                            incidentId
                    ),
                    "Assigned incident was not found "
                            + "by technician ID."
            );

            System.out.println(
                    "6. findByTechnicianId() "
                            + "completed successfully."
            );
            System.out.println(
                    "Technician incidents found: "
                            + technicianIncidents.size()
            );
            System.out.println();

            assignedIncident.setTitle(
                    "Updated DAO incident " + uniqueSuffix
            );
            assignedIncident.setDescription(
                    "Incident information was updated "
                            + "by IncidentDAOTest."
            );
            assignedIncident.setPriority(
                    IncidentPriority.CRITICAL
            );

            boolean updated =
                    incidentDAO.update(assignedIncident);

            requireCondition(
                    updated,
                    "Incident update returned false."
            );

            Incident updatedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated incident "
                                                    + "was not found."
                                    )
                            );

            requireCondition(
                    updatedIncident.getTitle().startsWith(
                            "Updated DAO incident"
                    ),
                    "Incident title was not updated."
            );

            requireCondition(
                    updatedIncident.getPriority()
                            == IncidentPriority.CRITICAL,
                    "Incident priority was not updated."
            );

            System.out.println(
                    "7. update() completed successfully."
            );
            System.out.println();

            boolean movedToInProgress =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    );

            requireCondition(
                    movedToInProgress,
                    "Status update to IN_PROGRESS "
                            + "returned false."
            );

            Incident inProgressIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Incident was not found "
                                                    + "after IN_PROGRESS update."
                                    )
                            );

            requireCondition(
                    inProgressIncident.getStatus()
                            == IncidentStatus.IN_PROGRESS,
                    "Incident does not have "
                            + "IN_PROGRESS status."
            );

            requireCondition(
                    inProgressIncident.getStartedAt() != null,
                    "startedAt was not set."
            );

            requireCondition(
                    inProgressIncident.getResolvedAt() == null
                            && inProgressIncident.getClosedAt() == null,
                    "Unexpected completion dates "
                            + "for IN_PROGRESS incident."
            );

            System.out.println(
                    "8. updateStatus(IN_PROGRESS) "
                            + "completed successfully."
            );
            System.out.println(
                    "Started at: "
                            + inProgressIncident.getStartedAt()
            );
            System.out.println();

            String resolvedSolution =
                    "Temporary incident was resolved "
                            + "during the DAO test.";

            boolean movedToResolved =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            resolvedSolution
                    );

            requireCondition(
                    movedToResolved,
                    "Status update to RESOLVED "
                            + "returned false."
            );

            Incident resolvedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Incident was not found "
                                                    + "after RESOLVED update."
                                    )
                            );

            requireCondition(
                    resolvedIncident.getStatus()
                            == IncidentStatus.RESOLVED,
                    "Incident does not have RESOLVED status."
            );

            requireCondition(
                    resolvedIncident.getResolvedAt() != null,
                    "resolvedAt was not set."
            );

            requireCondition(
                    resolvedIncident.getClosedAt() == null,
                    "closedAt must be null "
                            + "for RESOLVED incident."
            );

            requireCondition(
                    resolvedSolution.equals(
                            resolvedIncident
                                    .getSolutionDescription()
                    ),
                    "Solution description was not saved."
            );

            System.out.println(
                    "9. updateStatus(RESOLVED) "
                            + "completed successfully."
            );
            System.out.println(
                    "Resolved at: "
                            + resolvedIncident.getResolvedAt()
            );
            System.out.println();

            String closedSolution =
                    "Temporary incident was resolved "
                            + "and closed during the DAO test.";

            boolean movedToClosed =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.CLOSED,
                            closedSolution
                    );

            requireCondition(
                    movedToClosed,
                    "Status update to CLOSED "
                            + "returned false."
            );

            Incident closedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Incident was not found "
                                                    + "after CLOSED update."
                                    )
                            );

            requireCondition(
                    closedIncident.getStatus()
                            == IncidentStatus.CLOSED,
                    "Incident does not have CLOSED status."
            );

            requireCondition(
                    closedIncident.getStartedAt() != null
                            && closedIncident.getResolvedAt() != null
                            && closedIncident.getClosedAt() != null,
                    "Required lifecycle dates were not set."
            );

            requireCondition(
                    closedSolution.equals(
                            closedIncident
                                    .getSolutionDescription()
                    ),
                    "Final solution description was not saved."
            );

            System.out.println(
                    "10. updateStatus(CLOSED) "
                            + "completed successfully."
            );
            System.out.println(
                    "Closed at: "
                            + closedIncident.getClosedAt()
            );
            System.out.println();

            incidentDeleted =
                    incidentDAO.delete(incidentId);

            requireCondition(
                    incidentDeleted,
                    "Incident deletion returned false."
            );

            requireCondition(
                    incidentDAO.findById(incidentId).isEmpty(),
                    "Incident still exists after deletion."
            );

            System.out.println(
                    "11. delete() completed successfully."
            );
            System.out.println(
                    "Deleted incident ID: " + incidentId
            );
            System.out.println();

            int countAfterDelete =
                    incidentDAO.findAll().size();

            requireCondition(
                    countAfterDelete == countBeforeInsert,
                    "Incident count changed after the test. "
                            + "Before: "
                            + countBeforeInsert
                            + ", after: "
                            + countAfterDelete
            );

            System.out.println(
                    "Incidents after deletion: "
                            + countAfterDelete
            );
            System.out.println();

            System.out.println(
                    "12. All IncidentDAO operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {
            System.err.println(
                    "IncidentDAO SQL test failed."
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
                    "IncidentDAO test failed "
                            + "because of a database error.",
                    exception
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "IncidentDAO test failed."
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw exception;

        } finally {
            if (createdIncidentId != null
                    && !incidentDeleted) {

                try {
                    boolean cleanupSuccessful =
                            incidentDAO.delete(
                                    createdIncidentId
                            );

                    if (cleanupSuccessful) {
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

    private static void requireCondition(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}