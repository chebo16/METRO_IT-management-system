package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.service.EquipmentService;
import com.chebo16.metroit.service.IncidentService;
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
import java.util.List;
import java.util.Locale;

@WebServlet(
        name = "IncidentCreateServlet",
        urlPatterns = "/admin/incidents/create"
)
public final class IncidentCreateServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String INCIDENT_FORM_VIEW =
            "/WEB-INF/views/admin/incidents/form.jsp";

    private final IncidentService incidentService =
            new IncidentService();

    private final EquipmentService equipmentService =
            new EquipmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            prepareCreateForm(request);

            forwardToForm(
                    request,
                    response
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to prepare the incident creation form.",
                    exception
            );

            throw new ServletException(
                    "Unable to prepare the incident creation form.",
                    exception
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        String title =
                normalizeText(
                        request.getParameter(
                                "title"
                        )
                );

        String description =
                normalizeText(
                        request.getParameter(
                                "description"
                        )
                );

        String priorityValue =
                normalizeText(
                        request.getParameter(
                                "priority"
                        )
                );

        String equipmentIdValue =
                normalizeText(
                        request.getParameter(
                                "equipmentId"
                        )
                );

        try {
            SessionUser sessionUser =
                    getAuthenticatedUser(
                            request
                    );

            IncidentPriority priority =
                    parsePriority(
                            priorityValue
                    );

            long equipmentId =
                    parseEquipmentId(
                            equipmentIdValue
                    );

            Incident incident =
                    new Incident();

            incident.setTitle(
                    title
            );

            incident.setDescription(
                    description
            );

            incident.setPriority(
                    priority
            );

            incident.setStatus(
                    IncidentStatus.NEW
            );

            incident.setEquipmentId(
                    equipmentId
            );

            incident.setCreatedById(
                    sessionUser.getId()
            );

            incident.setAssignedTechnicianId(
                    null
            );

            incidentService.createIncident(
                    incident
            );

            response.sendRedirect(
                    response.encodeRedirectURL(
                            request.getContextPath()
                                    + "/incidents"
                                    + "?success=created"
                    )
            );

        } catch (ValidationException
                 | NotFoundException exception) {

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    title,
                    description,
                    priorityValue,
                    equipmentIdValue
            );

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            forwardToForm(
                    request,
                    response
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to create the incident.",
                    exception
            );

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    title,
                    description,
                    priorityValue,
                    equipmentIdValue
            );

            request.setAttribute(
                    "errorMessage",
                    "The incident could not be created. "
                            + "Please try again later."
            );

            forwardToForm(
                    request,
                    response
            );
        }
    }

    private void prepareCreateForm(
            HttpServletRequest request
    ) {

        List<Equipment> equipment =
                equipmentService.getAllEquipment();

        request.setAttribute(
                "pageTitle",
                "Create incident"
        );

        request.setAttribute(
                "formAction",
                request.getContextPath()
                        + "/admin/incidents/create"
        );

        request.setAttribute(
                "submitLabel",
                "Create incident"
        );

        request.setAttribute(
                "equipment",
                equipment
        );

        request.setAttribute(
                "availablePriorities",
                IncidentPriority.values()
        );

        if (request.getAttribute(
                "selectedPriority"
        ) == null) {

            request.setAttribute(
                    "selectedPriority",
                    IncidentPriority.MEDIUM.name()
            );
        }
    }

    private SessionUser getAuthenticatedUser(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            throw new ValidationException(
                    "Authenticated user session was not found."
            );
        }

        Object authenticatedUserAttribute =
                session.getAttribute(
                        SessionConstants.AUTHENTICATED_USER
                );

        if (!(authenticatedUserAttribute
                instanceof SessionUser)) {

            throw new ValidationException(
                    "Authenticated user session was not found."
            );
        }

        return (SessionUser)
                authenticatedUserAttribute;
    }

    private IncidentPriority parsePriority(
            String priorityValue
    ) {

        if (priorityValue == null
                || priorityValue.isBlank()) {

            throw new ValidationException(
                    "Incident priority must be selected."
            );
        }

        try {
            return IncidentPriority.valueOf(
                    priorityValue
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected incident priority is invalid."
            );
        }
    }

    private long parseEquipmentId(
            String equipmentIdValue
    ) {

        if (equipmentIdValue == null
                || equipmentIdValue.isBlank()) {

            throw new ValidationException(
                    "Equipment must be selected."
            );
        }

        try {
            long equipmentId =
                    Long.parseLong(
                            equipmentIdValue.trim()
                    );

            if (equipmentId <= 0) {

                throw new ValidationException(
                        "Equipment ID must be greater than zero."
                );
            }

            return equipmentId;

        } catch (NumberFormatException exception) {

            throw new ValidationException(
                    "Equipment ID must be a valid number."
            );
        }
    }

    private void preserveFormValues(
            HttpServletRequest request,
            String title,
            String description,
            String priorityValue,
            String equipmentIdValue
    ) {

        request.setAttribute(
                "title",
                valueOrEmpty(title)
        );

        request.setAttribute(
                "description",
                valueOrEmpty(description)
        );

        if (priorityValue == null
                || priorityValue.isBlank()) {

            request.setAttribute(
                    "selectedPriority",
                    IncidentPriority.MEDIUM.name()
            );

        } else {

            request.setAttribute(
                    "selectedPriority",
                    priorityValue
            );
        }

        request.setAttribute(
                "selectedEquipmentId",
                valueOrEmpty(
                        equipmentIdValue
                )
        );
    }

    private String normalizeText(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String valueOrEmpty(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value;
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(
                INCIDENT_FORM_VIEW
        ).forward(
                request,
                response
        );
    }
}