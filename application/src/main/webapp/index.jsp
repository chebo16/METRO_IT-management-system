<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         session="true" %>

<%@ page import="com.chebo16.metroit.web.session.SessionConstants" %>
<%@ page import="com.chebo16.metroit.web.session.SessionUser" %>

<%
    String contextPath = request.getContextPath();

    Object authenticatedUserAttribute = session.getAttribute(
            SessionConstants.AUTHENTICATED_USER
    );

    if (!(authenticatedUserAttribute instanceof SessionUser)) {
        response.sendRedirect(contextPath + "/login");
        return;
    }

    response.sendRedirect(contextPath + "/dashboard");
%>