<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.Incident" %>
<%@ page import="com.chebo16.metroit.model.enums.IncidentPriority" %>
<%@ page import="com.chebo16.metroit.model.enums.IncidentStatus" %>
<%@ page import="com.chebo16.metroit.web.session.SessionConstants" %>
<%@ page import="com.chebo16.metroit.web.session.SessionUser" %>

<%!
    private static String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }

        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static long getLongAttribute(Object attribute) {
        if (attribute instanceof Number) {
            Number number = (Number) attribute;
            return number.longValue();
        }

        return 0L;
    }

    private static String formatStatus(IncidentStatus status) {
        if (status == null) {
            return "";
        }

        return status.name().replace('_', ' ');
    }

    private static String formatPriority(IncidentPriority priority) {
        if (priority == null) {
            return "";
        }

        return priority.name().replace('_', ' ');
    }

    private static String getStatusCssClass(IncidentStatus status) {
        if (status == null) {
            return "status-inactive";
        }

        if (status == IncidentStatus.NEW) {
            return "status-new";
        }

        if (status == IncidentStatus.IN_PROGRESS) {
            return "status-in-progress";
        }

        if (status == IncidentStatus.RESOLVED) {
            return "status-resolved";
        }

        if (status == IncidentStatus.CLOSED) {
            return "status-closed";
        }

        return "status-inactive";
    }

    private static String getPriorityCssClass(
            IncidentPriority priority
    ) {
        if (priority == null) {
            return "priority-medium";
        }

        if (priority == IncidentPriority.LOW) {
            return "priority-low";
        }

        if (priority == IncidentPriority.MEDIUM) {
            return "priority-medium";
        }

        if (priority == IncidentPriority.HIGH) {
            return "priority-high";
        }

        if (priority == IncidentPriority.CRITICAL) {
            return "priority-critical";
        }

        return "priority-medium";
    }

    private static String shortenText(
            String value,
            int maximumLength
    ) {
        if (value == null) {
            return "";
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() <= maximumLength) {
            return normalizedValue;
        }

        return normalizedValue.substring(0, maximumLength) + "...";
    }
%>

