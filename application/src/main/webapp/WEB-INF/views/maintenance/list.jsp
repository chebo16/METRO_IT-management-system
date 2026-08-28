<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.MaintenanceRecord" %>
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

    private static long getLongAttribute(Object attribute) {
        if (attribute instanceof Number) {
            Number number = (Number) attribute;
            return number.longValue();
        }

        return 0L;
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
    String contextPath = request.getContextPath();

    Object authenticatedUserAttribute = session.getAttribute(
            SessionConstants.AUTHENTICATED_USER
    );

    SessionUser sessionUser = null;

    if (authenticatedUserAttribute instanceof SessionUser) {
        sessionUser = (SessionUser) authenticatedUserAttribute;
    }

    boolean administrator =
            sessionUser != null && sessionUser.isAdmin();

    List<MaintenanceRecord> records =
            Collections.emptyList();

    Object recordsAttribute =
            request.getAttribute("records");

    if (recordsAttribute instanceof List) {
        records =
                (List<MaintenanceRecord>) recordsAttribute;
    }

    long totalRecords =
            getLongAttribute(request.getAttribute("totalRecords"));

    long successfulRecords =
            getLongAttribute(request.getAttribute("successfulRecords"));

    long partiallyCompletedRecords =
            getLongAttribute(
                    request.getAttribute("partiallyCompletedRecords")
            );

    long failedRecords =
            getLongAttribute(request.getAttribute("failedRecords"));

    String search = "";

    Object searchAttribute =
            request.getAttribute("search");

    if (searchAttribute instanceof String) {
        search = (String) searchAttribute;
    }

    String selectedResult = "";

    Object selectedResultAttribute =
            request.getAttribute("selectedResult");

    if (selectedResultAttribute instanceof String) {
        selectedResult =
                (String) selectedResultAttribute;
    }

    MaintenanceResult[] results =
            MaintenanceResult.values();

    Object resultsAttribute =
            request.getAttribute("results");

    if (resultsAttribute instanceof MaintenanceResult[]) {
        results =
                (MaintenanceResult[]) resultsAttribute;
    }

    String success =
            request.getParameter("success");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Maintenance History | METRO IT Management</title>

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
            <h2>
                Maintenance history
            </h2>

            <% if (administrator) { %>

            <p>
                All recorded maintenance activities
                in the IT management system.
            </p>

            <% } else { %>

            <p>
                Maintenance activities performed by you.
            </p>

            <% } %>
        </div>
    </div>

    <% if ("created".equals(success)) { %>

    <div class="success-message"
         role="status">
        Maintenance record was created successfully.
    </div>

    <% } %>

    <section class="statistics-grid"
             aria-label="Maintenance statistics">

        <article class="statistics-card">
            <span class="statistics-label">
                Total records
            </span>

            <strong class="statistics-value">
                <%= totalRecords %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Successful
            </span>

            <strong class="statistics-value">
                <%= successfulRecords %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Partially completed
            </span>

            <strong class="statistics-value">
                <%= partiallyCompletedRecords %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Failed
            </span>

            <strong class="statistics-value">
                <%= failedRecords %>
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
                    Search maintenance records by
                    description, replaced component
                    or identifier and filter by result.
                </span>
            </div>
        </div>

        <form method="get"
              action="<%= contextPath %>/maintenance"
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
                           placeholder="Description, component or ID"
                           maxlength="150">
                </div>

                <div class="form-group">
                    <label for="result">
                        Result
                    </label>

                    <select id="result"
                            name="result">

                        <option value="">
                            All results
                        </option>

                        <% for (MaintenanceResult result : results) { %>

                        <option value="<%= result.name() %>"
                                <%= result.name().equalsIgnoreCase(selectedResult)
                                        ? "selected"
                                        : "" %>>
                            <%= formatEnum(result) %>
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

                <a href="<%= contextPath %>/maintenance"
                   class="button button-secondary">
                    Reset
                </a>
            </div>
        </form>
    </section>

    <section class="content-card"
             aria-labelledby="maintenance-records-title">

        <div class="content-card-header">
            <h2 id="maintenance-records-title">
                Maintenance records
            </h2>

            <span>
                Showing
                <%= records.size() %>
                of
                <%= totalRecords %>
                record(s)
            </span>
        </div>

        <% if (records.isEmpty()) { %>

        <div class="empty-state">
            <h3>
                No maintenance records found
            </h3>

            <p>
                No maintenance records match
                the selected search and result filters.
            </p>

            <a href="<%= contextPath %>/maintenance"
               class="button button-secondary">
                Clear filters
            </a>
        </div>

        <% } else { %>

        <div class="table-container">
            <table class="data-table">
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Incident ID</th>
                    <th scope="col">Equipment ID</th>

                    <% if (administrator) { %>

                    <th scope="col">Technician ID</th>

                    <% } %>

                    <th scope="col">Work description</th>
                    <th scope="col">Replaced components</th>
                    <th scope="col">Result</th>
                    <th scope="col">Performed at</th>
                    <th scope="col">Actions</th>
                </tr>
                </thead>

                <tbody>

                <% for (MaintenanceRecord record : records) { %>

                <tr>
                    <td>
                        #<%= record.getId() %>
                    </td>

                    <td>
                        #<%= record.getIncidentId() %>
                    </td>

                    <td>
                        #<%= record.getEquipmentId() %>
                    </td>

                    <% if (administrator) { %>

                    <td>
                        #<%= record.getTechnicianId() %>
                    </td>

                    <% } %>

                    <td>
                        <%= displayValue(
                                record.getWorkDescription()
                        ) %>
                    </td>

                    <td>
                        <% if (record.getReplacedComponents() != null
                                && !record.getReplacedComponents().isBlank()) { %>

                        <%= escapeHtml(
                                record.getReplacedComponents()
                        ) %>

                        <% } else { %>

                        <span class="table-secondary-text">
                            None
                        </span>

                        <% } %>
                    </td>

                    <td>
                        <span class="maintenance-result-badge <%= maintenanceResultClass(
                                record.getResult()
                        ) %>">

                            <%= formatEnum(record.getResult()) %>
                        </span>
                    </td>

                    <td>
                        <%= displayValue(
                                record.getPerformedAt()
                        ) %>
                    </td>

                    <td>
                        <a href="<%= contextPath %>/incidents/details?id=<%= record.getIncidentId() %>"
                           class="button button-small button-secondary">
                            Incident details
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