<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.model.enums.EquipmentStatus" %>
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

    private static String formatStatus(EquipmentStatus status) {
        if (status == null) {
            return "";
        }

        return status.name().replace('_', ' ');
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

    String pageTitle =
            escapeHtml(request.getAttribute("pageTitle"));

    if (pageTitle.isEmpty()) {
        pageTitle = "Equipment form";
    }

    String formMode =
            escapeHtml(request.getAttribute("formMode"));

    boolean editMode =
            "edit".equalsIgnoreCase(formMode);

    String formAction =
            escapeHtml(request.getAttribute("formAction"));

    if (formAction.isEmpty()) {
        formAction =
                contextPath + "/admin/equipment/create";
    }

    String submitLabel =
            escapeHtml(request.getAttribute("submitLabel"));

    if (submitLabel.isEmpty()) {
        submitLabel =
                editMode
                        ? "Save changes"
                        : "Create equipment";
    }

    String equipmentId =
            escapeHtml(request.getAttribute("equipmentId"));

    String inventoryNumber =
            escapeHtml(request.getAttribute("inventoryNumber"));

    String name =
            escapeHtml(request.getAttribute("name"));

    String type =
            escapeHtml(request.getAttribute("type"));

    String manufacturer =
            escapeHtml(request.getAttribute("manufacturer"));

    String model =
            escapeHtml(request.getAttribute("model"));

    String serialNumber =
            escapeHtml(request.getAttribute("serialNumber"));

    String ipAddress =
            escapeHtml(request.getAttribute("ipAddress"));

    String notes =
            escapeHtml(request.getAttribute("notes"));

    String selectedStatus =
            escapeHtml(request.getAttribute("selectedStatus"));

    if (selectedStatus.isEmpty()) {
        selectedStatus = EquipmentStatus.ACTIVE.name();
    }

    String errorMessage =
            escapeHtml(request.getAttribute("errorMessage"));

    EquipmentStatus[] availableStatuses =
            EquipmentStatus.values();

    Object statusesAttribute =
            request.getAttribute("availableStatuses");

    if (statusesAttribute instanceof EquipmentStatus[]) {
        availableStatuses =
                (EquipmentStatus[]) statusesAttribute;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title><%= pageTitle %> | METRO IT Management</title>

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
            <h2><%= pageTitle %></h2>

            <p>
                <% if (editMode) { %>

                Update the equipment information,
                network data and current status.

                <% } else { %>

                Register a new IT equipment item
                in the management system.

                <% } %>
            </p>
        </div>

        <a href="<%= contextPath %>/equipment"
           class="button button-secondary">
            Back to equipment
        </a>
    </div>

    <% if (!errorMessage.isEmpty()) { %>

    <div class="error-message"
         role="alert">
        <%= errorMessage %>
    </div>

    <% } %>

    <section class="content-card equipment-form-card"
             aria-labelledby="equipment-form-title">

        <div class="content-card-header">
            <h2 id="equipment-form-title">
                <% if (editMode) { %>

                Equipment details

                <% } else { %>

                New equipment details

                <% } %>
            </h2>
        </div>

        <form method="post"
              action="<%= formAction %>"
              class="equipment-form"
              autocomplete="off">

            <% if (editMode && !equipmentId.isEmpty()) { %>

            <input type="hidden"
                   name="id"
                   value="<%= equipmentId %>">

            <% } %>

            <div class="form-grid">

                <div class="form-group">
                    <label for="inventoryNumber">
                        Inventory number
                    </label>

                    <input type="text"
                           id="inventoryNumber"
                           name="inventoryNumber"
                           value="<%= inventoryNumber %>"
                           maxlength="50"
                           required>

                    <small class="form-help">
                        Unique internal inventory number.
                    </small>
                </div>

                <div class="form-group">
                    <label for="name">
                        Equipment name
                    </label>

                    <input type="text"
                           id="name"
                           name="name"
                           value="<%= name %>"
                           maxlength="100"
                           required>

                    <small class="form-help">
                        Descriptive name of the equipment.
                    </small>
                </div>

                <div class="form-group">
                    <label for="type">
                        Equipment type
                    </label>

                    <input type="text"
                           id="type"
                           name="type"
                           value="<%= type %>"
                           maxlength="50"
                           placeholder="Computer, printer, router..."
                           required>

                    <small class="form-help">
                        General category of the equipment.
                    </small>
                </div>

                <div class="form-group">
                    <label for="status">
                        Status
                    </label>

                    <select id="status"
                            name="status"
                            required>

                        <% for (EquipmentStatus status : availableStatuses) { %>

                        <option value="<%= status.name() %>"
                                <%= status.name().equals(selectedStatus)
                                        ? "selected"
                                        : "" %>>
                            <%= escapeHtml(formatStatus(status)) %>
                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">
                        Current operational status.
                    </small>
                </div>

                <div class="form-group">
                    <label for="manufacturer">
                        Manufacturer
                    </label>

                    <input type="text"
                           id="manufacturer"
                           name="manufacturer"
                           value="<%= manufacturer %>"
                           maxlength="100"
                           placeholder="Dell, HP, Cisco...">

                    <small class="form-help">
                        Optional equipment manufacturer.
                    </small>
                </div>

                <div class="form-group">
                    <label for="model">
                        Model
                    </label>

                    <input type="text"
                           id="model"
                           name="model"
                           value="<%= model %>"
                           maxlength="100">

                    <small class="form-help">
                        Optional model designation.
                    </small>
                </div>

                <div class="form-group">
                    <label for="serialNumber">
                        Serial number
                    </label>

                    <input type="text"
                           id="serialNumber"
                           name="serialNumber"
                           value="<%= serialNumber %>"
                           maxlength="100">

                    <small class="form-help">
                        Must be unique when provided.
                    </small>
                </div>

                <div class="form-group">
                    <label for="ipAddress">
                        IP address
                    </label>

                    <input type="text"
                           id="ipAddress"
                           name="ipAddress"
                           value="<%= ipAddress %>"
                           maxlength="45"
                           placeholder="192.168.1.10">

                    <small class="form-help">
                        Optional IPv4 or IPv6 address.
                    </small>
                </div>

                <div class="form-group form-group-full">
                    <div>
                        <label for="notes">
                            Notes
                        </label>
                    </div>

                    <textarea id="notes"
                              name="notes"
                              rows="5"
                              maxlength="2000"
                              placeholder="Additional equipment information..."><%= notes %></textarea>

                    <small class="form-help">
                        Optional technical or administrative notes.
                    </small>
                </div>

            </div>

            <div class="form-actions">
                <button type="submit"
                        class="button button-primary">
                    <%= submitLabel %>
                </button>

                <a href="<%= contextPath %>/equipment"
                   class="button button-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </section>

</main>

</body>

</html>