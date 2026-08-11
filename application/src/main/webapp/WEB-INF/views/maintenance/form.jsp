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
            Incidents
        </a>

        |

        <a href="<%= contextPath %>/incidents/my">
            My incidents
        </a>

        |

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
        Add maintenance record
    </h2>

    <% if (incident == null) { %>

    <p>
        Incident information is not available.
    </p>

    <p>

        <a href="<%= contextPath %>/incidents/my">
            Back to my incidents
        </a>

    </p>

    <% } else { %>

    <p>
        Record the maintenance work performed
        for this incident.
    </p>

    <hr>

    <h3>
        Incident information
    </h3>

    <p>

        <strong>
            Incident:
        </strong>

        #<%= incident.getId() %>
        -
        <%= escapeHtml(
                incident.getTitle()
        ) %>

    </p>

    <p>

        <strong>
            Status:
        </strong>

        <%= formatEnum(
                incident.getStatus()
        ) %>

    </p>

    <p>

        <strong>
            Priority:
        </strong>

        <%= formatEnum(
                incident.getPriority()
        ) %>

    </p>

    <p>

        <strong>
            Assigned technician ID:
        </strong>

        <%= displayValue(
                incident.getAssignedTechnicianId()
        ) %>

    </p>

    <hr>

    <h3>
        Equipment information
    </h3>

    <% if (equipment != null) { %>

    <p>

        <strong>
            Inventory number:
        </strong>

        <%= displayValue(
                equipment.getInventoryNumber()
        ) %>

    </p>

    <p>

        <strong>
            Equipment:
        </strong>

        <%= displayValue(
                equipment.getName()
        ) %>

    </p>

    <p>

        <strong>
            Type:
        </strong>

        <%= displayValue(
                equipment.getType()
        ) %>

    </p>

    <p>

        <strong>
            Manufacturer:
        </strong>

        <%= displayValue(
                equipment.getManufacturer()
        ) %>

    </p>

    <p>

        <strong>
            Model:
        </strong>

        <%= displayValue(
                equipment.getModel()
        ) %>

    </p>

    <% } else { %>

    <p>
        Equipment information is not available.
    </p>

    <% } %>

    <hr>

    <h3>
        Maintenance work
    </h3>

    <form method="post"
          action="<%= contextPath %>/maintenance/create">

        <input type="hidden"
               name="incidentId"
               value="<%= incident.getId() %>">

        <p>

            <label for="workDescription">

                <strong>
                    Work description:
                </strong>

            </label>

        </p>

        <p>

                <textarea id="workDescription"
                          name="workDescription"
                          rows="7"
                          cols="80"
                          maxlength="5000"
                          placeholder="Describe the maintenance work performed"
                          required></textarea>

        </p>

        <p>
            Describe the diagnostics, repair,
            configuration or other technical work performed.
        </p>

        <p>

            <label for="replacedComponents">

                <strong>
                    Replaced components:
                </strong>

            </label>

        </p>

        <p>

                <textarea id="replacedComponents"
                          name="replacedComponents"
                          rows="4"
                          cols="80"
                          maxlength="3000"
                          placeholder="Optional: list replaced components"></textarea>

        </p>

        <p>
            Leave this field empty if no components
            were replaced.
        </p>

        <p>

            <label for="result">

                <strong>
                    Maintenance result:
                </strong>

            </label>

        </p>

        <p>

            <select id="result"
                    name="result"
                    required>

                <option value="">
                    Select result
                </option>

                <% for (MaintenanceResult result
                        : results) { %>

                <option value="<%= result.name() %>">

                    <%= formatEnum(
                            result
                    ) %>

                </option>

                <% } %>

            </select>

        </p>

        <p>

            <button type="submit">
                Save maintenance record
            </button>

            <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>">
                Cancel
            </a>

        </p>

    </form>

    <hr>

    <p>

        <a href="<%= contextPath %>/incidents/details?id=<%= incident.getId() %>">
            Back to incident details
        </a>

    </p>

    <p>

        <a href="<%= contextPath %>/maintenance">
            View maintenance history
        </a>

    </p>

    <% } %>

</main>

</body>

</html>