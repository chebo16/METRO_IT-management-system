<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.Equipment" %>
<%@ page import="com.chebo16.metroit.model.enums.EquipmentStatus" %>
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

    private static long getLongAttribute(
            Object attribute
    ) {

        if (attribute instanceof Number) {

            Number number =
                    (Number) attribute;

            return number.longValue();
        }

        return 0L;
    }

    private static String formatStatus(
            EquipmentStatus status
    ) {

        if (status == null) {
            return "";
        }

        return status.name()
                .replace('_', ' ');
    }

    private static String getStatusCssClass(
            EquipmentStatus status
    ) {

        if (status == null) {
            return "status-inactive";
        }

        if (status == EquipmentStatus.ACTIVE) {
            return "status-active";
        }

        if (status == EquipmentStatus.IN_REPAIR) {
            return "status-in-repair";
        }

        if (status == EquipmentStatus.INACTIVE) {
            return "status-inactive";
        }

        if (status == EquipmentStatus.DECOMMISSIONED) {
            return "status-decommissioned";
        }

        return "status-inactive";
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

    long totalEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "totalEquipment"
                    )
            );

    long displayedEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "displayedEquipment"
                    )
            );

    long activeEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "activeEquipment"
                    )
            );

    long inRepairEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "inRepairEquipment"
                    )
            );

    long inactiveEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "inactiveEquipment"
                    )
            );

    long decommissionedEquipment =
            getLongAttribute(
                    request.getAttribute(
                            "decommissionedEquipment"
                    )
            );

    String searchQuery =
            escapeHtml(
                    request.getAttribute(
                            "searchQuery"
                    )
            );

    String selectedStatus =
            escapeHtml(
                    request.getAttribute(
                            "selectedStatus"
                    )
            );

    EquipmentStatus[] availableStatuses =
            EquipmentStatus.values();

    Object statusesAttribute =
            request.getAttribute(
                    "availableStatuses"
            );

    if (statusesAttribute
            instanceof EquipmentStatus[]) {

        availableStatuses =
                (EquipmentStatus[])
                        statusesAttribute;
    }

    String successParameter =
            request.getParameter(
                    "success"
            );

    String successMessage = "";

    if ("created".equals(successParameter)) {

        successMessage =
                "Equipment was created successfully.";

    } else if ("updated".equals(
            successParameter
    )) {

        successMessage =
                "Equipment was updated successfully.";

    } else if ("status-updated".equals(
            successParameter
    )) {

        successMessage =
                "Equipment status was updated successfully.";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Equipment | METRO IT Management
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

    <div class="page-header">

        <div>

            <% if (administrator) { %>

            <h2>
                Equipment management
            </h2>

            <p>
                View, search and manage IT equipment
                registered in the system.
            </p>

            <% } else { %>

            <h2>
                Equipment
            </h2>

            <p>
                View and search IT equipment
                registered in the system.
            </p>

            <% } %>

        </div>

        <% if (administrator) { %>

        <a href="<%= contextPath %>/admin/equipment/create"
           class="button button-primary">

            Add equipment

        </a>

        <% } %>

    </div>

    <% if (!successMessage.isEmpty()) { %>

    <div class="success-message"
         role="status">

        <%= escapeHtml(
                successMessage
        ) %>

    </div>

    <% } %>

    <section class="statistics-grid"
             aria-label="Equipment statistics">

        <article class="statistics-card">

            <span class="statistics-label">
                Total equipment
            </span>

            <strong class="statistics-value">
                <%= totalEquipment %>
            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Active
            </span>

            <strong class="statistics-value">
                <%= activeEquipment %>
            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                In repair
            </span>

            <strong class="statistics-value">
                <%= inRepairEquipment %>
            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Inactive
            </span>

            <strong class="statistics-value">
                <%= inactiveEquipment %>
            </strong>

        </article>

        <article class="statistics-card">

            <span class="statistics-label">
                Decommissioned
            </span>

            <strong class="statistics-value">
                <%= decommissionedEquipment %>
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
                    Find equipment by inventory number,
                    name, type, manufacturer, model,
                    serial number or IP address.
                </span>

            </div>

        </div>

        <form method="get"
              action="<%= contextPath %>/equipment"
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
                           placeholder="Inventory number, name, model..."
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

                        <% for (EquipmentStatus status
                                : availableStatuses) { %>

                        <option value="<%= status.name() %>"
                                <%= status.name()
                                        .equals(
                                                selectedStatus
                                        )
                                        ? "selected"
                                        : "" %>>

                            <%= escapeHtml(
                                    formatStatus(
                                            status
                                    )
                            ) %>

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

                <a href="<%= contextPath %>/equipment"
                   class="button button-secondary">

                    Reset

                </a>

            </div>

        </form>

    </section>

    <section class="content-card"
             aria-labelledby="equipment-table-title">

        <div class="content-card-header">

            <h2 id="equipment-table-title">
                Registered equipment
            </h2>

            <span>

                Showing
                <%= displayedEquipment %>
                of
                <%= totalEquipment %>
                item(s)

            </span>

        </div>

        <% if (equipmentList.isEmpty()) { %>

        <div class="empty-state">

            <h3>
                No equipment found
            </h3>

            <p>
                No equipment matches the selected
                search and status filters.
            </p>

            <a href="<%= contextPath %>/equipment"
               class="button button-secondary">

                Clear filters

            </a>

            <% if (administrator) { %>

            <a href="<%= contextPath %>/admin/equipment/create"
               class="button button-primary">

                Add equipment

            </a>

            <% } %>

        </div>

        <% } else { %>

        <div class="table-container">

            <table class="data-table">

                <thead>

                <tr>

                    <th scope="col">
                        Inventory number
                    </th>

                    <th scope="col">
                        Equipment
                    </th>

                    <th scope="col">
                        Type
                    </th>

                    <th scope="col">
                        Manufacturer / Model
                    </th>

                    <th scope="col">
                        Serial number
                    </th>

                    <th scope="col">
                        IP address
                    </th>

                    <th scope="col">
                        Status
                    </th>

                    <th scope="col">
                        Created
                    </th>

                    <% if (administrator) { %>

                    <th scope="col">
                        Actions
                    </th>

                    <% } %>

                </tr>

                </thead>

                <tbody>

                <% for (Equipment equipment
                        : equipmentList) { %>

                <tr>

                    <td>

                        <strong>

                            <%= escapeHtml(
                                    equipment
                                            .getInventoryNumber()
                            ) %>

                        </strong>

                    </td>

                    <td>

                        <strong>

                            <%= escapeHtml(
                                    equipment.getName()
                            ) %>

                        </strong>

                        <% if (equipment.getNotes() != null
                                && !equipment
                                .getNotes()
                                .isBlank()) { %>

                        <div class="table-secondary-text">

                            <%= escapeHtml(
                                    equipment.getNotes()
                            ) %>

                        </div>

                        <% } %>

                    </td>

                    <td>

                        <%= escapeHtml(
                                equipment.getType()
                        ) %>

                    </td>

                    <td>

                        <div>

                            <%= escapeHtml(
                                    equipment
                                            .getManufacturer()
                            ) %>

                        </div>

                        <div class="table-secondary-text">

                            <%= escapeHtml(
                                    equipment.getModel()
                            ) %>

                        </div>

                    </td>

                    <td>

                        <%= escapeHtml(
                                equipment
                                        .getSerialNumber()
                        ) %>

                    </td>

                    <td>

                        <%= escapeHtml(
                                equipment.getIpAddress()
                        ) %>

                    </td>

                    <td>

                        <span class="status-badge
                                <%= getStatusCssClass(
                                        equipment.getStatus()
                                ) %>">

                            <%= escapeHtml(
                                    formatStatus(
                                            equipment.getStatus()
                                    )
                            ) %>

                        </span>

                    </td>

                    <td>

                        <%= escapeHtml(
                                equipment.getCreatedAt()
                        ) %>

                    </td>

                    <% if (administrator) { %>

                    <td>

                        <div class="table-actions">

                            <a href="<%= contextPath %>/admin/equipment/edit?id=<%= equipment.getId() %>"
                               class="button button-small button-secondary">

                                Edit

                            </a>

                            <form method="post"
                                  action="<%= contextPath %>/admin/equipment/status"
                                  class="inline-form">

                                <input type="hidden"
                                       name="id"
                                       value="<%= equipment.getId() %>">

                                <label class="visually-hidden"
                                       for="status-<%= equipment.getId() %>">

                                    Equipment status

                                </label>

                                <select id="status-<%= equipment.getId() %>"
                                        name="status">

                                    <% for (EquipmentStatus status
                                            : availableStatuses) { %>

                                    <option value="<%= status.name() %>"
                                            <%= status
                                                    == equipment.getStatus()
                                                    ? "selected"
                                                    : "" %>>

                                        <%= escapeHtml(
                                                formatStatus(
                                                        status
                                                )
                                        ) %>

                                    </option>

                                    <% } %>

                                </select>

                                <button type="submit"
                                        class="button button-small button-primary">

                                    Update status

                                </button>

                            </form>

                        </div>

                    </td>

                    <% } %>

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
