package com.chebo16.metroit.web.servlet.incident;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.enums.IncidentStatus;
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

@WebServlet(
        name = "IncidentStatusServlet",
        urlPatterns = "/incidents/status"
)
public final class IncidentStatusServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final IncidentService incidentService =
            new IncidentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Incident status can only be changed using a POST request."
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        HttpSession session =
                request.getSession(false);

        SessionUser sessionUser =
                getSessionUser(
                        session
                );

        if (sessionUser == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required."
            );

            return;
        }

        try {

            long incidentId =
                    parseIncidentId(
                            request.getParameter(
                                    "incidentId"
                            )
                    );

            IncidentStatus requestedStatus =
                    parseStatus(
                            request.getParameter(
                                    "status"
                            )
                    );

            Incident incident =
                    incidentService.getIncidentById(
                            incidentId
                    );

            String solutionDescription =
                    normalizeParameter(
                            request.getParameter(
                                    "solutionDescription"
                            )
                    );

            if (sessionUser.isAdmin()) {

                updateAsAdministrator(
                        incident,
                        requestedStatus
                );

            } else {

                updateAsTechnician(
                        incident,
                        requestedStatus,
                        solutionDescription,
                        sessionUser.getId()
                );
            }

            String redirectUrl =
                    request.getContextPath()
                            + "/incidents/details"
                            + "?id="
                            + incidentId
                            + "&success=status-updated";

            response.sendRedirect(
                    response.encodeRedirectURL(
                            redirectUrl
                    )
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
                    "Unable to update incident status.",
                    exception
            );

            throw new ServletException(
                    "Unable to update incident status.",
                    exception
            );
        }
    }

    private void updateAsTechnician(
            Incident incident,
            IncidentStatus requestedStatus,
            String solutionDescription,
            long technicianId
    ) {

        validateAssignedTechnician(
                incident,
                technicianId
        );

        IncidentStatus currentStatus =
                incident.getStatus();

        if (currentStatus == IncidentStatus.NEW
                && requestedStatus
                == IncidentStatus.IN_PROGRESS) {

            incidentService.changeStatus(
                    incident.getId(),
                    IncidentStatus.IN_PROGRESS,
                    null
            );

            return;
        }

        if (currentStatus
                == IncidentStatus.IN_PROGRESS
                && requestedStatus
                == IncidentStatus.RESOLVED) {

            if (solutionDescription == null
                    || solutionDescription.isBlank()) {

                throw new ValidationException(
                        "Solution description is required."
                );
            }

            incidentService.changeStatus(
                    incident.getId(),
                    IncidentStatus.RESOLVED,
                    solutionDescription
            );

            return;
        }

        throw new ValidationException(
                "The requested incident status transition is not allowed."
        );
    }

    private void updateAsAdministrator(
            Incident incident,
            IncidentStatus requestedStatus
    ) {

        if (incident.getStatus()
                != IncidentStatus.RESOLVED) {

            throw new ValidationException(
                    "Only a resolved incident can be closed."
            );
        }

        if (requestedStatus
                != IncidentStatus.CLOSED) {

            throw new ValidationException(
                    "Administrator can only close a resolved incident."
            );
        }

        String existingSolution =
                incident.getSolutionDescription();

        if (existingSolution == null
                || existingSolution.isBlank()) {

            throw new ValidationException(
                    "Resolved incident does not contain a solution description."
            );
        }

        incidentService.changeStatus(
                incident.getId(),
                IncidentStatus.CLOSED,
                existingSolution
        );
    }

    private void validateAssignedTechnician(
            Incident incident,
            long technicianId
    ) {

        Long assignedTechnicianId =
                incident.getAssignedTechnicianId();

        if (assignedTechnicianId == null) {

            throw new ValidationException(
                    "The incident is not assigned to a technician."
            );
        }

        if (assignedTechnicianId.longValue()
                != technicianId) {

            throw new ValidationException(
                    "You can only update incidents assigned to you."
            );
        }
    }

    private SessionUser getSessionUser(
            HttpSession session
    ) {

        if (session == null) {
            return null;
        }

        Object authenticatedUser =
                session.getAttribute(
                        SessionConstants.AUTHENTICATED_USER
                );

        if (authenticatedUser
                instanceof SessionUser) {

            return (SessionUser)
                    authenticatedUser;
        }

        return null;
    }

    private long parseIncidentId(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new ValidationException(
                    "Incident ID is required."
            );
        }

        try {

            long incidentId =
                    Long.parseLong(
                            value.trim()
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

    private IncidentStatus parseStatus(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            throw new ValidationException(
                    "Incident status is required."
            );
        }

        try {

            return IncidentStatus.valueOf(
                    value.trim()
                            .toUpperCase()
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Invalid incident status."
            );
        }
    }

    private String normalizeParameter(
            String value
    ) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }
}