<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.chebo16.metroit.model.User" %>
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

    private static long getLongAttribute(Object attribute) {
        if (attribute instanceof Number) {
            Number number = (Number) attribute;
            return number.longValue();
        }

        return 0L;
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

    @SuppressWarnings("unchecked")
    List<User> users =
            request.getAttribute("users") instanceof List<?>
                    ? (List<User>) request.getAttribute("users")
                    : Collections.emptyList();

    long totalUsers =
            getLongAttribute(request.getAttribute("totalUsers"));

    long activeUsers =
            getLongAttribute(request.getAttribute("activeUsers"));

    long inactiveUsers =
            getLongAttribute(request.getAttribute("inactiveUsers"));

    long adminUsers =
            getLongAttribute(request.getAttribute("adminUsers"));

    long technicianUsers =
            getLongAttribute(request.getAttribute("technicianUsers"));

    String successParameter = request.getParameter("success");
    String successMessage = "";

    if ("created".equals(successParameter)) {
        successMessage =
                "User was created successfully.";
    } else if ("updated".equals(successParameter)) {
        successMessage =
                "User was updated successfully.";
    } else if ("activated".equals(successParameter)) {
        successMessage =
                "User account was activated successfully.";
    } else if ("deactivated".equals(successParameter)) {
        successMessage =
                "User account was deactivated successfully.";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Users | METRO IT Management</title>

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
            <h2>
                User management
            </h2>

            <p>
                Manage administrator and technician
                accounts.
            </p>
        </div>

        <a href="<%= contextPath %>/admin/users/create"
           class="button button-primary">
            Add user
        </a>
    </div>

    <% if (!successMessage.isEmpty()) { %>

    <div class="success-message"
         role="status">
        <%= escapeHtml(successMessage) %>
    </div>

    <% } %>

    <section class="statistics-grid"
             aria-label="User statistics">

        <article class="statistics-card">
            <span class="statistics-label">
                Total users
            </span>

            <strong class="statistics-value">
                <%= totalUsers %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Active
            </span>

            <strong class="statistics-value">
                <%= activeUsers %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Inactive
            </span>

            <strong class="statistics-value">
                <%= inactiveUsers %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Administrators
            </span>

            <strong class="statistics-value">
                <%= adminUsers %>
            </strong>
        </article>

        <article class="statistics-card">
            <span class="statistics-label">
                Technicians
            </span>

            <strong class="statistics-value">
                <%= technicianUsers %>
            </strong>
        </article>

    </section>

    <section class="content-card"
             aria-labelledby="users-table-title">

        <div class="content-card-header">
            <h2 id="users-table-title">
                System users
            </h2>

            <span>
                <%= totalUsers %> account(s)
            </span>
        </div>

        <% if (users.isEmpty()) { %>

        <div class="empty-state">
            <h3>
                No users found
            </h3>

            <p>
                Create the first system user account.
            </p>

            <a href="<%= contextPath %>/admin/users/create"
               class="button button-primary">
                Add user
            </a>
        </div>

        <% } else { %>

        <div class="table-container">
            <table class="data-table">
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Full name</th>
                    <th scope="col">Username</th>
                    <th scope="col">Email</th>
                    <th scope="col">Role</th>
                    <th scope="col">Status</th>
                    <th scope="col">Created</th>
                    <th scope="col">Actions</th>
                </tr>
                </thead>

                <tbody>

                <% for (User user : users) { %>

                <tr>
                    <td>
                        <%= user.getId() %>
                    </td>

                    <td>
                        <strong>
                            <%= escapeHtml(user.getFullName()) %>
                        </strong>

                        <% if (sessionUser != null
                                && sessionUser.getId().equals(user.getId())) { %>

                        <span class="current-user-label">
                            Current account
                        </span>

                        <% } %>
                    </td>

                    <td>
                        <%= escapeHtml(user.getUsername()) %>
                    </td>

                    <td>
                        <%= escapeHtml(user.getEmail()) %>
                    </td>

                    <td>
                        <span class="role-badge
                                <%= user.getRole().name().equals("ADMIN")
                                        ? "role-admin"
                                        : "role-technician" %>">

                            <%= escapeHtml(user.getRole()) %>
                        </span>
                    </td>

                    <td>
                        <% if (user.isActive()) { %>

                        <span class="status-badge status-active">
                            Active
                        </span>

                        <% } else { %>

                        <span class="status-badge status-inactive">
                            Inactive
                        </span>

                        <% } %>
                    </td>

                    <td>
                        <%= escapeHtml(user.getCreatedAt()) %>
                    </td>

                    <td>
                        <div class="table-actions">

                            <a href="<%= contextPath %>/admin/users/edit?id=<%= user.getId() %>"
                               class="button button-small button-secondary">
                                Edit
                            </a>

                            <% if (sessionUser == null
                                    || !sessionUser.getId().equals(user.getId())) { %>

                            <form method="post"
                                  action="<%= contextPath %>/admin/users/status"
                                  class="inline-form">

                                <input type="hidden"
                                       name="id"
                                       value="<%= user.getId() %>">

                                <input type="hidden"
                                       name="active"
                                       value="<%= !user.isActive() %>">

                                <button type="submit"
                                        class="button button-small
                                               <%= user.isActive()
                                                       ? "button-danger"
                                                       : "button-primary" %>">

                                    <%= user.isActive()
                                            ? "Deactivate"
                                            : "Activate" %>
                                </button>

                            </form>

                            <% } %>

                        </div>
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