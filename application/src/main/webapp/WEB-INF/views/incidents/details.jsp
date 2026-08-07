<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.model.Equipment" %>
<%@ page import="com.chebo16.metroit.model.Incident" %>
<%@ page import="com.chebo16.metroit.model.User" %>
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

    private static String displayValue(
            Object value
    ) {

        if (value == null) {
            return "Not available";
        }

        String text =
                value.toString().trim();

        if (text.isEmpty()) {
            return "Not available";
        }

        return escapeHtml(text);
    }

    private static String formatStatus(
            IncidentStatus status
    ) {

        if (status == null) {
            return "";
        }

        return status.name()
                .replace('_', ' ');
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

    private static String formatEquipmentStatus(
            Object status
    ) {

        if (status == null) {
            return "Not available";
        }

        return escapeHtml(
                status.toString()
                        .replace('_', ' ')
        );
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

    Object createdByAttribute =
            request.getAttribute(
                    "createdByUser"
            );

    if (createdByAttribute
            instanceof User) {

        createdByUser =
                (User)
                        createdByAttribute;
    }

    User assignedTechnician = null;

    Object technicianAttribute =
            request.getAttribute(
                    "assignedTechnician"
            );

    if (technicianAttribute
            instanceof User) {

        assignedTechnician =
                (User)
                        technicianAttribute;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Incident details | METRO IT Management
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

        <span class="role-badge
                    <%= sessionUser.isAdmin()
                            ? "role-admin"
                            : "role-technician" %>">

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

                <% if (administrator) { %>

                <li>

                    <a href="<%= contextPath %>/admin/users">
                        Users
                    </a>

                </li>

                <% } %>

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

        <% if (incident == null) { %>

        <section class="content-card">

            <div class="alert alert-error"
                 role="alert">

                Incident information is not available.

            </div>

            <a href="<%= contextPath %>/incidents"
               class="button button-secondary">

                Back to incidents

            </a>

        </section>

        <% } else { %>

        <div class="page-header">

            <div>

                <h1>

                    Incident #<%= incident.getId() %>

                </h1>

                <p>

                    <%= escapeHtml(
                            incident.getTitle()
                    ) %>

                </p>

            </div>

            <a href="<%= contextPath %>/incidents"
               class="button button-secondary">

                Back to incidents

            </a>

        </div>

        <section class="statistics-grid"
                 aria-label="Incident overview">

            <article class="statistics-card">

                    <span class="statistics-label">
                        Priority
                    </span>

                <strong class="statistics-value">

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

                </strong>

            </article>

            <article class="statistics-card">

                    <span class="statistics-label">
                        Status
                    </span>

                <strong class="statistics-value">

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

                </strong>

            </article>

            <article class="statistics-card">

                    <span class="statistics-label">
                        Created
                    </span>

                <strong class="statistics-value">

                    <%= displayValue(
                            incident.getCreatedAt()
                    ) %>

                </strong>

            </article>

            <article class="statistics-card">

                    <span class="statistics-label">
                        Technician
                    </span>

                <strong class="statistics-value">

                    <% if (assignedTechnician != null) { %>

                    <%= escapeHtml(
                            assignedTechnician
                                    .getFullName()
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

            <dl class="details-grid">

                <div class="details-item">

                    <dt>
                        Incident ID
                    </dt>

                    <dd>
                        #<%= incident.getId() %>
                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Title
                    </dt>

                    <dd>

                        <%= escapeHtml(
                                incident.getTitle()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Priority
                    </dt>

                    <dd>

                        <%= escapeHtml(
                                formatPriority(
                                        incident.getPriority()
                                )
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Status
                    </dt>

                    <dd>

                        <%= escapeHtml(
                                formatStatus(
                                        incident.getStatus()
                                )
                        ) %>

                    </dd>

                </div>

                <div class="details-item details-item-full">

                    <dt>
                        Description
                    </dt>

                    <dd class="preserve-line-breaks">

                        <%= escapeHtml(
                                incident.getDescription()
                        ) %>

                    </dd>

                </div>

            </dl>

        </section>

        <section class="content-card"
                 aria-labelledby="equipment-information-title">

            <div class="content-card-header">

                <h2 id="equipment-information-title">
                    Affected equipment
                </h2>

            </div>

            <% if (equipment != null) { %>

            <dl class="details-grid">

                <div class="details-item">

                    <dt>
                        Inventory number
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment
                                        .getInventoryNumber()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Equipment name
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment.getName()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Type
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment.getType()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Status
                    </dt>

                    <dd>

                        <%= formatEquipmentStatus(
                                equipment.getStatus()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Manufacturer
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment
                                        .getManufacturer()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Model
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment.getModel()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Serial number
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment
                                        .getSerialNumber()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        IP address
                    </dt>

                    <dd>

                        <%= displayValue(
                                equipment.getIpAddress()
                        ) %>

                    </dd>

                </div>

            </dl>

            <% } else { %>

            <div class="alert alert-error">

                Equipment information could not
                be loaded.

            </div>

            <% } %>

        </section>

        <section class="content-card"
                 aria-labelledby="assignment-information-title">

            <div class="content-card-header">

                <h2 id="assignment-information-title">
                    Responsibility
                </h2>

            </div>

            <dl class="details-grid">

                <div class="details-item">

                    <dt>
                        Created by
                    </dt>

                    <dd>

                        <% if (createdByUser != null) { %>

                        <%= escapeHtml(
                                createdByUser
                                        .getFullName()
                        ) %>

                        <div class="table-secondary-text">

                            <%= escapeHtml(
                                    createdByUser
                                            .getUsername()
                            ) %>

                        </div>

                        <% } else { %>

                        Not available

                        <% } %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Assigned technician
                    </dt>

                    <dd>

                        <% if (assignedTechnician != null) { %>

                        <%= escapeHtml(
                                assignedTechnician
                                        .getFullName()
                        ) %>

                        <div class="table-secondary-text">

                            <%= escapeHtml(
                                    assignedTechnician
                                            .getUsername()
                            ) %>

                        </div>

                        <% } else { %>

                        Not assigned

                        <% } %>

                    </dd>

                </div>

            </dl>

        </section>

        <section class="content-card"
                 aria-labelledby="incident-timeline-title">

            <div class="content-card-header">

                <h2 id="incident-timeline-title">
                    Incident timeline
                </h2>

            </div>

            <dl class="details-grid">

                <div class="details-item">

                    <dt>
                        Created at
                    </dt>

                    <dd>

                        <%= displayValue(
                                incident.getCreatedAt()
                        ) %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Started at
                    </dt>

                    <dd>

                        <% if (incident.getStartedAt()
                                != null) { %>

                        <%= displayValue(
                                incident.getStartedAt()
                        ) %>

                        <% } else { %>

                        Not started

                        <% } %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Resolved at
                    </dt>

                    <dd>

                        <% if (incident.getResolvedAt()
                                != null) { %>

                        <%= displayValue(
                                incident.getResolvedAt()
                        ) %>

                        <% } else { %>

                        Not resolved

                        <% } %>

                    </dd>

                </div>

                <div class="details-item">

                    <dt>
                        Closed at
                    </dt>

                    <dd>

                        <% if (incident.getClosedAt()
                                != null) { %>

                        <%= displayValue(
                                incident.getClosedAt()
                        ) %>

                        <% } else { %>

                        Not closed

                        <% } %>

                    </dd>

                </div>

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

            <p class="preserve-line-breaks">

                <%= escapeHtml(
                        incident
                                .getSolutionDescription()
                ) %>

            </p>

            <% } else { %>

            <p class="table-secondary-text">

                No solution has been recorded yet.

            </p>

            <% } %>

        </section>

        <% } %>

    </main>

</div>

</body>

</html>