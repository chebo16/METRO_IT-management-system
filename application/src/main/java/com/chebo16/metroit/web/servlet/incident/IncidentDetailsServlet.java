package com.chebo16.metroit.web.servlet.incident;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.EquipmentService;
import com.chebo16.metroit.service.IncidentService;
import com.chebo16.metroit.service.MaintenanceRecordService;
import com.chebo16.metroit.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet(
        name = "IncidentDetailsServlet",
        urlPatterns = "/incidents/details"
)
public final class IncidentDetailsServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String INCIDENT_DETAILS_VIEW =
            "/WEB-INF/views/incidents/details.jsp";

    private final IncidentService incidentService =
            new IncidentService();

    private final EquipmentService equipmentService =
            new EquipmentService();

    private final UserService userService =
            new UserService();

    private final MaintenanceRecordService maintenanceRecordService =
            new MaintenanceRecordService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {

            long incidentId =
                    parseIncidentId(
                            request.getParameter(
                                    "id"
                            )
                    );

            Incident incident =
                    incidentService.getIncidentById(
                            incidentId
                    );

            Equipment equipment =
                    equipmentService.getEquipmentById(
                            incident.getEquipmentId()
                    );

            User createdByUser =
                    userService.getUserById(
                            incident.getCreatedById()
                    );

            User assignedTechnician =
                    loadAssignedTechnician(
                            incident
                    );

            List<User> availableTechnicians =
                    loadAvailableTechnicians();

            List<MaintenanceRecord> maintenanceRecords =
                    maintenanceRecordService.getRecordsByIncident(
                            incidentId
                    );

            request.setAttribute(
                    "incident",
                    incident
            );

            request.setAttribute(
                    "equipment",
                    equipment
            );

            request.setAttribute(
                    "createdByUser",
                    createdByUser
            );

            request.setAttribute(
                    "assignedTechnician",
                    assignedTechnician
            );

            request.setAttribute(
                    "availableTechnicians",
                    availableTechnicians
            );

            request.setAttribute(
                    "maintenanceRecords",
                    maintenanceRecords
            );

            request.getRequestDispatcher(
                    INCIDENT_DETAILS_VIEW
            ).forward(
                    request,
                    response
            );

        } catch (ValidationException exception) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (NotFoundException exception) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to load incident details.",
                    exception
            );

            throw new ServletException(
                    "Unable to load incident details.",
                    exception
            );
        }
    }

    private User loadAssignedTechnician(
            Incident incident
    ) {

        Long technicianId =
                incident.getAssignedTechnicianId();

        if (technicianId == null) {
            return null;
        }

        return userService.getUserById(
                technicianId
        );
    }

    private List<User> loadAvailableTechnicians() {

        List<User> allUsers =
                userService.getAllUsers();

        List<User> technicians =
                new ArrayList<>();

        for (User user : allUsers) {

            boolean technicianRole =
                    user.getRole()
                            == UserRole.TECHNICIAN;

            boolean activeAccount =
                    user.isActive();

            if (technicianRole
                    && activeAccount) {

                technicians.add(
                        user
                );
            }
        }

        technicians.sort(
                Comparator.comparing(
                        User::getFullName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return technicians;
    }

    private long parseIncidentId(
            String incidentIdValue
    ) {

        if (incidentIdValue == null
                || incidentIdValue.isBlank()) {

            throw new ValidationException(
                    "Incident ID must be provided."
            );
        }

        try {

            long incidentId =
                    Long.parseLong(
                            incidentIdValue.trim()
                    );

            if (incidentId <= 0) {

                throw new ValidationException(
                        "Incident ID must be greater than zero."
                );
            }

            return incidentId;

        } catch (NumberFormatException exception) {

            throw new ValidationException(
                    "Incident ID must be a valid number."
            );
        }
    }
}