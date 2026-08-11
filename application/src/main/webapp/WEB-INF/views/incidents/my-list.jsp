<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.Incident" %>
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

    List<Incident> incidents =
            Collections.emptyList();

    Object incidentsAttribute =
            request.getAttribute(
                    "incidents"
            );

    if (incidentsAttribute
            instanceof List) {

        incidents =
                (List<Incident>)
                        incidentsAttribute;
    }

    long totalIncidents = 0;
    long newIncidents = 0;
    long inProgressIncidents = 0;
    long resolvedIncidents = 0;
    long closedIncidents = 0;

    Object totalIncidentsAttribute =
            request.getAttribute(
                    "totalIncidents"
            );

    if (totalIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        totalIncidentsAttribute;

        totalIncidents =
                number.longValue();
    }

    Object newIncidentsAttribute =
            request.getAttribute(
                    "newIncidents"
            );

    if (newIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        newIncidentsAttribute;

        newIncidents =
                number.longValue();
    }

    Object inProgressIncidentsAttribute =
            request.getAttribute(
                    "inProgressIncidents"
            );

    if (inProgressIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        inProgressIncidentsAttribute;

        inProgressIncidents =
                number.longValue();
    }

    Object resolvedIncidentsAttribute =
            request.getAttribute(
                    "resolvedIncidents"
            );

    if (resolvedIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        resolvedIncidentsAttribute;

        resolvedIncidents =
                number.longValue();
    }

    Object closedIncidentsAttribute =
            request.getAttribute(
                    "closedIncidents"
            );

    if (closedIncidentsAttribute
            instanceof Number) {

        Number number =
                (Number)
                        closedIncidentsAttribute;

        closedIncidents =
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

    String selectedStatus = "";

    Object selectedStatusAttribute =
            request.getAttribute(
                    "selectedStatus"
            );

    if (selectedStatusAttribute
            instanceof String) {

        selectedStatus =
                (String)
                        selectedStatusAttribute;
    }

    String selectedPriority = "";

    Object selectedPriorityAttribute =
            request.getAttribute(
                    "selectedPriority"
            );

    if (selectedPriorityAttribute
            instanceof String) {

        selectedPriority =
                (String)
                        selectedPriorityAttribute;
    }

    IncidentStatus[] statuses =
            IncidentStatus.values();

    Object statusesAttribute =
            request.getAttribute(
                    "statuses"
            );

    if (statusesAttribute
            instanceof IncidentStatus[]) {

        statuses =
                (IncidentStatus[])
                        statusesAttribute;
    }

    IncidentPriority[] priorities =
            IncidentPriority.values();

    Object prioritiesAttribute =
            request.getAttribute(
                    "priorities"
            );

    if (prioritiesAttribute
            instanceof IncidentPriority[]) {

        priorities =
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
        My Incidents | METRO IT Management
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

        <a href="<%= contextPath %>/equipment">
            Equipment
        </a>

        |

        <a href="<%= contextPath %>/incidents">
            All incidents
        </a>

        |

        <a href="<%= contextPath %>/incidents/my">
            My incidents
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
        My assigned incidents
    </h2>

    <p>
        Incidents currently assigned to your technician account.
    </p>

    <hr>

    <h3>
        Statistics
    </h3>

    <p>
        <strong>Total:</strong>
        <%= totalIncidents %>
    </p>

    <p>
        <strong>New:</strong>
        <%= newIncidents %>
    </p>

    <p>
        <strong>In progress:</strong>
        <%= inProgressIncidents %>
    </p>

    <p>
        <strong>Resolved:</strong>
        <%= resolvedIncidents %>
    </p>

    <p>
        <strong>Closed:</strong>
        <%= closedIncidents %>
    </p>

    <hr>

    <h3>
        Search and filters
    </h3>

    <form method="get"
          action="<%= contextPath %>/incidents/my">

        <p>

            <label for="search">
                Search:
            </label>

            <input type="text"
                   id="search"
                   name="search"
                   value="<%= escapeHtml(search) %>"
                   placeholder="Title or description">

        </p>

        <p>

            <label for="status">
                Status:
            </label>

            <select id="status"
                    name="status">

                <option value="">
                    All statuses
                </option>

                <% for (IncidentStatus status
                        : statuses) { %>

                <option
                        value="<%= status.name() %>"
                        <%= status.name()
                                .equalsIgnoreCase(
                                        selectedStatus
                                )
                                ? "selected"
                                : "" %>>

                    <%= formatEnum(status) %>

                </option>

                <% } %>

            </select>

        </p>

        <p>

            <label for="priority">
                Priority:
            </label>

            <select id="priority"
                    name="priority">

                <option value="">
                    All priorities
                </option>

                <% for (IncidentPriority priority
                        : priorities) { %>

                <option
                        value="<%= priority.name() %>"
                        <%= priority.name()
                                .equalsIgnoreCase(
                                        selectedPriority
                                )
                                ? "selected"
                                : "" %>>

                    <%= formatEnum(priority) %>

                </option>

                <% } %>

            </select>

        </p>

        <p>

            <button type="submit">
                Apply filters
            </button>

            <a href="<%= contextPath %>/incidents/my">
                Reset
            </a>

        </p>

    </form>

    <hr>

    <h3>
        Assigned incidents
    </h3>

    <% if (incidents.isEmpty()) { %>

    <p>
        No incidents match the selected criteria.
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
                Title
            </th>

            <th>
                Priority
            </th>

            <th>
                Status
            </th>

            <th>
                Equipment ID
            </th>

            <th>
                Created
            </th>

            <th>
                Actions
            </th>

        </tr>

        </thead>

        <tbody>

        <% for (Incident incident
                : incidents) { %>

        <tr>

            <td>
                #<%= incident.getId() %>
            </td>

            <td>

                <strong>
                    <%= escapeHtml(
                            incident.getTitle()
                    ) %>
                </strong>

                <% if (incident.getDescription()
                        != null
                        && !incident
                        .getDescription()
                        .isBlank()) { %>

                <br>

                <small>

                    <%= escapeHtml(
                            incident.getDescription()
                    ) %>

                </small>

                <% } %>

            </td>

            <td>
                <%= formatEnum(
                        incident.getPriority()
                ) %>
            </td>

            <td>
                <%= formatEnum(
                        incident.getStatus()
                ) %>
            </td>

            <td>
                <%= incident.getEquipmentId() %>
            </td>

            <td>

                <% if (incident.getCreatedAt()
                        != null) { %>

                <%= escapeHtml(
                        incident.getCreatedAt()
                ) %>

                <% } else { %>

                Not available

                <% } %>

            </td>

            <td>

                <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>">
                    Details
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