<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true"
         import="com.chebo16.metroit.web.session.SessionConstants"
         import="com.chebo16.metroit.web.session.SessionUser" %>

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
    String contextPath =
            request.getContextPath();

    Object authenticatedUserAttribute =
            session.getAttribute(
                    SessionConstants.AUTHENTICATED_USER
            );

    SessionUser sessionUser =
            authenticatedUserAttribute instanceof SessionUser
                    ? (SessionUser) authenticatedUserAttribute
                    : null;

    if (sessionUser == null) {

        response.sendRedirect(
                contextPath + "/login"
        );

        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Home | METRO IT Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">
</head>

<body>

<main>

    <section>

        <h1>
            METRO IT Management System
        </h1>

        <p>
            Authentication completed successfully.
        </p>

        <dl>

            <dt>Full name</dt>

            <dd>
                <%= escapeHtml(sessionUser.getFullName()) %>
            </dd>

            <dt>Username</dt>

            <dd>
                <%= escapeHtml(sessionUser.getUsername()) %>
            </dd>

            <dt>Role</dt>

            <dd>
                <%= escapeHtml(sessionUser.getRole()) %>
            </dd>

        </dl>

        <% if (sessionUser.isAdmin()) { %>

        <p>
            Administrative access is enabled.
        </p>

        <% } else { %>

        <p>
            Technician access is enabled.
        </p>

        <% } %>

        <form method="post"
              action="<%= contextPath %>/logout">

            <button type="submit">
                Sign out
            </button>

        </form>

    </section>

</main>

</body>

</html>
