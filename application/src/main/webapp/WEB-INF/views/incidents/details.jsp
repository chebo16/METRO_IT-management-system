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
            && incident.getAssignedTechnicianId() != null) {

        technicianOwnsIncident =
                incident.getAssignedTechnicianId()
                        .longValue()
                        == sessionUser.getId();
    }

    boolean maintenanceCanBeAdded =
            technicianOwnsIncident
                    && incident != null
                    && incident.getStatus()
                    != IncidentStatus.NEW
                    && incident.getStatus()
                    != IncidentStatus.CLOSED;

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
        METRO IT Management
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

        |

        <% if (administrator) { %>

        <a href="<%= contextPath %>/admin/users">
            Users
        </a>

        |

        <% } %>

        <a href="<%= contextPath %>/equipment">
            Equipment
        </a>

        |

        <a href="<%= contextPath %>/incidents">
            Incidents
        </a>

        |

        <% if (!administrator) { %>

        <a href="<%= contextPath %>/incidents/my">
            My incidents
        </a>

        |

        <% } %>

        <a href="<%= contextPath %>/maintenance">
            Maintenance
        </a>

        |

        <form method="post"
              action="<%= contextPath %>/logout"
              style="display: inline;">

            <button type="submit">
                Sign out
            </button>

        </form>

    </nav>

</header>

<hr>

