<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="false"
         import="jakarta.servlet.http.HttpServletResponse" %>

<%
    response.setStatus(
            HttpServletResponse.SC_FORBIDDEN
    );

    String contextPath =
            request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Access denied | METRO IT Management</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">
</head>

<body class="error-page">

<main class="error-container">

    <section class="error-card"
             aria-labelledby="error-title">

        <div class="error-code"
             aria-hidden="true">
            403
        </div>

        <h1 id="error-title">
            Access denied
        </h1>

        <p class="error-message">
            You do not have permission to access
            the requested page or perform this action.
        </p>

        <div class="error-actions">

            <a href="<%= contextPath %>/"
               class="button button-primary">

                Return to dashboard

            </a>

            <form method="post"
                  action="<%= contextPath %>/logout"
                  class="inline-form">

                <button type="submit"
                        class="button button-secondary">

                    Sign out

                </button>

            </form>

        </div>

        <p class="error-help">
            Contact the system administrator if you
            believe your access permissions are incorrect.
        </p>

    </section>

</main>

</body>

</html>