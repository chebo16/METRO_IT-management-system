<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

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

    private static long longValue(
            Object value
    ) {

        if (value instanceof Number) {

            Number number =
                    (Number) value;

            return number.longValue();
        }

        return 0;
    }

    private static double percentage(
            long value,
            long total
    ) {

        if (total <= 0) {
            return 0.0;
        }

        return (value * 100.0)
                / total;
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

    if (sessionUser == null) {

        response.sendRedirect(
                contextPath + "/login"
        );

        return;
    }

    boolean administrator =
            sessionUser.isAdmin();

    long totalUsers =
            longValue(
                    request.getAttribute(
                            "totalUsers"
                    )
            );

    long activeUsers =
            longValue(
                    request.getAttribute(
                            "activeUsers"
                    )
            );

    long administratorUsers =
            longValue(
                    request.getAttribute(
                            "administratorUsers"
                    )
            );

    long technicianUsers =
            longValue(
                    request.getAttribute(
                            "technicianUsers"
                    )
            );

    long totalEquipment =
            longValue(
                    request.getAttribute(
                            "totalEquipment"
                    )
            );

    long activeEquipment =
            longValue(
                    request.getAttribute(
                            "activeEquipment"
                    )
            );

    long equipmentInRepair =
            longValue(
                    request.getAttribute(
                            "equipmentInRepair"
                    )
            );

    long inactiveEquipment =
            longValue(
                    request.getAttribute(
                            "inactiveEquipment"
                    )
            );

    long decommissionedEquipment =
            longValue(
                    request.getAttribute(
                            "decommissionedEquipment"
                    )
            );

    long totalIncidents =
            longValue(
                    request.getAttribute(
                            "totalIncidents"
                    )
            );

    long newIncidents =
            longValue(
                    request.getAttribute(
                            "newIncidents"
                    )
            );

    long inProgressIncidents =
            longValue(
                    request.getAttribute(
                            "inProgressIncidents"
                    )
            );

    long resolvedIncidents =
            longValue(
                    request.getAttribute(
                            "resolvedIncidents"
                    )
            );

    long closedIncidents =
            longValue(
                    request.getAttribute(
                            "closedIncidents"
                    )
            );

    long totalMaintenanceRecords =
            longValue(
                    request.getAttribute(
                            "totalMaintenanceRecords"
                    )
            );

    long successfulMaintenance =
            longValue(
                    request.getAttribute(
                            "successfulMaintenance"
                    )
            );

    long partiallyCompletedMaintenance =
            longValue(
                    request.getAttribute(
                            "partiallyCompletedMaintenance"
                    )
            );

    long failedMaintenance =
            longValue(
                    request.getAttribute(
                            "failedMaintenance"
                    )
            );

    double incidentClosureRate =
            percentage(
                    closedIncidents,
                    totalIncidents
            );

    double maintenanceSuccessRate =
            percentage(
                    successfulMaintenance,
                    totalMaintenanceRecords
            );
%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Dashboard | METRO IT Management
    </title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">

</head>

<body>

<header>

    <h1>
        METRO IT Management
    </h1>

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

    <nav>

        <a href="<%= contextPath %>/dashboard">
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

    <% if (administrator) { %>

    <h2>
        Administrator dashboard
    </h2>

    <p>
        System-wide overview of users,
        IT equipment, incidents and maintenance activities.
    </p>

    <% } else { %>

    <h2>
        Technician dashboard
    </h2>

    <p>
        Overview of your assigned incidents
        and maintenance activities.
    </p>

    <% } %>

    <% if (administrator) { %>

    <hr>

    <section>

        <h3>
            Users
        </h3>

        <table border="1"
               cellpadding="10"
               cellspacing="0">

            <thead>

            <tr>

                <th>
                    Total users
                </th>

                <th>
                    Active users
                </th>

                <th>
                    Administrators
                </th>

                <th>
                    Technicians
                </th>

            </tr>

            </thead>

            <tbody>

            <tr>

                <td>
                    <strong>
                        <%= totalUsers %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= activeUsers %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= administratorUsers %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= technicianUsers %>
                    </strong>
                </td>

            </tr>

            </tbody>

        </table>

        <p>

            <a href="<%= contextPath %>/admin/users">
                Manage users
            </a>

        </p>

    </section>

    <hr>

    <section>

        <h3>
            Equipment
        </h3>

        <table border="1"
               cellpadding="10"
               cellspacing="0">

            <thead>

            <tr>

                <th>
                    Total
                </th>

                <th>
                    Active
                </th>

                <th>
                    In repair
                </th>

                <th>
                    Inactive
                </th>

                <th>
                    Decommissioned
                </th>

            </tr>

            </thead>

            <tbody>

            <tr>

                <td>
                    <strong>
                        <%= totalEquipment %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= activeEquipment %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= equipmentInRepair %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= inactiveEquipment %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= decommissionedEquipment %>
                    </strong>
                </td>

            </tr>

            </tbody>

        </table>

        <p>

            <a href="<%= contextPath %>/equipment">
                View equipment
            </a>

        </p>

    </section>

    <% } %>

    <hr>

    <section>

        <% if (administrator) { %>

        <h3>
            Incidents
        </h3>

        <% } else { %>

        <h3>
            My assigned incidents
        </h3>

        <% } %>

        <table border="1"
               cellpadding="10"
               cellspacing="0">

            <thead>

            <tr>

                <th>
                    Total
                </th>

                <th>
                    New
                </th>

                <th>
                    In progress
                </th>

                <th>
                    Resolved
                </th>

                <th>
                    Closed
                </th>

            </tr>

            </thead>

            <tbody>

            <tr>

                <td>
                    <strong>
                        <%= totalIncidents %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= newIncidents %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= inProgressIncidents %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= resolvedIncidents %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= closedIncidents %>
                    </strong>
                </td>

            </tr>

            </tbody>

        </table>

        <p>

            <strong>
                Closure rate:
            </strong>

            <%= String.format(
                    "%.1f",
                    incidentClosureRate
            ) %>%

        </p>

        <% if (administrator) { %>

        <p>

            <a href="<%= contextPath %>/incidents">
                View all incidents
            </a>

        </p>

        <% } else { %>

        <p>

            <a href="<%= contextPath %>/incidents/my">
                View my incidents
            </a>

        </p>

        <% } %>

    </section>

    <hr>

    <section>

        <% if (administrator) { %>

        <h3>
            Maintenance
        </h3>

        <% } else { %>

        <h3>
            My maintenance activity
        </h3>

        <% } %>

        <table border="1"
               cellpadding="10"
               cellspacing="0">

            <thead>

            <tr>

                <th>
                    Total records
                </th>

                <th>
                    Successful
                </th>

                <th>
                    Partially completed
                </th>

                <th>
                    Failed
                </th>

            </tr>

            </thead>

            <tbody>

            <tr>

                <td>
                    <strong>
                        <%= totalMaintenanceRecords %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= successfulMaintenance %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= partiallyCompletedMaintenance %>
                    </strong>
                </td>

                <td>
                    <strong>
                        <%= failedMaintenance %>
                    </strong>
                </td>

            </tr>

            </tbody>

        </table>

        <p>

            <strong>
                Success rate:
            </strong>

            <%= String.format(
                    "%.1f",
                    maintenanceSuccessRate
            ) %>%

        </p>

        <p>

            <a href="<%= contextPath %>/maintenance">
                View maintenance history
            </a>

        </p>

    </section>

    <hr>

    <section>

        <h3>
            Quick actions
        </h3>

        <% if (administrator) { %>

        <p>

            <a href="<%= contextPath %>/admin/users">
                Manage users
            </a>

            |

            <a href="<%= contextPath %>/equipment">
                Manage equipment
            </a>

            |

            <a href="<%= contextPath %>/admin/incidents/create">
                Create incident
            </a>

            |

            <a href="<%= contextPath %>/incidents">
                Incident management
            </a>

            |

            <a href="<%= contextPath %>/maintenance">
                Maintenance history
            </a>

        </p>

        <% } else { %>

        <p>

            <a href="<%= contextPath %>/incidents/my">
                My incidents
            </a>

            |

            <a href="<%= contextPath %>/equipment">
                Equipment
            </a>

            |

            <a href="<%= contextPath %>/maintenance">
                My maintenance history
            </a>

        </p>

        <% } %>

    </section>

</main>

</body>

</html>