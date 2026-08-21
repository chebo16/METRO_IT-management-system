<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.Equipment" %>
<%@ page import="com.chebo16.metroit.model.enums.IncidentPriority" %>
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

    private static String formatPriority(
            IncidentPriority priority
    ) {

        if (priority == null) {
            return "";
        }

        return priority.name()
                .replace('_', ' ');
    }

    private static String formatEquipmentStatus(
            Object status
    ) {

        if (status == null) {
            return "";
        }

        return status.toString()
                .replace('_', ' ');
    }
%>

<%
    String contextPath =
            request.getContextPath();

    Object authenticatedUserAttribute =
            session.getAttribute(
                    SessionConstants.AUTHENTICATED_USER
            );

    SessionUser sessionUser = null;

    if (authenticatedUserAttribute
            instanceof SessionUser) {

        sessionUser =
                (SessionUser)
                        authenticatedUserAttribute;
    }

    String pageTitle =
            escapeHtml(
                    request.getAttribute(
                            "pageTitle"
                    )
            );

    if (pageTitle.isEmpty()) {
        pageTitle = "Create incident";
    }

    String formAction =
            escapeHtml(
                    request.getAttribute(
                            "formAction"
                    )
            );

    if (formAction.isEmpty()) {

        formAction =
                contextPath
                        + "/admin/incidents/create";
    }

    String submitLabel =
            escapeHtml(
                    request.getAttribute(
                            "submitLabel"
                    )
            );

    if (submitLabel.isEmpty()) {
        submitLabel = "Create incident";
    }

    String title =
            escapeHtml(
                    request.getAttribute(
                            "title"
                    )
            );

    String description =
            escapeHtml(
                    request.getAttribute(
                            "description"
                    )
            );

    String selectedPriority =
            escapeHtml(
                    request.getAttribute(
                            "selectedPriority"
                    )
            );

    if (selectedPriority.isEmpty()) {

        selectedPriority =
                IncidentPriority.MEDIUM.name();
    }

    String selectedEquipmentId =
            escapeHtml(
                    request.getAttribute(
                            "selectedEquipmentId"
                    )
            );

    String errorMessage =
            escapeHtml(
                    request.getAttribute(
                            "errorMessage"
                    )
            );

    List<Equipment> equipmentList =
            Collections.emptyList();

    Object equipmentAttribute =
            request.getAttribute(
                    "equipment"
            );

    if (equipmentAttribute
            instanceof List) {

        equipmentList =
                (List<Equipment>)
                        equipmentAttribute;
    }

    IncidentPriority[] availablePriorities =
            IncidentPriority.values();

    Object prioritiesAttribute =
            request.getAttribute(
                    "availablePriorities"
            );

    if (prioritiesAttribute
            instanceof IncidentPriority[]) {

        availablePriorities =
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
        <%= pageTitle %> | METRO IT Management
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

        <a href="<%= contextPath %>/admin/users">
            Users
        </a>

        <a href="<%= contextPath %>/equipment">
            Equipment
        </a>

        <a href="<%= contextPath %>/incidents">
            Incidents
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
                <%= pageTitle %>
            </h2>

            <p>
                Register a new IT incident and associate
                it with the affected equipment.
            </p>

        </div>

        <a href="<%= contextPath %>/incidents"
           class="button button-secondary">

            Back to incidents

        </a>

    </div>

    <% if (!errorMessage.isEmpty()) { %>

    <div class="error-message"
         role="alert">

        <%= errorMessage %>

    </div>

    <% } %>

    <section class="content-card incident-form-card"
             aria-labelledby="incident-form-title">

        <div class="content-card-header">

            <div>

                <h2 id="incident-form-title">
                    Incident details
                </h2>

                <span>
                    New incidents are automatically
                    created with status NEW.
                </span>

            </div>

        </div>

        <% if (equipmentList.isEmpty()) { %>

        <div class="error-message"
             role="alert">

            No equipment is registered in the system.
            Equipment must exist before an incident
            can be created.

        </div>

        <div class="form-actions">

            <a href="<%= contextPath %>/admin/equipment/create"
               class="button button-primary">

                Add equipment

            </a>

            <a href="<%= contextPath %>/incidents"
               class="button button-secondary">

                Back to incidents

            </a>

        </div>

        <% } else { %>

        <form method="post"
              action="<%= formAction %>"
              class="incident-form"
              autocomplete="off">

            <div class="form-grid">

                <div class="form-group form-group-full">

                    <label for="title">
                        Incident title
                    </label>

                    <input type="text"
                           id="title"
                           name="title"
                           value="<%= title %>"
                           maxlength="150"
                           placeholder="Short description of the problem"
                           required>

                    <small class="form-help">
                        Enter a clear and concise title
                        for the incident.
                    </small>

                </div>

                <div class="form-group">

                    <label for="priority">
                        Priority
                    </label>

                    <select id="priority"
                            name="priority"
                            required>

                        <% for (IncidentPriority priority
                                : availablePriorities) { %>

                        <option value="<%= priority.name() %>"
                                <%= priority.name()
                                        .equals(
                                                selectedPriority
                                        )
                                        ? "selected"
                                        : "" %>>

                            <%= escapeHtml(
                                    formatPriority(
                                            priority
                                    )
                            ) %>

                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">
                        Select the operational priority
                        of the incident.
                    </small>

                </div>

                <div class="form-group">

                    <label for="equipmentId">
                        Affected equipment
                    </label>

                    <select id="equipmentId"
                            name="equipmentId"
                            required>

                        <option value="">
                            Select equipment
                        </option>

                        <% for (Equipment equipment
                                : equipmentList) {

                            String equipmentId =
                                    String.valueOf(
                                            equipment.getId()
                                    );
                        %>

                        <option value="<%= equipmentId %>"
                                <%= equipmentId.equals(
                                        selectedEquipmentId
                                )
                                        ? "selected"
                                        : "" %>>

                            <%= escapeHtml(
                                    equipment
                                            .getInventoryNumber()
                            ) %>

                            —

                            <%= escapeHtml(
                                    equipment.getName()
                            ) %>

                            (

                            <%= escapeHtml(
                                    formatEquipmentStatus(
                                            equipment.getStatus()
                                    )
                            ) %>

                            )

                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">
                        Select the equipment affected
                        by the incident.
                    </small>

                </div>

                <div class="form-group form-group-full">

                    <div>
                        <label for="description">
                            Description
                        </label>
                    </div>

                    <textarea id="description"
                              name="description"
                              rows="8"
                              placeholder="Describe the problem, symptoms and relevant circumstances..."
                              required><%= description %></textarea>

                    <small class="form-help">
                        Provide enough information for
                        the technician to diagnose the
                        problem.
                    </small>

                </div>

            </div>

            <section class="content-card incident-information">

                <div class="content-card-header">

                    <h3>
                        Initial incident state
                    </h3>

                </div>

                <dl>

                    <dt>
                        Status
                    </dt>

                    <dd>

                        <span class="status-badge status-new">
                            NEW
                        </span>

                    </dd>

                    <dt>
                        Assigned technician
                    </dt>

                    <dd>
                        Not assigned
                    </dd>

                    <dt>
                        Created by
                    </dt>

                    <dd>

                        <% if (sessionUser != null) { %>

                        <%= escapeHtml(
                                sessionUser.getFullName()
                        ) %>

                        <% } else { %>

                        Current administrator

                        <% } %>

                    </dd>

                </dl>

            </section>

            <div class="form-actions">

                <button type="submit"
                        class="button button-primary">

                    <%= submitLabel %>

                </button>

                <a href="<%= contextPath %>/incidents"
                   class="button button-secondary">

                    Cancel

                </a>

            </div>

        </form>

        <% } %>

    </section>

</main>

</body>

</html>
