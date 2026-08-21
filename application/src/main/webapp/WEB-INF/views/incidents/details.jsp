<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%@ page import="com.chebo16.metroit.model.Equipment" %>
<%@ page import="com.chebo16.metroit.model.Incident" %>
<%@ page import="com.chebo16.metroit.model.MaintenanceRecord" %>
<%@ page import="com.chebo16.metroit.model.User" %>

<%@ page import="com.chebo16.metroit.model.enums.IncidentPriority" %>
<%@ page import="com.chebo16.metroit.model.enums.IncidentStatus" %>
<%@ page import="com.chebo16.metroit.model.enums.MaintenanceResult" %>

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

    private static String displayValue(
            Object value,
            String emptyValue
    ) {

        if (value == null) {
            return emptyValue;
        }

        String text =
                value.toString().trim();

        if (text.isEmpty()) {
            return emptyValue;
        }

        return escapeHtml(text);
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

    private static String maintenanceResultClass(
            MaintenanceResult result
    ) {

        if (result == null) {
            return "maintenance-result-unknown";
        }

        if (result == MaintenanceResult.SUCCESS) {
            return "maintenance-result-success";
        }

        if (result == MaintenanceResult.PARTIALLY_COMPLETED) {
            return "maintenance-result-partial";
        }

        if (result == MaintenanceResult.FAILED) {
            return "maintenance-result-failed";
        }

        return "maintenance-result-unknown";
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

    boolean administrator =
            sessionUser != null
                    && sessionUser.isAdmin();

    Incident incident = null;

    Object incidentAttribute =
            request.getAttribute(
                    "incident"
            );

    if (incidentAttribute
            instanceof Incident) {

        incident =
                (Incident)
                        incidentAttribute;
    }

    Equipment equipment = null;

    Object equipmentAttribute =
            request.getAttribute(
                    "equipment"
            );

    if (equipmentAttribute
            instanceof Equipment) {

        equipment =
                (Equipment)
                        equipmentAttribute;
    }

    User createdByUser = null;

    Object createdByUserAttribute =
            request.getAttribute(
                    "createdByUser"
            );

    if (createdByUserAttribute
            instanceof User) {

        createdByUser =
                (User)
                        createdByUserAttribute;
    }

    User assignedTechnician = null;

    Object assignedTechnicianAttribute =
            request.getAttribute(
                    "assignedTechnician"
            );

    if (assignedTechnicianAttribute
            instanceof User) {

        assignedTechnician =
                (User)
                        assignedTechnicianAttribute;
    }

    List<User> availableTechnicians =
            Collections.emptyList();

    Object availableTechniciansAttribute =
            request.getAttribute(
                    "availableTechnicians"
            );

    if (availableTechniciansAttribute
            instanceof List) {

        availableTechnicians =
                (List<User>)
                        availableTechniciansAttribute;
    }

    List<MaintenanceRecord> maintenanceRecords =
            Collections.emptyList();

    Object maintenanceRecordsAttribute =
            request.getAttribute(
                    "maintenanceRecords"
            );

    if (maintenanceRecordsAttribute
            instanceof List) {

        maintenanceRecords =
                (List<MaintenanceRecord>)
                        maintenanceRecordsAttribute;
    }

    boolean technicianOwnsIncident =
            false;

    if (!administrator
            && sessionUser != null
            && incident != null
            && incident.getAssignedTechnicianId()
            != null) {

        technicianOwnsIncident =
                incident.getAssignedTechnicianId()
                        .longValue()
                        == sessionUser.getId();
    }

    /*
     * Maintenance records may be added only while
     * the assigned technician is actively working
     * on the incident.
     *
     * NEW         -> not allowed
     * IN_PROGRESS -> allowed
     * RESOLVED    -> not allowed
     * CLOSED      -> not allowed
     */
    boolean maintenanceCanBeAdded =
            technicianOwnsIncident
                    && incident != null
                    && incident.getStatus()
                    == IncidentStatus.IN_PROGRESS;

    String success =
            request.getParameter(
                    "success"
            );
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Incident Details | METRO IT Management
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

    <% if (incident == null) { %>

    <div class="empty-state">

        <h2>
            Incident not available
        </h2>

        <p>
            Incident information could not be loaded.
        </p>

        <a href="<%= contextPath %>/incidents"
           class="button button-secondary">

            Back to incidents

        </a>

    </div>

    <% } else { %>

    <div class="page-header">

        <div>

            <h2>
                Incident #<%= incident.getId() %>
            </h2>

            <p>

                <strong>

                    <%= escapeHtml(
                            incident.getTitle()
                    ) %>

                </strong>

            </p>

        </div>

        <div class="form-actions">

            <a href="<%= contextPath %>/incidents"
               class="button button-secondary">

                Back to incidents

            </a>

            <% if (!administrator) { %>

            <a href="<%= contextPath %>/incidents/my"
               class="button button-secondary">

                My incidents

            </a>

            <% } %>

        </div>

    </div>

    <% if ("assigned".equals(success)) { %>

    <div class="success-message"
         role="status">

        Technician was assigned successfully.

    </div>

    <% } %>

    <% if ("status-updated".equals(success)) { %>

    <div class="success-message"
         role="status">

        Incident status was updated successfully.

    </div>

    <% } %>

    <% if ("maintenance-created".equals(success)) { %>

    <div class="success-message"
         role="status">

        Maintenance record was created successfully.

    </div>

    <% } %>

    <section class="statistics-grid"
             aria-label="Incident summary">

        <article class="statistics-card">

            <span class="statistics-label">
                Priority
            </span>

            <strong>

                <span class="priority-badge
                        <%= getPriorityCssClass(
                                incident.getPriority()
                        ) %>">

                    <%= formatEnum(
                            incident.getPriority()
                    ) %>

                </span>

            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Status
            </span>

            <strong>

                <span class="status-badge
                        <%= getStatusCssClass(
                                incident.getStatus()
                        ) %>">

                    <%= formatEnum(
                            incident.getStatus()
                    ) %>

                </span>

            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Created
            </span>

            <strong>

                <%= displayValue(
                        incident.getCreatedAt(),
                        "Not available"
                ) %>

            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Technician
            </span>

            <strong>

                <% if (assignedTechnician != null) { %>

                <%= escapeHtml(
                        assignedTechnician.getFullName()
                ) %>

                <% } else { %>

                Not assigned

                <% } %>

            </strong>

        </article>

    </section>

    <section class="content-card"
             aria-labelledby="incident-information-title">

        <div class="content-card-header">

            <h2 id="incident-information-title">
                Incident information
            </h2>

        </div>

        <dl>

            <dt>
                ID
            </dt>

            <dd>
                #<%= incident.getId() %>
            </dd>

            <dt>
                Title
            </dt>

            <dd>

                <%= escapeHtml(
                        incident.getTitle()
                ) %>

            </dd>

            <dt>
                Priority
            </dt>

            <dd>

                <span class="priority-badge
                        <%= getPriorityCssClass(
                                incident.getPriority()
                        ) %>">

                    <%= formatEnum(
                            incident.getPriority()
                    ) %>

                </span>

            </dd>

            <dt>
                Status
            </dt>

            <dd>

                <span class="status-badge
                        <%= getStatusCssClass(
                                incident.getStatus()
                        ) %>">

                    <%= formatEnum(
                            incident.getStatus()
                    ) %>

                </span>

            </dd>

            <dt>
                Description
            </dt>

            <dd>

                <%= displayValue(
                        incident.getDescription(),
                        "Not available"
                ) %>

            </dd>

        </dl>

    </section>

    <section class="content-card"
             aria-labelledby="affected-equipment-title">

        <div class="content-card-header">

            <h2 id="affected-equipment-title">
                Affected equipment
            </h2>

        </div>

        <% if (equipment != null) { %>

        <dl>

            <dt>
                Inventory number
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getInventoryNumber(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Equipment name
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getName(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Type
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getType(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Status
            </dt>

            <dd>

                <%= formatEnum(
                        equipment.getStatus()
                ) %>

            </dd>

            <dt>
                Manufacturer
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getManufacturer(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Model
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getModel(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Serial number
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getSerialNumber(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                IP address
            </dt>

            <dd>

                <%= displayValue(
                        equipment.getIpAddress(),
                        "Not available"
                ) %>

            </dd>

        </dl>

        <% } else { %>

        <div class="empty-state">

            <p>
                Equipment information is not available.
            </p>

        </div>

        <% } %>

    </section>

    <section class="content-card"
             aria-labelledby="responsibility-title">

        <div class="content-card-header">

            <h2 id="responsibility-title">
                Responsibility
            </h2>

        </div>

        <dl>

            <dt>
                Created by
            </dt>

            <dd>

                <% if (createdByUser != null) { %>

                <%= escapeHtml(
                        createdByUser.getFullName()
                ) %> (<%= escapeHtml(
                    createdByUser.getUsername()
            ) %>)

                <% } else { %>

                Not available

                <% } %>

            </dd>

            <dt>
                Assigned technician
            </dt>

            <dd>

                <% if (assignedTechnician != null) { %>

                <%= escapeHtml(
                        assignedTechnician.getFullName()
                ) %> (<%= escapeHtml(
                    assignedTechnician.getUsername()
            ) %>)

                <% } else { %>

                Not assigned

                <% } %>

            </dd>

        </dl>

    </section>

    <% if (administrator) { %>

    <section class="content-card"
             aria-labelledby="assign-technician-title">

        <div class="content-card-header">

            <div>

                <h2 id="assign-technician-title">
                    Assign technician
                </h2>

                <span>
                    Assign or reassign responsibility
                    for this incident.
                </span>

            </div>

        </div>

        <% if (incident.getStatus()
                == IncidentStatus.RESOLVED) { %>

        <div class="warning-message">

            This incident is resolved.
            Technician assignment cannot be changed.

        </div>

        <% } else if (incident.getStatus()
                == IncidentStatus.CLOSED) { %>

        <div class="warning-message">

            This incident is closed.
            Technician assignment cannot be changed.

        </div>

        <% } else if (availableTechnicians.isEmpty()) { %>

        <div class="warning-message">

            No active technicians are available.

        </div>

        <% } else { %>

        <form method="post"
              action="<%= contextPath %>/admin/incidents/assign">

            <input type="hidden"
                   name="incidentId"
                   value="<%= incident.getId() %>">

            <div class="form-grid">

                <div class="form-group">

                    <label for="technicianId">
                        Technician
                    </label>

                    <select id="technicianId"
                            name="technicianId"
                            required>

                        <% for (User technician
                                : availableTechnicians) { %>

                        <option
                                value="<%= technician.getId() %>"
                                <%= incident
                                        .getAssignedTechnicianId()
                                        != null
                                        && incident
                                        .getAssignedTechnicianId()
                                        .equals(
                                                technician.getId()
                                        )
                                        ? "selected"
                                        : "" %>>

                            <%= escapeHtml(
                                    technician.getFullName()
                            ) %>

                            -

                            <%= escapeHtml(
                                    technician.getUsername()
                            ) %>

                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">

                        Only active technicians are
                        available for assignment.

                    </small>

                </div>

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="button button-primary">

                    <% if (incident
                            .getAssignedTechnicianId()
                            == null) { %>

                    Assign technician

                    <% } else { %>

                    Reassign technician

                    <% } %>

                </button>

            </div>

        </form>

        <% } %>

    </section>

    <% } %>

    <section class="content-card"
             aria-labelledby="status-actions-title">

        <div class="content-card-header">

            <h2 id="status-actions-title">
                Incident status actions
            </h2>

        </div>

        <% if (administrator
                && incident.getStatus()
                == IncidentStatus.RESOLVED) { %>

        <p>
            The incident has been resolved.
            Close it after reviewing the completed work.
        </p>

        <form method="post"
              action="<%= contextPath %>/incidents/status">

            <input type="hidden"
                   name="incidentId"
                   value="<%= incident.getId() %>">

            <input type="hidden"
                   name="status"
                   value="CLOSED">

            <div class="form-actions">

                <button type="submit"
                        class="button button-primary">

                    Close incident

                </button>

            </div>

        </form>

        <% } else if (technicianOwnsIncident
                && incident.getStatus()
                == IncidentStatus.NEW) { %>

        <p>
            Work on this incident has not started yet.
        </p>

        <form method="post"
              action="<%= contextPath %>/incidents/status">

            <input type="hidden"
                   name="incidentId"
                   value="<%= incident.getId() %>">

            <input type="hidden"
                   name="status"
                   value="IN_PROGRESS">

            <div class="form-actions">

                <button type="submit"
                        class="button button-primary">

                    Start work

                </button>

            </div>

        </form>

        <% } else if (technicianOwnsIncident
                && incident.getStatus()
                == IncidentStatus.IN_PROGRESS) { %>

        <p>
            Work is currently in progress.
            Describe the solution before resolving
            the incident.
        </p>

        <form method="post"
              action="<%= contextPath %>/incidents/status">

            <input type="hidden"
                   name="incidentId"
                   value="<%= incident.getId() %>">

            <input type="hidden"
                   name="status"
                   value="RESOLVED">

            <div class="form-group form-group-full">

                <div>
                    <label for="solutionDescription">
                        Solution description
                    </label>
                </div>

                <textarea id="solutionDescription"
                          name="solutionDescription"
                          rows="6"
                          required
                          placeholder="Describe the performed work and final solution..."></textarea>

                <small class="form-help">

                    A solution description is required
                    before resolving the incident.

                </small>

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="button button-primary">

                    Resolve incident

                </button>

            </div>

        </form>

        <% } else if (technicianOwnsIncident
                && incident.getStatus()
                == IncidentStatus.RESOLVED) { %>

        <div class="success-message">

            This incident has been resolved and is
            waiting for administrator closure.

        </div>

        <% } else if (incident.getStatus()
                == IncidentStatus.CLOSED) { %>

        <div class="success-message">

            This incident is closed.

        </div>

        <% } else { %>

        <div class="warning-message">

            No status action is currently available.

        </div>

        <% } %>

    </section>

    <% if (maintenanceCanBeAdded) { %>

    <section class="content-card"
             aria-labelledby="maintenance-action-title">

        <div class="content-card-header">

            <div>

                <h2 id="maintenance-action-title">
                    Maintenance action
                </h2>

                <span>
                    Record work performed for this
                    incident.
                </span>

            </div>

        </div>

        <p>
            Record diagnostics, repair,
            configuration or component replacement
            performed for this incident.
        </p>

        <div class="form-actions">

            <a href="<%= contextPath %>/maintenance/create?incidentId=<%= incident.getId() %>"
               class="button button-primary">

                Add maintenance record

            </a>

        </div>

    </section>

    <% } %>

    <section class="content-card"
             aria-labelledby="incident-timeline-title">

        <div class="content-card-header">

            <h2 id="incident-timeline-title">
                Incident timeline
            </h2>

        </div>

        <dl>

            <dt>
                Created at
            </dt>

            <dd>

                <%= displayValue(
                        incident.getCreatedAt(),
                        "Not available"
                ) %>

            </dd>

            <dt>
                Started at
            </dt>

            <dd>

                <%= displayValue(
                        incident.getStartedAt(),
                        "Not started"
                ) %>

            </dd>

            <dt>
                Resolved at
            </dt>

            <dd>

                <%= displayValue(
                        incident.getResolvedAt(),
                        "Not resolved"
                ) %>

            </dd>

            <dt>
                Closed at
            </dt>

            <dd>

                <%= displayValue(
                        incident.getClosedAt(),
                        "Not closed"
                ) %>

            </dd>

        </dl>

    </section>

    <section class="content-card"
             aria-labelledby="solution-title">

        <div class="content-card-header">

            <h2 id="solution-title">
                Solution
            </h2>

        </div>

        <% if (incident.getSolutionDescription()
                != null
                && !incident
                .getSolutionDescription()
                .isBlank()) { %>

        <p>

            <%= escapeHtml(
                    incident.getSolutionDescription()
            ) %>

        </p>

        <% } else { %>

        <div class="empty-state">

            <p>
                No solution has been recorded yet.
            </p>

        </div>

        <% } %>

    </section>

    <section class="content-card"
             aria-labelledby="maintenance-history-title">

        <div class="content-card-header">

            <div>

                <h2 id="maintenance-history-title">
                    Maintenance history
                </h2>

                <span>

                    <%= maintenanceRecords.size() %>
                    record(s)

                </span>

            </div>

            <a href="<%= contextPath %>/maintenance"
               class="button button-secondary">

                View maintenance history

            </a>

        </div>

        <% if (maintenanceRecords.isEmpty()) { %>

        <div class="empty-state">

            <h3>
                No maintenance records
            </h3>

            <p>
                No maintenance work has been recorded
                for this incident yet.
            </p>

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
                        Performed at
                    </th>

                    <% if (administrator) { %>

                    <th scope="col">
                        Technician ID
                    </th>

                    <% } %>

                    <th scope="col">
                        Work description
                    </th>

                    <th scope="col">
                        Replaced components
                    </th>

                    <th scope="col">
                        Result
                    </th>

                </tr>

                </thead>

                <tbody>

                <% for (MaintenanceRecord record
                        : maintenanceRecords) { %>

                <tr>

                    <td>
                        #<%= record.getId() %>
                    </td>

                    <td>

                        <%= displayValue(
                                record.getPerformedAt(),
                                "Not available"
                        ) %>

                    </td>

                    <% if (administrator) { %>

                    <td>
                        #<%= record.getTechnicianId() %>
                    </td>

                    <% } %>

                    <td>

                        <%= displayValue(
                                record.getWorkDescription(),
                                "Not available"
                        ) %>

                    </td>

                    <td>

                        <% if (record
                                .getReplacedComponents()
                                != null
                                && !record
                                .getReplacedComponents()
                                .isBlank()) { %>

                        <%= escapeHtml(
                                record.getReplacedComponents()
                        ) %>

                        <% } else { %>

                        None

                        <% } %>

                    </td>

                    <td>

                        <span class="maintenance-result-badge <%= maintenanceResultClass(
                                record.getResult()
                        ) %>">

                            <%= formatEnum(
                                    record.getResult()
                            ) %>

                        </span>

                    </td>

                </tr>

                <% } %>

                </tbody>

            </table>

        </div>

        <% } %>

    </section>

    <div class="form-actions">

        <a href="<%= contextPath %>/incidents"
           class="button button-secondary">

            Back to incidents

        </a>

        <% if (!administrator) { %>

        <a href="<%= contextPath %>/incidents/my"
           class="button button-secondary">

            Back to my incidents

        </a>

        <% } %>

    </div>

    <% } %>

</main>

</body>

</html>