<main>

    <% if (incident == null) { %>

    <h2>
        Incident not available
    </h2>

    <p>
        Incident information could not be loaded.
    </p>

    <% } else { %>

    <h2>
        Incident #<%= incident.getId() %>
    </h2>

    <h3>
        <%= escapeHtml(
                incident.getTitle()
        ) %>
    </h3>

    <% if ("assigned".equals(success)) { %>

    <p>
        <strong>
            Technician was assigned successfully.
        </strong>
    </p>

    <% } %>

    <% if ("status-updated".equals(success)) { %>

    <p>
        <strong>
            Incident status was updated successfully.
        </strong>
    </p>

    <% } %>

    <% if ("maintenance-created".equals(success)) { %>

    <p>
        <strong>
            Maintenance record was created successfully.
        </strong>
    </p>

    <% } %>

    <p>

        <strong>
            Priority:
        </strong>

        <%= formatEnum(
                incident.getPriority()
        ) %>

    </p>

    <p>

        <strong>
            Status:
        </strong>

        <%= formatEnum(
                incident.getStatus()
        ) %>

    </p>

    <p>

        <strong>
            Created:
        </strong>

        <%= displayValue(
                incident.getCreatedAt(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Technician:
        </strong>

        <% if (assignedTechnician != null) { %>

        <%= escapeHtml(
                assignedTechnician.getFullName()
        ) %>

        <% } else { %>

        Not assigned

        <% } %>

    </p>

    <hr>

    <h3>
        Incident information
    </h3>

    <p>

        <strong>
            ID:
        </strong>

        <%= incident.getId() %>

    </p>

    <p>

        <strong>
            Title:
        </strong>

        <%= escapeHtml(
                incident.getTitle()
        ) %>

    </p>

    <p>

        <strong>
            Priority:
        </strong>

        <%= formatEnum(
                incident.getPriority()
        ) %>

    </p>

    <p>

        <strong>
            Status:
        </strong>

        <%= formatEnum(
                incident.getStatus()
        ) %>

    </p>

    <p>

        <strong>
            Description:
        </strong>

        <%= escapeHtml(
                incident.getDescription()
        ) %>

    </p>

    <hr>

    <h3>
        Affected equipment
    </h3>

    <% if (equipment != null) { %>

    <p>

        <strong>
            Inventory number:
        </strong>

        <%= displayValue(
                equipment.getInventoryNumber(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Equipment name:
        </strong>

        <%= displayValue(
                equipment.getName(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Type:
        </strong>

        <%= displayValue(
                equipment.getType(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Status:
        </strong>

        <%= formatEnum(
                equipment.getStatus()
        ) %>

    </p>

    <p>

        <strong>
            Manufacturer:
        </strong>

        <%= displayValue(
                equipment.getManufacturer(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Model:
        </strong>

        <%= displayValue(
                equipment.getModel(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Serial number:
        </strong>

        <%= displayValue(
                equipment.getSerialNumber(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            IP address:
        </strong>

        <%= displayValue(
                equipment.getIpAddress(),
                "Not available"
        ) %>

    </p>

    <% } else { %>

    <p>
        Equipment information is not available.
    </p>

    <% } %>

    <hr>

    <h3>
        Responsibility
    </h3>

    <p>

        <strong>
            Created by:
        </strong>

        <% if (createdByUser != null) { %>

        <%= escapeHtml(
                createdByUser.getFullName()
        ) %>

        (
        <%= escapeHtml(
                createdByUser.getUsername()
        ) %>
        )

        <% } else { %>

        Not available

        <% } %>

    </p>

    <p>

        <strong>
            Assigned technician:
        </strong>

        <% if (assignedTechnician != null) { %>

        <%= escapeHtml(
                assignedTechnician.getFullName()
        ) %>

        (
        <%= escapeHtml(
                assignedTechnician.getUsername()
        ) %>
        )

        <% } else { %>

        Not assigned

        <% } %>

    </p>

    <% if (administrator) { %>

    <hr>

    <h3>
        Assign technician
    </h3>

    <% if (incident.getStatus()
            == IncidentStatus.CLOSED) { %>

    <p>
        This incident is closed.
        Technician assignment cannot be changed.
    </p>

    <% } else if (availableTechnicians.isEmpty()) { %>

    <p>
        No active technicians are available.
    </p>

    <% } else { %>

    <p>
        Assign or reassign an active technician
        to this incident.
    </p>

    <form method="post"
          action="<%= contextPath %>/admin/incidents/assign">

        <input type="hidden"
               name="incidentId"
               value="<%= incident.getId() %>">

        <p>

            <label for="technicianId">
                Technician:
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

        </p>

        <p>

            <button type="submit">

                <% if (incident
                        .getAssignedTechnicianId()
                        == null) { %>

                Assign technician

                <% } else { %>

                Reassign technician

                <% } %>

            </button>

        </p>

    </form>

    <% } %>

    <% } %>

    <hr>

    <h3>
        Incident status actions
    </h3>

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

        <button type="submit">
            Close incident
        </button>

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

        <button type="submit">
            Start work
        </button>

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

        <p>

            <label for="solutionDescription">

                <strong>
                    Solution description:
                </strong>

            </label>

        </p>

        <p>

                    <textarea id="solutionDescription"
                              name="solutionDescription"
                              rows="6"
                              cols="80"
                              required></textarea>

        </p>

        <button type="submit">
            Resolve incident
        </button>

    </form>

    <% } else if (technicianOwnsIncident
            && incident.getStatus()
            == IncidentStatus.RESOLVED) { %>

    <p>
        This incident has been resolved
        and is waiting for administrator closure.
    </p>

    <% } else if (incident.getStatus()
            == IncidentStatus.CLOSED) { %>

    <p>
        This incident is closed.
    </p>

    <% } else { %>

    <p>
        No status action is currently available.
    </p>

    <% } %>

    <% if (maintenanceCanBeAdded) { %>

    <hr>

    <h3>
        Maintenance action
    </h3>

    <p>
        Record diagnostics, repair,
        configuration or component replacement
        performed for this incident.
    </p>

    <p>

        <a href="<%= contextPath %>/maintenance/create?incidentId=<%= incident.getId() %>">
            Add maintenance record
        </a>

    </p>

    <% } %>

    <hr>

    <h3>
        Incident timeline
    </h3>

    <p>

        <strong>
            Created at:
        </strong>

        <%= displayValue(
                incident.getCreatedAt(),
                "Not available"
        ) %>

    </p>

    <p>

        <strong>
            Started at:
        </strong>

        <%= displayValue(
                incident.getStartedAt(),
                "Not started"
        ) %>

    </p>

    <p>

        <strong>
            Resolved at:
        </strong>

        <%= displayValue(
                incident.getResolvedAt(),
                "Not resolved"
        ) %>

    </p>

    <p>

        <strong>
            Closed at:
        </strong>

        <%= displayValue(
                incident.getClosedAt(),
                "Not closed"
        ) %>

    </p>

    <hr>

    <h3>
        Solution
    </h3>

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

    <p>
        No solution has been recorded yet.
    </p>

    <% } %>

    <hr>

    <h3>
        Maintenance history
    </h3>

    <p>
        Maintenance activities recorded
        for this incident:
        <strong>
            <%= maintenanceRecords.size() %>
        </strong>
    </p>

    <% if (maintenanceRecords.isEmpty()) { %>

    <p>
        No maintenance work has been recorded
        for this incident yet.
    </p>

    <% } else { %>

    <table border="1"
           cellpadding="8"
           cellspacing="0">

        <thead>

        <tr>

            <th>
                ID
            </th>

            <th>
                Performed at
            </th>

            <th>
                Technician ID
            </th>

            <th>
                Work description
            </th>

            <th>
                Replaced components
            </th>

            <th>
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

            <td>
                #<%= record.getTechnicianId() %>
            </td>

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
                <%= formatEnum(
                        record.getResult()
                ) %>
            </td>

        </tr>

        <% } %>

        </tbody>

    </table>

    <% } %>

    <p>

        <a href="<%= contextPath %>/maintenance">
            View maintenance history
        </a>

    </p>

    <hr>

    <p>

        <a href="<%= contextPath %>/incidents">
            Back to incidents
        </a>

    </p>

    <% if (!administrator) { %>

    <p>

        <a href="<%= contextPath %>/incidents/my">
            Back to my incidents
        </a>

    </p>

    <% } %>

    <% } %>

</main>

</body>

</html>
