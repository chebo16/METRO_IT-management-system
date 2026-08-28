package com.chebo16.metroit.web.servlet.maintenance;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.service.EquipmentService;
import com.chebo16.metroit.service.IncidentService;
import com.chebo16.metroit.service.MaintenanceRecordService;
import com.chebo16.metroit.web.session.SessionConstants;
import com.chebo16.metroit.web.session.SessionUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;

@WebServlet(
        name = "MaintenanceCreateServlet",
        urlPatterns = "/maintenance/create"
)
public final class MaintenanceCreateServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MAINTENANCE_FORM_VIEW =
            "/WEB-INF/views/maintenance/form.jsp";

    private final MaintenanceRecordService maintenanceRecordService =
            new MaintenanceRecordService();

    private final IncidentService incidentService =
            new IncidentService();

    private final EquipmentService equipmentService =
            new EquipmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        SessionUser sessionUser =
                getSessionUser(request.getSession(false));

        if (sessionUser == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required."
            );
            return;
        }

        if (sessionUser.isAdmin()) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Only technicians can create maintenance records."
            );
            return;
        }

        try {
            long incidentId =
                    parseIncidentId(
                            request.getParameter("incidentId")
                    );

            Incident incident =
                    incidentService.getIncidentById(incidentId);

            validateTechnicianAccess(
                    incident,
                    sessionUser.getId()
            );

            Equipment equipment =
                    equipmentService.getEquipmentById(
                            incident.getEquipmentId()
                    );

            request.setAttribute("incident", incident);
            request.setAttribute("equipment", equipment);
            request.setAttribute(
                    "results",
                    MaintenanceResult.values()
            );

            request.getRequestDispatcher(MAINTENANCE_FORM_VIEW)
                    .forward(request, response);

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
                    "Unable to load maintenance form.",
                    exception
            );

            throw new ServletException(
                    "Unable to load maintenance form.",
                    exception
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        SessionUser sessionUser =
                getSessionUser(request.getSession(false));

        if (sessionUser == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required."
            );
            return;
        }

        if (sessionUser.isAdmin()) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Only technicians can create maintenance records."
            );
            return;
        }

        try {
            long incidentId =
                    parseIncidentId(
                            request.getParameter("incidentId")
                    );

            Incident incident =
                    incidentService.getIncidentById(incidentId);

            validateTechnicianAccess(
                    incident,
                    sessionUser.getId()
            );

            String workDescription =
                    normalizeRequiredText(
                            request.getParameter("workDescription"),
                            "Work description"
                    );

            String replacedComponents =
                    normalizeOptionalText(
                            request.getParameter("replacedComponents")
                    );

            MaintenanceResult result =
                    parseResult(
                            request.getParameter("result")
                    );

            MaintenanceRecord record = new MaintenanceRecord();

            record.setIncidentId(incident.getId());
            record.setEquipmentId(incident.getEquipmentId());
            record.setTechnicianId(sessionUser.getId());
            record.setWorkDescription(workDescription);
            record.setReplacedComponents(replacedComponents);
            record.setResult(result);

            maintenanceRecordService.createRecord(record);

            String redirectUrl =
                    request.getContextPath()
                            + "/incidents/details"
                            + "?id="
                            + incidentId
                            + "&success=maintenance-created";

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
                    "Unable to create maintenance record.",
                    exception
            );

            throw new ServletException(
                    "Unable to create maintenance record.",
                    exception
            );
        }
    }

    private void validateTechnicianAccess(
            Incident incident,
            long technicianId
    ) {
        Long assignedTechnicianId =
                incident.getAssignedTechnicianId();

        if (assignedTechnicianId == null) {
            throw new ValidationException(
                    "The incident does not have an assigned technician."
            );
        }

        if (assignedTechnicianId.longValue() != technicianId) {
            throw new ValidationException(
                    "You can only record maintenance work "
                            + "for incidents assigned to you."
            );
        }

        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new ValidationException(
                    "Maintenance work can only be recorded "
                            + "for an incident with IN_PROGRESS status."
            );
        }
    }

    private SessionUser getSessionUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object authenticatedUser = session.getAttribute(
                SessionConstants.AUTHENTICATED_USER
        );

        if (authenticatedUser instanceof SessionUser sessionUser) {
            return sessionUser;
        }

        return null;
    }

    private long parseIncidentId(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    "Incident ID is required."
            );
        }

        try {
            long incidentId =
                    Long.parseLong(value.trim());

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

    private MaintenanceResult parseResult(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    "Maintenance result is required."
            );
        }

        try {
            return MaintenanceResult.valueOf(
                    value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(
                    "Invalid maintenance result."
            );
        }
    }

    private String normalizeRequiredText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " must not be empty."
            );
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}