<%
    String contextPath = request.getContextPath();

    Object authenticatedUserAttribute = session.getAttribute(
            SessionConstants.AUTHENTICATED_USER
    );

    SessionUser sessionUser = null;

    if (authenticatedUserAttribute instanceof SessionUser) {
        sessionUser = (SessionUser) authenticatedUserAttribute;
    }

    boolean administrator =
            sessionUser != null && sessionUser.isAdmin();

    List<Incident> incidents =
            Collections.emptyList();

    Object incidentsAttribute =
            request.getAttribute("incidents");

    if (incidentsAttribute instanceof List) {
        incidents = (List<Incident>) incidentsAttribute;
    }

    long totalIncidents =
            getLongAttribute(request.getAttribute("totalIncidents"));

    long displayedIncidents =
            getLongAttribute(request.getAttribute("displayedIncidents"));

    long newIncidents =
            getLongAttribute(request.getAttribute("newIncidents"));

    long inProgressIncidents =
            getLongAttribute(request.getAttribute("inProgressIncidents"));

    long resolvedIncidents =
            getLongAttribute(request.getAttribute("resolvedIncidents"));

    long closedIncidents =
            getLongAttribute(request.getAttribute("closedIncidents"));

    String searchQuery =
            escapeHtml(request.getAttribute("searchQuery"));

    String selectedStatus =
            escapeHtml(request.getAttribute("selectedStatus"));

    String selectedPriority =
            escapeHtml(request.getAttribute("selectedPriority"));

    IncidentStatus[] availableStatuses =
            IncidentStatus.values();

    Object statusesAttribute =
            request.getAttribute("availableStatuses");

    if (statusesAttribute instanceof IncidentStatus[]) {
        availableStatuses =
                (IncidentStatus[]) statusesAttribute;
    }

    IncidentPriority[] availablePriorities =
            IncidentPriority.values();

    Object prioritiesAttribute =
            request.getAttribute("availablePriorities");

    if (prioritiesAttribute instanceof IncidentPriority[]) {
        availablePriorities =
                (IncidentPriority[]) prioritiesAttribute;
    }

    String successParameter =
            request.getParameter("success");

    String successMessage = "";

    if ("created".equals(successParameter)) {
        successMessage =
                "Incident was created successfully.";
    } else if ("updated".equals(successParameter)) {
        successMessage =
                "Incident was updated successfully.";
    } else if ("assigned".equals(successParameter)) {
        successMessage =
                "Incident was assigned successfully.";
    } else if ("status-updated".equals(successParameter)) {
        successMessage =
                "Incident status was updated successfully.";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Incidents | METRO IT Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">
</head>

<body>

<header>
    <h1>
        <a href="<%= contextPath %>/">
            METRO IT Management
        </a>
    </h1>

    <% if (sessionUser != null) { %>

    <p>
        Logged in as:
        <strong><%= escapeHtml(sessionUser.getFullName()) %></strong>
        -
        <%= escapeHtml(sessionUser.getRole()) %>
    </p>

    <% } %>

    <nav>
        <a href="<%= contextPath %>/">
            Dashboard
        </a>

        <% if (administrator) { %>

        <a href="<%= contextPath %>/admin/users">
            Users
        </a>

        <% } %>

        <a href="<%= contextPath %>/equipment">
            Equipment
        </a>

        <a href="<%= contextPath %>/incidents">
            Incidents
        </a>

        <% if (!administrator) { %>

        <a href="<%= contextPath %>/incidents/my">
            My incidents
        </a>

        <% } %>

        <a href="<%= contextPath %>/maintenance">
            Maintenance
        </a>

        <form method="post"
              action="<%= contextPath %>/logout"
              class="inline-form">

            <button type="submit"
                    class="button button-secondary">
                Sign out
            </button>
        </form>
    </nav>
</header>

<hr>

<main>

    <%@ include file="/WEB-INF/views/common/navigation.jspf" %>

    <div class="page-header">
        <div>
            <% if (administrator) { %>

            <h2>
                Incident management
            </h2>

            <% } else { %>

            <h2>
                Incidents
            </h2>

            <% } %>

            <p>
                View and track IT incidents registered
                in the system.
            </p>
        </div>

        <% if (administrator) { %>

        <a href="<%= contextPath %>/admin/incidents/create"
           class="button button-primary">
            Create incident
        </a>

        <% } %>
    </div>

    <% if (!successMessage.isEmpty()) { %>

    <div class="success-message"
         role="status">
        <%= escapeHtml(successMessage) %>
    </div>

    <% } %>

    <section class="statistics-grid"
             aria-label="Incident statistics">

        <article class="statistics-card">
            <span class="statistics-label">
                Total incidents
            </span>

            <strong class="statistics-value">
                <%= totalIncidents %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                New
            </span>

            <strong class="statistics-value">
                <%= newIncidents %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                In progress
            </span>

            <strong class="statistics-value">
                <%= inProgressIncidents %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Resolved
            </span>

            <strong class="statistics-value">
                <%= resolvedIncidents %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Closed
            </span>

            <strong class="statistics-value">
                <%= closedIncidents %>
            </strong>
        </article>

    </section>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <h2>
                    Search and filters
                </h2>

                <span>
                    Search incidents by title or
                    description and filter by status
                    and priority.
                </span>
            </div>
        </div>

        <form method="get"
              action="<%= contextPath %>/incidents"
              class="filter-form">

            <div class="filter-grid">

                <div class="form-group">
                    <label for="q">
                        Search
                    </label>

                    <input type="search"
                           id="q"
                           name="q"
                           value="<%= searchQuery %>"
                           placeholder="Incident title or description..."
                           maxlength="150">
                </div>

                <div class="form-group">
                    <label for="status">
                        Status
                    </label>

                    <select id="status"
                            name="status">

                        <option value="">
                            All statuses
                        </option>

                        <% for (IncidentStatus status : availableStatuses) { %>

                        <option value="<%= status.name() %>"
                                <%= status.name().equals(selectedStatus)
                                        ? "selected"
                                        : "" %>>
                            <%= escapeHtml(formatStatus(status)) %>
                        </option>

                        <% } %>

                    </select>
                </div>

                <div class="form-group">
                    <label for="priority">
                        Priority
                    </label>

                    <select id="priority"
                            name="priority">

                        <option value="">
                            All priorities
                        </option>

                        <% for (IncidentPriority priority : availablePriorities) { %>

                        <option value="<%= priority.name() %>"
                                <%= priority.name().equals(selectedPriority)
                                        ? "selected"
                                        : "" %>>
                            <%= escapeHtml(formatPriority(priority)) %>
                        </option>

                        <% } %>

                    </select>
                </div>

            </div>

            <div class="form-actions">
                <button type="submit"
                        class="button button-primary">
                    Apply filters
                </button>

                <a href="<%= contextPath %>/incidents"
                   class="button button-secondary">
                    Reset
                </a>
            </div>
        </form>
    </section>

    <section class="content-card"
             aria-labelledby="incident-table-title">

        <div class="content-card-header">
            <h2 id="incident-table-title">
                Registered incidents
            </h2>

            <span>
                Showing
                <%= displayedIncidents %>
                of
                <%= totalIncidents %>
                incident(s)
            </span>
        </div>

        <% if (incidents.isEmpty()) { %>

        <div class="empty-state">
            <h3>
                No incidents found
            </h3>

            <p>
                No incidents match the selected
                search and filter criteria.
            </p>

            <a href="<%= contextPath %>/incidents"
               class="button button-secondary">
                Clear filters
            </a>

            <% if (administrator) { %>

            <a href="<%= contextPath %>/admin/incidents/create"
               class="button button-primary">
                Create incident
            </a>

            <% } %>
        </div>

        <% } else { %>

        <div class="table-container">
            <table class="data-table">
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Incident</th>
                    <th scope="col">Priority</th>
                    <th scope="col">Status</th>
                    <th scope="col">Created</th>
                    <th scope="col">Actions</th>
                </tr>
                </thead>

                <tbody>

                <% for (Incident incident : incidents) { %>

                <tr>
                    <td>
                        #<%= incident.getId() %>
                    </td>

                    <td>
                        <strong>
                            <%= escapeHtml(incident.getTitle()) %>
                        </strong>

                        <div class="table-secondary-text">
                            <%= escapeHtml(
                                    shortenText(
                                            incident.getDescription(),
                                            120
                                    )
                            ) %>
                        </div>
                    </td>

                    <td>
                        <span class="priority-badge
                                <%= getPriorityCssClass(
                                        incident.getPriority()
                                ) %>">

                            <%= escapeHtml(
                                    formatPriority(
                                            incident.getPriority()
                                    )
                            ) %>
                        </span>
                    </td>

                    <td>
                        <span class="status-badge
                                <%= getStatusCssClass(
                                        incident.getStatus()
                                ) %>">

                            <%= escapeHtml(
                                    formatStatus(
                                            incident.getStatus()
                                    )
                            ) %>
                        </span>
                    </td>

                    <td>
                        <%= escapeHtml(incident.getCreatedAt()) %>
                    </td>

                    <td>
                        <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>"
                           class="button button-small button-secondary">
                            Details
                        </a>
                    </td>
                </tr>

                <% } %>

                </tbody>
            </table>
        </div>

        <% } %>

    </section>

</main>

</body>

</html>