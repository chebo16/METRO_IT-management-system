package com.chebo16.metroit.web.servlet.incident;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.service.IncidentService;
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
import java.util.Locale;

@WebServlet(
        name = "IncidentListServlet",
        urlPatterns = "/incidents"
)
public final class IncidentListServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String INCIDENT_LIST_VIEW =
            "/WEB-INF/views/incidents/list.jsp";

    private final IncidentService incidentService =
            new IncidentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String searchQuery =
                normalizeText(
                        request.getParameter("q")
                );

        String statusValue =
                normalizeText(
                        request.getParameter("status")
                );

        String priorityValue =
                normalizeText(
                        request.getParameter("priority")
                );

        try {
            IncidentStatus selectedStatus =
                    parseOptionalStatus(
                            statusValue
                    );

            IncidentPriority selectedPriority =
                    parseOptionalPriority(
                            priorityValue
                    );

            List<Incident> allIncidents =
                    incidentService.getAllIncidents();

            List<Incident> filteredIncidents =
                    filterIncidents(
                            allIncidents,
                            searchQuery,
                            selectedStatus,
                            selectedPriority
                    );

            filteredIncidents.sort(
                    Comparator.comparing(
                            Incident::getCreatedAt,
                            Comparator.nullsLast(
                                    Comparator.reverseOrder()
                            )
                    )
            );

            request.setAttribute(
                    "incidents",
                    filteredIncidents
            );

            request.setAttribute(
                    "totalIncidents",
                    allIncidents.size()
            );

            request.setAttribute(
                    "displayedIncidents",
                    filteredIncidents.size()
            );

            request.setAttribute(
                    "newIncidents",
                    countByStatus(
                            allIncidents,
                            IncidentStatus.NEW
                    )
            );

            request.setAttribute(
                    "inProgressIncidents",
                    countByStatus(
                            allIncidents,
                            IncidentStatus.IN_PROGRESS
                    )
            );

            request.setAttribute(
                    "resolvedIncidents",
                    countByStatus(
                            allIncidents,
                            IncidentStatus.RESOLVED
                    )
            );

            request.setAttribute(
                    "closedIncidents",
                    countByStatus(
                            allIncidents,
                            IncidentStatus.CLOSED
                    )
            );

            request.setAttribute(
                    "searchQuery",
                    searchQuery
            );

            request.setAttribute(
                    "selectedStatus",
                    selectedStatus == null
                            ? ""
                            : selectedStatus.name()
            );

            request.setAttribute(
                    "selectedPriority",
                    selectedPriority == null
                            ? ""
                            : selectedPriority.name()
            );

            request.setAttribute(
                    "availableStatuses",
                    IncidentStatus.values()
            );

            request.setAttribute(
                    "availablePriorities",
                    IncidentPriority.values()
            );

            request.getRequestDispatcher(
                    INCIDENT_LIST_VIEW
            ).forward(
                    request,
                    response
            );

        } catch (ValidationException exception) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to load the incident list.",
                    exception
            );

            throw new ServletException(
                    "Unable to load the incident list.",
                    exception
            );
        }
    }

    private List<Incident> filterIncidents(
            List<Incident> incidents,
            String searchQuery,
            IncidentStatus selectedStatus,
            IncidentPriority selectedPriority
    ) {

        List<Incident> filtered =
                new ArrayList<>();

        String normalizedQuery =
                searchQuery.toLowerCase(
                        Locale.ROOT
                );

        for (Incident incident : incidents) {

            boolean statusMatches =
                    selectedStatus == null
                            || incident.getStatus()
                            == selectedStatus;

            boolean priorityMatches =
                    selectedPriority == null
                            || incident.getPriority()
                            == selectedPriority;

            boolean searchMatches =
                    normalizedQuery.isEmpty()
                            || containsIgnoreCase(
                            incident.getTitle(),
                            normalizedQuery
                    )
                            || containsIgnoreCase(
                            incident.getDescription(),
                            normalizedQuery
                    );

            if (statusMatches
                    && priorityMatches
                    && searchMatches) {

                filtered.add(
                        incident
                );
            }
        }

        return filtered;
    }

    private long countByStatus(
            List<Incident> incidents,
            IncidentStatus status
    ) {

        long count = 0;

        for (Incident incident : incidents) {

            if (incident.getStatus()
                    == status) {

                count++;
            }
        }

        return count;
    }

    private IncidentStatus parseOptionalStatus(
            String value
    ) {

        if (value.isEmpty()) {
            return null;
        }

        try {
            return IncidentStatus.valueOf(
                    value.toUpperCase(
                            Locale.ROOT
                    )
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected incident status is invalid."
            );
        }
    }

    private IncidentPriority parseOptionalPriority(
            String value
    ) {

        if (value.isEmpty()) {
            return null;
        }

        try {
            return IncidentPriority.valueOf(
                    value.toUpperCase(
                            Locale.ROOT
                    )
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected incident priority is invalid."
            );
        }
    }

    private boolean containsIgnoreCase(
            String value,
            String normalizedQuery
    ) {

        if (value == null
                || value.isBlank()) {

            return false;
        }

        return value.toLowerCase(
                Locale.ROOT
        ).contains(
                normalizedQuery
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
}