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
    private static String escapeHtml(
            Object value
    ) {

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

    private static String formatEnum(
            Object value
    ) {

        if (value == null) {
            return "";
        }

        return escapeHtml(
                value.toString()
                        .replace('_', ' ')
        );
    }

    private static String getStatusCssClass(
            IncidentStatus status
    ) {

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
%>

<%
    String contextPath =
            request.getContextPath();

    SessionUser sessionUser = null;

    Object authenticatedUserAttribute =
            session.getAttribute(
                    SessionConstants.AUTHENTICATED_USER
            );

    if (authenticatedUserAttribute
            instanceof SessionUser) {

        sessionUser =
                (SessionUser)
                        authenticatedUserAttribute;
    }

    List<Incident> incidents =
            Collections.emptyList();

    Object incidentsAttribute =
            request.getAttribute(
                    "incidents"
            );

    if (incidentsAttribute
            instanceof List) {

        incidents =
                (List<Incident>)
                        incidentsAttribute;
    }

    long totalIncidents = 0;
    long newIncidents = 0;
    long inProgressIncidents = 0;
    long resolvedIncidents = 0;
    long closedIncidents = 0;

    Object totalIncidentsAttribute =
            request.getAttribute(
                    "totalIncidents"
            );

    if (totalIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        totalIncidentsAttribute;

        totalIncidents =
                number.longValue();
    }

    Object newIncidentsAttribute =
            request.getAttribute(
                    "newIncidents"
            );

    if (newIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        newIncidentsAttribute;

        newIncidents =
                number.longValue();
    }

    Object inProgressIncidentsAttribute =
            request.getAttribute(
                    "inProgressIncidents"
            );

    if (inProgressIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        inProgressIncidentsAttribute;

        inProgressIncidents =
                number.longValue();
    }

    Object resolvedIncidentsAttribute =
            request.getAttribute(
                    "resolvedIncidents"
            );

    if (resolvedIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        resolvedIncidentsAttribute;

        resolvedIncidents =
                number.longValue();
    }

    Object closedIncidentsAttribute =
            request.getAttribute(
                    "closedIncidents"
            );

    if (closedIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        closedIncidentsAttribute;

        closedIncidents =
                number.longValue();
    }

    String search = "";

    Object searchAttribute =
            request.getAttribute(
                    "search"
            );

    if (searchAttribute
            instanceof String) {

        search =
                (String)
                        searchAttribute;
    }

    String selectedStatus = "";

    Object selectedStatusAttribute =
            request.getAttribute(
                    "selectedStatus"
            );

    if (selectedStatusAttribute
            instanceof String) {

        selectedStatus =
                (String)
                        selectedStatusAttribute;
    }

    String selectedPriority = "";

    Object selectedPriorityAttribute =
            request.getAttribute(
                    "selectedPriority"
            );

    if (selectedPriorityAttribute
            instanceof String) {

        selectedPriority =
                (String)
                        selectedPriorityAttribute;
    }

    IncidentStatus[] statuses =
            IncidentStatus.values();

    Object statusesAttribute =
            request.getAttribute(
                    "statuses"
            );

    if (statusesAttribute
            instanceof IncidentStatus[]) {

        statuses =
                (IncidentStatus[])
                        statusesAttribute;
    }

    IncidentPriority[] priorities =
            IncidentPriority.values();

    Object prioritiesAttribute =
            request.getAttribute(
                    "priorities"
            );

    if (prioritiesAttribute
            instanceof IncidentPriority[]) {

        priorities =
                (IncidentPriority[])
                        prioritiesAttribute;
    }
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        My Incidents | METRO IT Management
    </title>

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

        <strong>
            <%= escapeHtml(
                    sessionUser.getFullName()
            ) %>
        </strong>

        -

        <%= escapeHtml(
                sessionUser.getRole()
        ) %>

    </p>

    <% } %>

    <nav>

        <a href="<%= contextPath %>/">
            Dashboard
        </a>

        <a href="<%= contextPath %>/equipment">
            Equipment
        </a>

        <a href="<%= contextPath %>/incidents">
            Incidents
        </a>

        <a href="<%= contextPath %>/incidents/my">
            My incidents
        </a>

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

            <h2>
                My assigned incidents
            </h2>

            <p>
                Incidents assigned to your
                technician account.
            </p>

        </div>

    </div>

    <section class="statistics-grid"
             aria-label="Assigned incident statistics">

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
                    Search assigned incidents by title
                    or description and filter them by
                    status and priority.
                </span>

            </div>

        </div>

        <form method="get"
              action="<%= contextPath %>/incidents/my"
              class="filter-form">

            <div class="filter-grid">

                <div class="form-group">

                    <label for="search">
                        Search
                    </label>

                    <input type="search"
                           id="search"
                           name="search"
                           value="<%= escapeHtml(search) %>"
                           placeholder="Title or description"
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

                        <% for (IncidentStatus status
                                : statuses) { %>

                        <option value="<%= status.name() %>"
                                <%= status.name()
                                        .equalsIgnoreCase(
                                                selectedStatus
                                        )
                                        ? "selected"
                                        : "" %>>

                            <%= formatEnum(status) %>

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

                        <% for (IncidentPriority priority
                                : priorities) { %>

                        <option value="<%= priority.name() %>"
                                <%= priority.name()
                                        .equalsIgnoreCase(
                                                selectedPriority
                                        )
                                        ? "selected"
                                        : "" %>>

                            <%= formatEnum(priority) %>

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

                <a href="<%= contextPath %>/incidents/my"
                   class="button button-secondary">

                    Reset

                </a>

            </div>

        </form>

    </section>

    <section class="content-card"
             aria-labelledby="assigned-incidents-title">

        <div class="content-card-header">

            <h2 id="assigned-incidents-title">
                Assigned incidents
            </h2>

            <span>
                <%= incidents.size() %> incident(s) displayed
            </span>

        </div>

        <% if (incidents.isEmpty()) { %>

        <div class="empty-state">

            <h3>
                No incidents found
            </h3>

            <p>
                No assigned incidents match the
                selected search and filter criteria.
            </p>

            <a href="<%= contextPath %>/incidents/my"
               class="button button-secondary">

                Clear filters

            </a>

        </div>

        <% } else { %>

        <div class="table-container">

            <table class="data-table">

                <thead>

                <tr>

                    <th scope="col">
                        ID
                    </th>

                    <th scope="col">
                        Incident
                    </th>

                    <th scope="col">
                        Priority
                    </th>

                    <th scope="col">
                        Status
                    </th>

                    <th scope="col">
                        Equipment ID
                    </th>

                    <th scope="col">
                        Created
                    </th>

                    <th scope="col">
                        Actions
                    </th>

                </tr>

                </thead>

                <tbody>

                <% for (Incident incident
                        : incidents) { %>

                <tr>

                    <td>
                        #<%= incident.getId() %>
                    </td>

                    <td>

                        <strong>

                            <%= escapeHtml(
                                    incident.getTitle()
                            ) %>

                        </strong>

                        <% if (incident.getDescription()
                                != null
                                && !incident
                                .getDescription()
                                .isBlank()) { %>

                        <div class="table-secondary-text">

                            <%= escapeHtml(
                                    incident.getDescription()
                            ) %>

                        </div>

                        <% } %>

                    </td>

                    <td>

                        <span class="priority-badge
                                <%= getPriorityCssClass(
                                        incident.getPriority()
                                ) %>">

                            <%= formatEnum(
                                    incident.getPriority()
                            ) %>

                        </span>

                    </td>

                    <td>

                        <span class="status-badge
                                <%= getStatusCssClass(
                                        incident.getStatus()
                                ) %>">

                            <%= formatEnum(
                                    incident.getStatus()
                            ) %>

                        </span>

                    </td>

                    <td>
                        #<%= incident.getEquipmentId() %>
                    </td>

                    <td>

                        <% if (incident.getCreatedAt()
                                != null) { %>

                        <%= escapeHtml(
                                incident.getCreatedAt()
                        ) %>

                        <% } else { %>

                        Not available

                        <% } %>

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
