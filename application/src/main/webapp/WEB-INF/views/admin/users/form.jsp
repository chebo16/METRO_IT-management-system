<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.model.enums.UserRole" %>
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
        pageTitle = "User form";
    }

    String formMode =
            escapeHtml(request.getAttribute("formMode"));

    boolean editMode =
            "edit".equalsIgnoreCase(formMode);

    String formAction =
            escapeHtml(request.getAttribute("formAction"));

    if (formAction.isEmpty()) {
        formAction =
                contextPath + "/admin/users/create";
    }

    String submitLabel =
            escapeHtml(request.getAttribute("submitLabel"));

    if (submitLabel.isEmpty()) {
        submitLabel =
                editMode
                        ? "Save changes"
                        : "Create user";
    }

    String username =
            escapeHtml(request.getAttribute("username"));

    String fullName =
            escapeHtml(request.getAttribute("fullName"));

    String email =
            escapeHtml(request.getAttribute("email"));

    String selectedRole =
            escapeHtml(request.getAttribute("selectedRole"));

    if (selectedRole.isEmpty()) {
        selectedRole = UserRole.TECHNICIAN.name();
    }

    String userId =
            escapeHtml(request.getAttribute("userId"));

    String errorMessage =
            escapeHtml(request.getAttribute("errorMessage"));

    UserRole[] availableRoles = UserRole.values();

    Object availableRolesAttribute =
            request.getAttribute("availableRoles");

    if (availableRolesAttribute instanceof UserRole[]) {
        availableRoles =
                (UserRole[]) availableRolesAttribute;
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

                Update the user account information
                and access role.

                <% } else { %>

                Create a new administrator or
                technician account.

                <% } %>
            </p>
        </div>

        <a href="<%= contextPath %>/admin/users"
           class="button button-secondary">
            Back to users
        </a>
    </div>

    <% if (!errorMessage.isEmpty()) { %>

    <div class="error-message"
         role="alert">
        <%= errorMessage %>
    </div>

    <% } %>

    <section class="content-card user-form-card"
             aria-labelledby="user-form-title">

        <div class="content-card-header">
            <h2 id="user-form-title">
                <% if (editMode) { %>

                Account details

                <% } else { %>

                New user details

                <% } %>
            </h2>
        </div>

        <form method="post"
              action="<%= formAction %>"
              class="user-form"
              autocomplete="off">

            <% if (editMode && !userId.isEmpty()) { %>

            <input type="hidden"
                   name="id"
                   value="<%= userId %>">

            <% } %>

            <div class="form-grid">

                <div class="form-group">
                    <label for="fullName">
                        Full name
                    </label>

                    <input type="text"
                           id="fullName"
                           name="fullName"
                           value="<%= fullName %>"
                           maxlength="100"
                           autocomplete="name"
                           required>

                    <small class="form-help">
                        Employee's first and last name.
                    </small>
                </div>

                <div class="form-group">
                    <label for="username">
                        Username
                    </label>

                    <input type="text"
                           id="username"
                           name="username"
                           value="<%= username %>"
                           maxlength="50"
                           autocomplete="username"
                           required>

                    <small class="form-help">
                        Username must be unique.
                    </small>
                </div>

                <div class="form-group">
                    <label for="email">
                        Email
                    </label>

                    <input type="email"
                           id="email"
                           name="email"
                           value="<%= email %>"
                           maxlength="150"
                           autocomplete="email"
                           required>

                    <small class="form-help">
                        Email address must be unique.
                    </small>
                </div>

                <div class="form-group">
                    <label for="role">
                        Role
                    </label>

                    <select id="role"
                            name="role"
                            required>

                        <% for (UserRole role : availableRoles) { %>

                        <option value="<%= role.name() %>"
                                <%= role.name().equals(selectedRole)
                                        ? "selected"
                                        : "" %>>
                            <%= escapeHtml(role.name()) %>
                        </option>

                        <% } %>

                    </select>

                    <small class="form-help">
                        Administrators manage users and
                        system data. Technicians process
                        assigned incidents and maintenance.
                    </small>
                </div>

                <div class="form-group">
                    <label for="password">
                        Password
                    </label>

                    <input type="password"
                           id="password"
                           name="password"
                           minlength="8"
                           maxlength="72"
                           autocomplete="new-password"
                        <%= editMode ? "" : "required" %>>

                    <small class="form-help">
                        <% if (editMode) { %>

                        Leave empty to keep the current
                        password. Enter at least
                        8 characters to change it.

                        <% } else { %>

                        Password must contain at least
                        8 characters.

                        <% } %>
                    </small>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">
                        Confirm password
                    </label>

                    <input type="password"
                           id="confirmPassword"
                           name="confirmPassword"
                           minlength="8"
                           maxlength="72"
                           autocomplete="new-password"
                        <%= editMode ? "" : "required" %>>

                    <small class="form-help">
                        Enter the same password again.
                    </small>
                </div>

            </div>

            <div class="form-actions">
                <button type="submit"
                        class="button button-primary">
                    <%= submitLabel %>
                </button>

                <a href="<%= contextPath %>/admin/users"
                   class="button button-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </section>

</main>

</body>

</html>