package com.chebo16.metroit.web.servlet.incident;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.enums.IncidentPriority;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@WebServlet(
        name = "MyIncidentsServlet",
        urlPatterns = "/incidents/my"
)
public final class MyIncidentsServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MY_INCIDENTS_VIEW =
            "/WEB-INF/views/incidents/my-list.jsp";

    private final IncidentService incidentService =
            new IncidentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SessionUser sessionUser = getSessionUser(session);

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
                    "This page is available only to technicians."
            );
            return;
        }

        try {
            List<Incident> allIncidents =
                    incidentService.getAllIncidents();

            List<Incident> myIncidents = filterAssignedIncidents(
                    allIncidents,
                    sessionUser.getId()
            );

            String search =
                    normalizeParameter(request.getParameter("search"));

            String status =
                    normalizeParameter(request.getParameter("status"));

            String priority =
                    normalizeParameter(request.getParameter("priority"));

            List<Incident> filteredIncidents = applyFilters(
                    myIncidents,
                    search,
                    status,
                    priority
            );

            sortByCreatedAtDescending(filteredIncidents);

            long totalIncidents = myIncidents.size();
            long newIncidents = countByStatus(
                    myIncidents,
                    IncidentStatus.NEW
            );
            long inProgressIncidents = countByStatus(
                    myIncidents,
                    IncidentStatus.IN_PROGRESS
            );
            long resolvedIncidents = countByStatus(
                    myIncidents,
                    IncidentStatus.RESOLVED
            );
            long closedIncidents = countByStatus(
                    myIncidents,
                    IncidentStatus.CLOSED
            );

            request.setAttribute("incidents", filteredIncidents);
            request.setAttribute("totalIncidents", totalIncidents);
            request.setAttribute("newIncidents", newIncidents);
            request.setAttribute(
                    "inProgressIncidents",
                    inProgressIncidents
            );
            request.setAttribute(
                    "resolvedIncidents",
                    resolvedIncidents
            );
            request.setAttribute("closedIncidents", closedIncidents);
            request.setAttribute("search", search);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedPriority", priority);
            request.setAttribute(
                    "statuses",
                    IncidentStatus.values()
            );
            request.setAttribute(
                    "priorities",
                    IncidentPriority.values()
            );

            request.getRequestDispatcher(MY_INCIDENTS_VIEW)
                    .forward(request, response);

        } catch (ServiceException exception) {
            getServletContext().log(
                    "Unable to load assigned incidents.",
                    exception
            );

            throw new ServletException(
                    "Unable to load assigned incidents.",
                    exception
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

    private List<Incident> filterAssignedIncidents(
            List<Incident> incidents,
            long technicianId
    ) {
        List<Incident> assignedIncidents = new ArrayList<>();

        for (Incident incident : incidents) {
            Long assignedTechnicianId =
                    incident.getAssignedTechnicianId();

            if (assignedTechnicianId != null
                    && assignedTechnicianId == technicianId) {
                assignedIncidents.add(incident);
            }
        }

        return assignedIncidents;
    }

    private List<Incident> applyFilters(
            List<Incident> incidents,
            String search,
            String status,
            String priority
    ) {
        List<Incident> filteredIncidents = new ArrayList<>();

        for (Incident incident : incidents) {
            if (!matchesSearch(incident, search)) {
                continue;
            }

            if (!matchesStatus(incident, status)) {
                continue;
            }

            if (!matchesPriority(incident, priority)) {
                continue;
            }

            filteredIncidents.add(incident);
        }

        return filteredIncidents;
    }

    private boolean matchesSearch(
            Incident incident,
            String search
    ) {
        if (search.isEmpty()) {
            return true;
        }

        String normalizedSearch =
                search.toLowerCase(Locale.ROOT);

        String title = incident.getTitle() == null
                ? ""
                : incident.getTitle().toLowerCase(Locale.ROOT);

        String description = incident.getDescription() == null
                ? ""
                : incident.getDescription().toLowerCase(Locale.ROOT);

        return title.contains(normalizedSearch)
                || description.contains(normalizedSearch);
    }

    private boolean matchesStatus(
            Incident incident,
            String status
    ) {
        if (status.isEmpty()) {
            return true;
        }

        if (incident.getStatus() == null) {
            return false;
        }

        return incident.getStatus()
                .name()
                .equalsIgnoreCase(status);
    }

    private boolean matchesPriority(
            Incident incident,
            String priority
    ) {
        if (priority.isEmpty()) {
            return true;
        }

        if (incident.getPriority() == null) {
            return false;
        }

        return incident.getPriority()
                .name()
                .equalsIgnoreCase(priority);
    }

    private long countByStatus(
            List<Incident> incidents,
            IncidentStatus status
    ) {
        long count = 0;

        for (Incident incident : incidents) {
            if (incident.getStatus() == status) {
                count++;
            }
        }

        return count;
    }

    private void sortByCreatedAtDescending(
            List<Incident> incidents
    ) {
        incidents.sort(
                Comparator.comparing(
                        Incident::getCreatedAt,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );
    }

    private String normalizeParameter(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}