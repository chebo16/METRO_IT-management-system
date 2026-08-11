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

    List<MaintenanceRecord> records =
            Collections.emptyList();

    Object recordsAttribute =
            request.getAttribute(
                    "records"
            );

    if (recordsAttribute
            instanceof List) {

        records =
                (List<MaintenanceRecord>)
                        recordsAttribute;
    }

    long totalRecords = 0;
    long successfulRecords = 0;
    long partiallyCompletedRecords = 0;
    long failedRecords = 0;

    Object totalRecordsAttribute =
            request.getAttribute(
                    "totalRecords"
            );

    if (totalRecordsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        totalRecordsAttribute;

        totalRecords =
                number.longValue();
    }

    Object successfulRecordsAttribute =
            request.getAttribute(
                    "successfulRecords"
            );

    if (successfulRecordsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        successfulRecordsAttribute;

        successfulRecords =
                number.longValue();
    }

    Object partiallyCompletedRecordsAttribute =
            request.getAttribute(
                    "partiallyCompletedRecords"
            );

    if (partiallyCompletedRecordsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        partiallyCompletedRecordsAttribute;

        partiallyCompletedRecords =
                number.longValue();
    }

    Object failedRecordsAttribute =
            request.getAttribute(
                    "failedRecords"
            );

    if (failedRecordsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        failedRecordsAttribute;

        failedRecords =
                number.longValue();
    }

    String search = "";

    Object searchAttribute =
            request.getAttribute(
                    "search"
            );

    if (searchAttribute
            instanceof String) {

        search =
                (String)
                        searchAttribute;
    }

    String selectedResult = "";

    Object selectedResultAttribute =
            request.getAttribute(
                    "selectedResult"
            );

    if (selectedResultAttribute
            instanceof String) {

        selectedResult =
                (String)
                        selectedResultAttribute;
    }

    MaintenanceResult[] results =
            MaintenanceResult.values();

    Object resultsAttribute =
            request.getAttribute(
                    "results"
            );

    if (resultsAttribute
            instanceof MaintenanceResult[]) {

        results =
                (MaintenanceResult[])
                        resultsAttribute;
    }

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
        Maintenance History | METRO IT Management
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
        Maintenance activities performed
        by your technician account.
    </p>

    <% } %>

    <% if ("created".equals(success)) { %>

    <p>
        <strong>
            Maintenance record was created successfully.
        </strong>
    </p>

    <% } %>

    <hr>

    <h3>
        Statistics
    </h3>

    <p>
        <strong>
            Total records:
        </strong>

        <%= totalRecords %>
    </p>

    <p>
        <strong>
            Successful:
        </strong>

        <%= successfulRecords %>
    </p>

    <p>
        <strong>
            Partially completed:
        </strong>

        <%= partiallyCompletedRecords %>
    </p>

    <p>
        <strong>
            Failed:
        </strong>

        <%= failedRecords %>
    </p>

    <hr>

    <h3>
        Search and filters
    </h3>

    <form method="get"
          action="<%= contextPath %>/maintenance">

        <p>

            <label for="search">
                Search:
            </label>

            <input type="text"
                   id="search"
                   name="search"
                   value="<%= escapeHtml(search) %>"
                   placeholder="Description, component or ID">

        </p>

        <p>

            <label for="result">
                Result:
            </label>

            <select id="result"
                    name="result">

                <option value="">
                    All results
                </option>

                <% for (MaintenanceResult result
                        : results) { %>

                <option
                        value="<%= result.name() %>"
                        <%= result.name()
                                .equalsIgnoreCase(
                                        selectedResult
                                )
                                ? "selected"
                                : "" %>>

                    <%= formatEnum(result) %>

                </option>

                <% } %>

            </select>

        </p>

        <p>

            <button type="submit">
                Apply filters
            </button>

            <a href="<%= contextPath %>/maintenance">
                Reset
            </a>

        </p>

    </form>

    <hr>

    <h3>
        Maintenance records
    </h3>

    <p>
        Showing
        <strong>
            <%= records.size() %>
        </strong>
        of
        <strong>
            <%= totalRecords %>
        </strong>
        record(s).
    </p>

    <% if (records.isEmpty()) { %>

    <p>
        No maintenance records match
        the selected criteria.
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
                Incident
            </th>

            <th>
                Equipment
            </th>

            <th>
                Technician
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

            <th>
                Performed at
            </th>

            <th>
                Actions
            </th>

        </tr>

        </thead>

        <tbody>

        <% for (MaintenanceRecord record
                : records) { %>

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

            <td>
                #<%= record.getTechnicianId() %>
            </td>

            <td>
                <%= displayValue(
                        record.getWorkDescription()
                ) %>
            </td>

            <td>

                <% if (record.getReplacedComponents()
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

            <td>
                <%= displayValue(
                        record.getPerformedAt()
                ) %>
            </td>

            <td>

                <a href="<%= contextPath %>/incidents/details?id=<%= record.getIncidentId() %>">
                    Incident details
                </a>

            </td>

        </tr>

        <% } %>

        </tbody>

    </table>

    <% } %>

</main>

</body>

</html>