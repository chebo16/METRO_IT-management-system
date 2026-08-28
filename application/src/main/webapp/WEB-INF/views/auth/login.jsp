<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="false" %>

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

    String errorMessage =
            escapeHtml(request.getAttribute("errorMessage"));

    String username =
            escapeHtml(request.getAttribute("username"));

    boolean logoutSuccessful =
            "true".equals(request.getParameter("logout"));
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Sign in | METRO IT Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">
</head>

<body class="login-page">

<main class="login-container">

    <section class="login-card"
             aria-labelledby="login-title">

        <header class="login-header">
            <div class="login-brand">
                METRO
            </div>

            <h1 id="login-title">
                IT Management System
            </h1>

            <p class="login-subtitle">
                Sign in to manage IT equipment, incidents and maintenance activities.
            </p>
        </header>

        <% if (logoutSuccessful) { %>

        <div class="alert alert-success"
             role="status"
             aria-live="polite">
            You have been signed out successfully.
        </div>

        <% } %>

        <% if (!errorMessage.isEmpty()) { %>

        <div class="alert alert-error"
             role="alert"
             aria-live="assertive">
            <%= errorMessage %>
        </div>

        <% } %>

        <form method="post"
              action="<%= contextPath %>/login"
              class="login-form"
              autocomplete="on">

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
                       placeholder="Enter your username"
                       required
                       autofocus>
            </div>

            <div class="form-group">
                <label for="password">
                    Password
                </label>

                <input type="password"
                       id="password"
                       name="password"
                       autocomplete="current-password"
                       placeholder="Enter your password"
                       required>
            </div>

            <div class="login-actions">
                <button type="submit"
                        class="button button-primary login-submit">
                    Sign in
                </button>
            </div>

        </form>

        <footer class="login-footer">
            <p>
                Access is restricted to authorized METRO personnel.
            </p>
        </footer>

    </section>

</main>

</body>

</html>