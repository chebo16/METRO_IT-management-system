<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.model.Equipment" %>
<%@ page import="com.chebo16.metroit.model.Incident" %>
<%@ page import="com.chebo16.metroit.model.enums.MaintenanceResult" %>
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

    private static String displayValue(Object value) {
        if (value == null) {
            return "Not available";
        }

        String text = value.toString().trim();

        if (text.isEmpty()) {
            return "Not available";
        }

        return escapeHtml(text);
    }

    private static String formatEnum(Object value) {
        if (value == null) {
            return "";
        }

        return escapeHtml(
                value.toString().replace('_', ' ')
        );
    }
%>

<%
    String contextPath = request.getContextPath();

    SessionUser sessionUser = null;

    Object authenticatedUserAttribute = session.getAttribute(
            SessionConstants.AUTHENTICATED_USER
    );

    if (authenticatedUserAttribute instanceof SessionUser) {
        sessionUser = (SessionUser) authenticatedUserAttribute;
    }

    Incident incident = null;

    Object incidentAttribute =
            request.getAttribute("incident");

    if (incidentAttribute instanceof Incident) {
        incident = (Incident) incidentAttribute;
    }

    Equipment equipment = null;

    Object equipmentAttribute =
            request.getAttribute("equipment");

    if (equipmentAttribute instanceof Equipment) {
        equipment = (Equipment) equipmentAttribute;
    }

    MaintenanceResult[] results =
            MaintenanceResult.values();

    Object resultsAttribute =
            request.getAttribute("results");

    if (resultsAttribute instanceof MaintenanceResult[]) {
        results =
                (MaintenanceResult[]) resultsAttribute;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Add Maintenance Record | METRO IT Management
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
            <%= escapeHtml(sessionUser.getFullName()) %>
        </strong>
        -
        <%= escapeHtml(sessionUser.getRole()) %>
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

    <% if (incident == null) { %>

    <div class="page-header">
        <div>
            <h2>
                Add maintenance record
            </h2>

            <p>
                Maintenance information cannot be created
                without a valid incident.
            </p>
        </div>
    </div>

    <div class="warning-message">
        Incident information is not available.
    </div>

    <div class="form-actions">
        <a href="<%= contextPath %>/incidents/my"
           class="button button-secondary">
            Back to my incidents
        </a>

        <a href="<%= contextPath %>/maintenance"
           class="button button-secondary">
            Maintenance history
        </a>
    </div>

    <% } else { %>

    <div class="page-header">
        <div>
            <h2>
                Add maintenance record
            </h2>

            <p>
                Record diagnostics, repair,
                configuration or other technical work
                performed for incident
                #<%= incident.getId() %>.
            </p>
        </div>

        <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>"
           class="button button-secondary">
            Back to incident
        </a>
    </div>

    <section class="content-card"
             aria-labelledby="maintenance-incident-title">

        <div class="content-card-header">
            <div>
                <h2 id="maintenance-incident-title">
                    Incident information
                </h2>

                <span>
                    Incident associated with this
                    maintenance record.
                </span>
            </div>
        </div>

        <dl>
            <dt>
                Incident
            </dt>

            <dd>
                #<%= incident.getId() %>
                -
                <%= escapeHtml(incident.getTitle()) %>
            </dd>

            <dt>
                Status
            </dt>

            <dd>
                <%= formatEnum(incident.getStatus()) %>
            </dd>

            <dt>
                Priority
            </dt>

            <dd>
                <%= formatEnum(incident.getPriority()) %>
            </dd>

            <dt>
                Assigned technician ID
            </dt>

            <dd>
                <%= displayValue(
                        incident.getAssignedTechnicianId()
                ) %>
            </dd>
        </dl>

    </section>

    <section class="content-card"
             aria-labelledby="maintenance-equipment-title">

        <div class="content-card-header">
            <div>
                <h2 id="maintenance-equipment-title">
                    Equipment information
                </h2>

                <span>
                    Equipment affected by the incident.
                </span>
            </div>
        </div>

        <% if (equipment != null) { %>

        <dl>
            <dt>
                Inventory number
            </dt>

            <dd>
                <%= displayValue(
                        equipment.getInventoryNumber()
                ) %>
            </dd>

            <dt>
                Equipment
            </dt>

            <dd>
                <%= displayValue(
                        equipment.getName()
                ) %>
            </dd>

            <dt>
                Type
            </dt>

            <dd>
                <%= displayValue(
                        equipment.getType()
                ) %>
            </dd>

            <dt>
                Manufacturer
            </dt>

            <dd>
                <%= displayValue(
                        equipment.getManufacturer()
                ) %>
            </dd>

            <dt>
                Model
            </dt>

            <dd>
                <%= displayValue(
                        equipment.getModel()
                ) %>
            </dd>
        </dl>

        <% } else { %>

        <div class="warning-message">
            Equipment information is not available.
        </div>

        <% } %>

    </section>

    <section class="content-card"
             aria-labelledby="maintenance-work-title">

        <div class="content-card-header">
            <div>
                <h2 id="maintenance-work-title">
                    Maintenance work
                </h2>

                <span>
                    Describe the performed technical work
                    and its final result.
                </span>
            </div>
        </div>

        <form method="post"
              action="<%= contextPath %>/maintenance/create">

            <input type="hidden"
                   name="incidentId"
                   value="<%= incident.getId() %>">

            <div class="form-grid">

                <div class="form-group form-group-full">
                    <div>
                        <label for="workDescription">
                            Work description
                        </label>
                    </div>

                    <textarea id="workDescription"
                              name="workDescription"
                              rows="7"
                              maxlength="5000"
                              placeholder="Describe the maintenance work performed"
                              required></textarea>

                    <small class="form-help">
                        Describe diagnostics, repair,
                        configuration or other technical
                        work performed.
                    </small>
                </div>

                <div class="form-group form-group-full">
                    <div>
                        <label for="replacedComponents">
                            Replaced components
                        </label>
                    </div>

                    <textarea id="replacedComponents"
                              name="replacedComponents"
                              rows="4"
                              maxlength="3000"
                              placeholder="Optional: list replaced components"></textarea>

                    <small class="form-help">
                        Leave this field empty if no
                        components were replaced.
                    </small>
                </div>

                <div class="form-group">
                    <label for="result">
                        Maintenance result
                    </label>

                    <select id="result"
                            name="result"
                            required>

                        <option value="">
                            Select result
                        </option>

                        <% for (MaintenanceResult result : results) { %>

                        <option value="<%= result.name() %>">
                            <%= formatEnum(result) %>
                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">
                        Select the final result of the
                        maintenance activity.
                    </small>
                </div>

            </div>

            <div class="form-actions">
                <button type="submit"
                        class="button button-primary">
                    Save maintenance record
                </button>

                <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>"
                   class="button button-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </section>

    <div class="form-actions">
        <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>"
           class="button button-secondary">
            Back to incident details
        </a>

        <a href="<%= contextPath %>/maintenance"
           class="button button-secondary">
            View maintenance history
        </a>
    </div>

    <% } %>

</main>

</body>

</html>