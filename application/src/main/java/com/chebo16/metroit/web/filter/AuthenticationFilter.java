package com.chebo16.metroit.web.filter;

import com.chebo16.metroit.web.session.SessionConstants;
import com.chebo16.metroit.web.session.SessionUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = "/*"
)
public final class AuthenticationFilter implements Filter {

    private static final String LOGIN_PATH =
            "/login";

    private static final String LOGOUT_PATH =
            "/logout";

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        String applicationPath =
                getApplicationPath(request);

        if (isPublicResource(applicationPath)) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        HttpSession session =
                request.getSession(false);

        SessionUser sessionUser =
                getAuthenticatedUser(session);

        if (sessionUser != null) {

            preventProtectedPageCaching(response);

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        saveOriginalRequest(request);

        String loginUrl =
                request.getContextPath()
                        + LOGIN_PATH;

        response.sendRedirect(
                response.encodeRedirectURL(loginUrl)
        );
    }

    private String getApplicationPath(
            HttpServletRequest request
    ) {

        String requestUri =
                request.getRequestURI();

        String contextPath =
                request.getContextPath();

        return requestUri.substring(
                contextPath.length()
        );
    }

    private boolean isPublicResource(
            String applicationPath
    ) {

        if (applicationPath.equals(LOGIN_PATH)
                || applicationPath.equals(LOGOUT_PATH)) {

            return true;
        }

        return applicationPath.startsWith("/css/")
                || applicationPath.startsWith("/js/")
                || applicationPath.startsWith("/images/")
                || applicationPath.startsWith("/fonts/")
                || applicationPath.equals("/favicon.ico");
    }

    private SessionUser getAuthenticatedUser(
            HttpSession session
    ) {

        if (session == null) {
            return null;
        }

        Object sessionAttribute =
                session.getAttribute(
                        SessionConstants.AUTHENTICATED_USER
                );

        if (sessionAttribute instanceof SessionUser sessionUser) {
            return sessionUser;
        }

        return null;
    }

    private void saveOriginalRequest(
            HttpServletRequest request
    ) {

        /*
         * Only GET requests are saved because redirecting
         * back to a POST request would lose its request body.
         */
        if (!"GET".equalsIgnoreCase(
                request.getMethod()
        )) {
            return;
        }

        HttpSession session =
                request.getSession(true);

        Object existingTarget =
                session.getAttribute(
                        SessionConstants.ORIGINAL_REQUEST_URI
                );

        if (existingTarget != null) {
            return;
        }

        String originalRequestUri =
                request.getRequestURI();

        String queryString =
                request.getQueryString();

        if (queryString != null
                && !queryString.isBlank()) {

            originalRequestUri =
                    originalRequestUri
                            + "?"
                            + queryString;
        }

        session.setAttribute(
                SessionConstants.ORIGINAL_REQUEST_URI,
                originalRequestUri
        );
    }

    private void preventProtectedPageCaching(
            HttpServletResponse response
    ) {

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );
    }
}