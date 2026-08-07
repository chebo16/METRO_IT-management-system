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

<body class="application-page">

<header class="application-header">

    <div class="application-brand">

        <a href="<%= contextPath %>/"
           class="brand-link">

            METRO IT Management

        </a>

    </div>

    <div class="header-user">

        <% if (sessionUser != null) { %>

        <span class="header-user-name">

                <%= escapeHtml(
                        sessionUser.getFullName()
                ) %>

            </span>

        <span class="role-badge role-admin">

                <%= escapeHtml(
                        sessionUser.getRole()
                ) %>

            </span>

        <% } %>

        <form method="post"
              action="<%= contextPath %>/logout"
              class="inline-form">

            <button type="submit"
                    class="button button-secondary">

                Sign out

            </button>

        </form>

    </div>

</header>

<div class="application-layout">

    <aside class="sidebar">

        <nav aria-label="Main navigation">

            <ul class="navigation-list">

                <li>

                    <a href="<%= contextPath %>/">
                        Dashboard
                    </a>

                </li>

                <li>

                    <a href="<%= contextPath %>/admin/users">
                        Users
                    </a>

                </li>

                <li>

                    <a href="<%= contextPath %>/equipment">
                        Equipment
                    </a>

                </li>

                <li>

                    <a href="<%= contextPath %>/incidents"
                       class="active">

                        Incidents

                    </a>

                </li>

                <li>

                    <a href="<%= contextPath %>/maintenance">
                        Maintenance
                    </a>

                </li>

            </ul>

        </nav>

    </aside>

    <main class="application-content">

        <div class="page-header">

            <div>

                <h1>
                    <%= pageTitle %>
                </h1>

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

        <div class="alert alert-error"
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

            <div class="alert alert-error"
                 role="alert">

                No equipment is registered in the system.
                Equipment must exist before an incident
                can be created.

            </div>

            <a href="<%= contextPath %>/admin/equipment/create"
               class="button button-primary">

                Add equipment

            </a>

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

                        <label for="description">
                            Description
                        </label>

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

                    <h3>
                        Initial incident state
                    </h3>

                    <dl>

                        <dt>Status</dt>

                        <dd>
                            NEW
                        </dd>

                        <dt>Assigned technician</dt>

                        <dd>
                            Not assigned
                        </dd>

                        <dt>Created by</dt>

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

</div>

</body>

</html>