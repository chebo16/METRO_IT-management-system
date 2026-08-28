package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.IncidentService;
import com.chebo16.metroit.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;

@WebServlet(
        name = "IncidentAssignServlet",
        urlPatterns = "/admin/incidents/assign"
)
public final class IncidentAssignServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final IncidentService incidentService =
            new IncidentService();

    private final UserService userService =
            new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Incident assignment can only be performed using a POST request."
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            long incidentId = parsePositiveId(
                    request.getParameter("incidentId"),
                    "Incident"
            );

            long technicianId = parsePositiveId(
                    request.getParameter("technicianId"),
                    "Technician"
            );

            Incident incident =
                    incidentService.getIncidentById(incidentId);

            validateIncidentAssignment(incident);

            User technician =
                    userService.getUserById(technicianId);

            validateTechnician(technician);

            incident.setAssignedTechnicianId(technicianId);
            incidentService.updateIncident(incident);

            String redirectUrl =
                    request.getContextPath()
                            + "/incidents/details"
                            + "?id="
                            + incidentId
                            + "&success=assigned";

            response.sendRedirect(
                    response.encodeRedirectURL(redirectUrl)
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
                    "Unable to assign technician to the incident.",
                    exception
            );

            throw new ServletException(
                    "Unable to assign technician to the incident.",
                    exception
            );
        }
    }

    private void validateIncidentAssignment(Incident incident) {
        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            throw new ValidationException(
                    "Technician assignment cannot be changed for a resolved incident."
            );
        }

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new ValidationException(
                    "Technician assignment cannot be changed for a closed incident."
            );
        }
    }

    private void validateTechnician(User technician) {
        if (technician.getRole() != UserRole.TECHNICIAN) {
            throw new ValidationException(
                    "Selected user is not a technician."
            );
        }

        if (!technician.isActive()) {
            throw new ValidationException(
                    "Selected technician account is inactive."
            );
        }
    }

    private long parsePositiveId(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " ID must be provided."
            );
        }

        try {
            long id = Long.parseLong(value.trim());

            if (id <= 0) {
                throw new ValidationException(
                        fieldName + " ID must be greater than zero."
                );
            }

            return id;

        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    fieldName + " ID must be a valid number."
            );
        }
    }
}