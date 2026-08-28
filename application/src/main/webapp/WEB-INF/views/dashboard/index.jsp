<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.web.session.SessionConstants" %>
<%@ page import="com.chebo16.metroit.web.session.SessionUser" %>
<%@ page import="java.util.Locale" %>

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

    private static long longValue(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            return number.longValue();
        }

        return 0L;
    }

    private static double percentage(long value, long total) {
        if (total <= 0) {
            return 0.0;
        }

        return value * 100.0 / total;
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

    if (sessionUser == null) {
        response.sendRedirect(contextPath + "/login");
        return;
    }

    boolean administrator = sessionUser.isAdmin();

    long totalUsers =
            longValue(request.getAttribute("totalUsers"));

    long activeUsers =
            longValue(request.getAttribute("activeUsers"));

    long administratorUsers =
            longValue(request.getAttribute("administratorUsers"));

    long technicianUsers =
            longValue(request.getAttribute("technicianUsers"));

    long totalEquipment =
            longValue(request.getAttribute("totalEquipment"));

    long activeEquipment =
            longValue(request.getAttribute("activeEquipment"));

    long equipmentInRepair =
            longValue(request.getAttribute("equipmentInRepair"));

    long inactiveEquipment =
            longValue(request.getAttribute("inactiveEquipment"));

    long decommissionedEquipment =
            longValue(request.getAttribute("decommissionedEquipment"));

    long totalIncidents =
            longValue(request.getAttribute("totalIncidents"));

    long newIncidents =
            longValue(request.getAttribute("newIncidents"));

    long inProgressIncidents =
            longValue(request.getAttribute("inProgressIncidents"));

    long resolvedIncidents =
            longValue(request.getAttribute("resolvedIncidents"));

    long closedIncidents =
            longValue(request.getAttribute("closedIncidents"));

    long totalMaintenanceRecords =
            longValue(request.getAttribute("totalMaintenanceRecords"));

    long successfulMaintenance =
            longValue(request.getAttribute("successfulMaintenance"));

    long partiallyCompletedMaintenance =
            longValue(request.getAttribute("partiallyCompletedMaintenance"));

    long failedMaintenance =
            longValue(request.getAttribute("failedMaintenance"));

    double incidentClosureRate =
            percentage(closedIncidents, totalIncidents);

    double maintenanceSuccessRate =
            percentage(
                    successfulMaintenance,
                    totalMaintenanceRecords
            );

    String incidentClosureRateText =
            String.format(
                    Locale.US,
                    "%.1f",
                    incidentClosureRate
            );

    String maintenanceSuccessRateText =
            String.format(
                    Locale.US,
                    "%.1f",
                    maintenanceSuccessRate
            );
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Dashboard | METRO IT Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">
</head>

<body>

<header>
    <h1>
        METRO IT Management
    </h1>

    <p>
        Logged in as:
        <strong><%= escapeHtml(sessionUser.getFullName()) %></strong>
        -
        <%= escapeHtml(sessionUser.getRole()) %>
    </p>

    <nav>
        <a href="<%= contextPath %>/dashboard">
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
              action="<%= contextPath %>/logout">
            <button type="submit">
                Sign out
            </button>
        </form>
    </nav>
</header>

<main>

    <div class="page-header">
        <div>
            <% if (administrator) { %>

            <h1>
                Administrator dashboard
            </h1>

            <p>
                System-wide overview of users,
                IT equipment, incidents and maintenance activities.
            </p>

            <% } else { %>

            <h1>
                Technician dashboard
            </h1>

            <p>
                Overview of your assigned incidents
                and maintenance activities.
            </p>

            <% } %>
        </div>
    </div>

    <% if (administrator) { %>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <h2>
                    Users
                </h2>

                <span>
                    Current user account statistics.
                </span>
            </div>

            <a href="<%= contextPath %>/admin/users"
               class="button button-primary">
                Manage users
            </a>
        </div>

        <div class="statistics-grid">
            <div class="statistics-card">
                <span class="statistics-label">
                    Total users
                </span>

                <strong class="statistics-value">
                    <%= totalUsers %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Active users
                </span>

                <strong class="statistics-value">
                    <%= activeUsers %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Administrators
                </span>

                <strong class="statistics-value">
                    <%= administratorUsers %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Technicians
                </span>

                <strong class="statistics-value">
                    <%= technicianUsers %>
                </strong>
            </div>
        </div>
    </section>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <h2>
                    Equipment
                </h2>

                <span>
                    Current status of registered IT equipment.
                </span>
            </div>

            <a href="<%= contextPath %>/equipment"
               class="button button-primary">
                View equipment
            </a>
        </div>

        <div class="statistics-grid">
            <div class="statistics-card">
                <span class="statistics-label">
                    Total equipment
                </span>

                <strong class="statistics-value">
                    <%= totalEquipment %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Active
                </span>

                <strong class="statistics-value">
                    <%= activeEquipment %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    In repair
                </span>

                <strong class="statistics-value">
                    <%= equipmentInRepair %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Inactive
                </span>

                <strong class="statistics-value">
                    <%= inactiveEquipment %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Decommissioned
                </span>

                <strong class="statistics-value">
                    <%= decommissionedEquipment %>
                </strong>
            </div>
        </div>
    </section>

    <% } %>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <% if (administrator) { %>

                <h2>
                    Incidents
                </h2>

                <span>
                    System-wide incident status overview.
                </span>

                <% } else { %>

                <h2>
                    My assigned incidents
                </h2>

                <span>
                    Status overview of incidents assigned to you.
                </span>

                <% } %>
            </div>

            <% if (administrator) { %>

            <a href="<%= contextPath %>/incidents"
               class="button button-primary">
                View all incidents
            </a>

            <% } else { %>

            <a href="<%= contextPath %>/incidents/my"
               class="button button-primary">
                View my incidents
            </a>

            <% } %>
        </div>

        <div class="statistics-grid">
            <div class="statistics-card">
                <span class="statistics-label">
                    Total incidents
                </span>

                <strong class="statistics-value">
                    <%= totalIncidents %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    New
                </span>

                <strong class="statistics-value">
                    <%= newIncidents %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    In progress
                </span>

                <strong class="statistics-value">
                    <%= inProgressIncidents %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Resolved
                </span>

                <strong class="statistics-value">
                    <%= resolvedIncidents %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Closed
                </span>

                <strong class="statistics-value">
                    <%= closedIncidents %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Closure rate
                </span>

                <strong class="statistics-value">
                    <%= incidentClosureRateText %>%
                </strong>
            </div>
        </div>
    </section>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <% if (administrator) { %>

                <h2>
                    Maintenance
                </h2>

                <span>
                    System-wide maintenance activity overview.
                </span>

                <% } else { %>

                <h2>
                    My maintenance activity
                </h2>

                <span>
                    Summary of maintenance records performed by you.
                </span>

                <% } %>
            </div>

            <a href="<%= contextPath %>/maintenance"
               class="button button-primary">
                View maintenance history
            </a>
        </div>

        <div class="statistics-grid">
            <div class="statistics-card">
                <span class="statistics-label">
                    Total records
                </span>

                <strong class="statistics-value">
                    <%= totalMaintenanceRecords %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Successful
                </span>

                <strong class="statistics-value">
                    <%= successfulMaintenance %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Partially completed
                </span>

                <strong class="statistics-value">
                    <%= partiallyCompletedMaintenance %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Failed
                </span>

                <strong class="statistics-value">
                    <%= failedMaintenance %>
                </strong>
            </div>

            <div class="statistics-card">
                <span class="statistics-label">
                    Success rate
                </span>

                <strong class="statistics-value">
                    <%= maintenanceSuccessRateText %>%
                </strong>
            </div>
        </div>
    </section>

    <section class="content-card">
        <div class="content-card-header">
            <div>
                <h2>
                    Quick actions
                </h2>

                <span>
                    Frequently used system actions.
                </span>
            </div>
        </div>

        <div class="actions">

            <% if (administrator) { %>

            <a href="<%= contextPath %>/admin/users">
                Manage users
            </a>

            <a href="<%= contextPath %>/equipment">
                Manage equipment
            </a>

            <a href="<%= contextPath %>/admin/incidents/create">
                Create incident
            </a>

            <a href="<%= contextPath %>/incidents">
                Incident management
            </a>

            <a href="<%= contextPath %>/maintenance">
                Maintenance history
            </a>

            <% } else { %>

            <a href="<%= contextPath %>/incidents/my">
                My incidents
            </a>

            <a href="<%= contextPath %>/equipment">
                Equipment
            </a>

            <a href="<%= contextPath %>/maintenance">
                My maintenance history
            </a>

            <% } %>

        </div>
    </section>

</main>

</body>

</html>