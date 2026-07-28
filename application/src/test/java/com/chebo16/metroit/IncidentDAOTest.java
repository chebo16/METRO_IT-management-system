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

            // PREPARATION: SELECT EXISTING RELATED RECORDS

            List<Equipment> equipmentList =
                    equipmentDAO.findAll();

            if (equipmentList.isEmpty()) {
                throw new IllegalStateException(
                        "No equipment records are available "
                                + "for the incident test."
                );
            }

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

            // READ ALL

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

            // CREATE

            String uniqueSuffix =
                    String.valueOf(
                            System.currentTimeMillis()
                    );

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

            long incidentId =
                    createdIncidentId;

            if (incidentId <= 0) {
                throw new IllegalStateException(
                        "Generated incident ID is invalid."
                );
            }

            System.out.println(
                    "2. insert() completed successfully."
            );

            System.out.println(
                    "Generated incident ID: "
                            + incidentId
            );

            System.out.println();

            // READ BY ID

            Optional<Incident> insertedIncidentOptional =
                    incidentDAO.findById(incidentId);

            if (insertedIncidentOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Inserted incident was not found by ID."
                );
            }

            Incident insertedIncident =
                    insertedIncidentOptional.get();

            if (insertedIncident.getStatus()
                    != IncidentStatus.NEW) {

                throw new IllegalStateException(
                        "New incident does not have NEW status."
                );
            }

            System.out.println(
                    "3. findById() completed successfully."
            );

            System.out.println("Inserted incident:");
            System.out.println(insertedIncident);
            System.out.println();

            // READ BY STATUS

            List<Incident> newIncidents =
                    incidentDAO.findByStatus(
                            IncidentStatus.NEW
                    );

            if (!containsIncident(
                    newIncidents,
                    incidentId
            )) {
                throw new IllegalStateException(
                        "Inserted incident was not found "
                                + "by NEW status."
                );
            }

            System.out.println(
                    "4. findByStatus(NEW) "
                            + "completed successfully."
            );

            System.out.println(
                    "NEW incidents found: "
                            + newIncidents.size()
            );

            System.out.println();

            // ASSIGN TECHNICIAN

            boolean technicianAssigned =
                    incidentDAO.assignTechnician(
                            incidentId,
                            technician.getId()
                    );

            if (!technicianAssigned) {
                throw new IllegalStateException(
                        "Technician assignment returned false."
                );
            }

            Incident assignedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Incident was not found "
                                                    + "after technician "
                                                    + "assignment."
                                    )
                            );

            if (!technician.getId().equals(
                    assignedIncident
                            .getAssignedTechnicianId()
            )) {
                throw new IllegalStateException(
                        "Technician ID was not saved."
                );
            }

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

            // READ BY TECHNICIAN

            List<Incident> technicianIncidents =
                    incidentDAO.findByTechnicianId(
                            technician.getId()
                    );

            if (!containsIncident(
                    technicianIncidents,
                    incidentId
            )) {
                throw new IllegalStateException(
                        "Assigned incident was not found "
                                + "by technician ID."
                );
            }

            System.out.println(
                    "6. findByTechnicianId() "
                            + "completed successfully."
            );

            System.out.println(
                    "Technician incidents found: "
                            + technicianIncidents.size()
            );

            System.out.println();

            // UPDATE GENERAL INFORMATION

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

            if (!updated) {
                throw new IllegalStateException(
                        "Incident update returned false."
                );
            }

            Incident updatedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated incident "
                                                    + "was not found."
                                    )
                            );

            if (!updatedIncident.getTitle().startsWith(
                    "Updated DAO incident"
            )) {
                throw new IllegalStateException(
                        "Incident title was not updated."
                );
            }

            if (updatedIncident.getPriority()
                    != IncidentPriority.CRITICAL) {

                throw new IllegalStateException(
                        "Incident priority was not updated."
                );
            }

            System.out.println(
                    "7. update() completed successfully."
            );

            System.out.println("Updated incident:");
            System.out.println(updatedIncident);
            System.out.println();

            // STATUS: IN_PROGRESS

            boolean movedToInProgress =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.IN_PROGRESS,
                            null
                    );

            if (!movedToInProgress) {
                throw new IllegalStateException(
                        "Status update to IN_PROGRESS "
                                + "returned false."
                );
            }

            Incident inProgressIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow();

            if (inProgressIncident.getStatus()
                    != IncidentStatus.IN_PROGRESS) {

                throw new IllegalStateException(
                        "Incident does not have "
                                + "IN_PROGRESS status."
                );
            }

            if (inProgressIncident.getStartedAt() == null) {
                throw new IllegalStateException(
                        "startedAt was not set."
                );
            }

            if (inProgressIncident.getResolvedAt() != null
                    || inProgressIncident.getClosedAt()
                    != null) {

                throw new IllegalStateException(
                        "Unexpected completion dates "
                                + "for IN_PROGRESS incident."
                );
            }

            System.out.println(
                    "8. updateStatus(IN_PROGRESS) "
                            + "completed successfully."
            );

            System.out.println(
                    "Started at: "
                            + inProgressIncident.getStartedAt()
            );

            System.out.println();

            // STATUS: RESOLVED

            String resolvedSolution =
                    "Temporary incident was resolved "
                            + "during the DAO test.";

            boolean movedToResolved =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.RESOLVED,
                            resolvedSolution
                    );

            if (!movedToResolved) {
                throw new IllegalStateException(
                        "Status update to RESOLVED "
                                + "returned false."
                );
            }

            Incident resolvedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow();

            if (resolvedIncident.getStatus()
                    != IncidentStatus.RESOLVED) {

                throw new IllegalStateException(
                        "Incident does not have "
                                + "RESOLVED status."
                );
            }

            if (resolvedIncident.getResolvedAt() == null) {
                throw new IllegalStateException(
                        "resolvedAt was not set."
                );
            }

            if (resolvedIncident.getClosedAt() != null) {
                throw new IllegalStateException(
                        "closedAt must be null "
                                + "for RESOLVED incident."
                );
            }

            if (!resolvedSolution.equals(
                    resolvedIncident
                            .getSolutionDescription()
            )) {
                throw new IllegalStateException(
                        "Solution description was not saved."
                );
            }

            System.out.println(
                    "9. updateStatus(RESOLVED) "
                            + "completed successfully."
            );

            System.out.println(
                    "Resolved at: "
                            + resolvedIncident.getResolvedAt()
            );

            System.out.println();

            // STATUS: CLOSED

            String closedSolution =
                    "Temporary incident was resolved "
                            + "and closed during the DAO test.";

            boolean movedToClosed =
                    incidentDAO.updateStatus(
                            incidentId,
                            IncidentStatus.CLOSED,
                            closedSolution
                    );

            if (!movedToClosed) {
                throw new IllegalStateException(
                        "Status update to CLOSED "
                                + "returned false."
                );
            }

            Incident closedIncident =
                    incidentDAO.findById(incidentId)
                            .orElseThrow();

            if (closedIncident.getStatus()
                    != IncidentStatus.CLOSED) {

                throw new IllegalStateException(
                        "Incident does not have CLOSED status."
                );
            }

            if (closedIncident.getStartedAt() == null
                    || closedIncident.getResolvedAt() == null
                    || closedIncident.getClosedAt() == null) {

                throw new IllegalStateException(
                        "Required lifecycle dates "
                                + "were not set."
                );
            }

            if (!closedSolution.equals(
                    closedIncident
                            .getSolutionDescription()
            )) {
                throw new IllegalStateException(
                        "Final solution description "
                                + "was not saved."
                );
            }

            System.out.println(
                    "10. updateStatus(CLOSED) "
                            + "completed successfully."
            );

            System.out.println(
                    "Closed at: "
                            + closedIncident.getClosedAt()
            );

            System.out.println();

            // DELETE TEMPORARY INCIDENT

            incidentDeleted =
                    incidentDAO.delete(incidentId);

            if (!incidentDeleted) {
                throw new IllegalStateException(
                        "Incident deletion returned false."
                );
            }

            if (incidentDAO.findById(incidentId).isPresent()) {
                throw new IllegalStateException(
                        "Incident still exists after deletion."
                );
            }

            System.out.println(
                    "11. delete() completed successfully."
            );

            System.out.println(
                    "Deleted incident ID: "
                            + incidentId
            );

            System.out.println();

            // FINAL COUNT CHECK

            int countAfterDelete =
                    incidentDAO.findAll().size();

            if (countAfterDelete != countBeforeInsert) {
                throw new IllegalStateException(
                        "Incident count changed after the test. "
                                + "Before: "
                                + countBeforeInsert
                                + ", after: "
                                + countAfterDelete
                );
            }

            System.out.println(
                    "Incidents after deletion: "
                            + countAfterDelete
            );

            System.out.println();

            System.out.println(
                    "All IncidentDAO operations "
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

        } catch (RuntimeException exception) {

            System.err.println(
                    "IncidentDAO test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

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
}
