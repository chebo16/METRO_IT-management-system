package com.chebo16.metroit.web.filter;

import com.chebo16.metroit.web.session.SessionConstants;
import com.chebo16.metroit.web.session.SessionUser;
import jakarta.servlet.DispatcherType;
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
        filterName = "RoleAuthorizationFilter",
        urlPatterns = "/admin/*",
        dispatcherTypes = DispatcherType.REQUEST
)
public final class RoleAuthorizationFilter implements Filter {

    private static final String LOGIN_PATH = "/login";
    private static final String ACCESS_DENIED_VIEW =
            "/WEB-INF/views/auth/access-denied.jsp";

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

        HttpSession session = request.getSession(false);
        SessionUser sessionUser = getSessionUser(session);

        if (sessionUser == null) {
            saveOriginalRequest(request);
            redirectToLogin(request, response);
            return;
        }

        if (!sessionUser.isAdmin()) {
            preventPageCaching(response);

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            request.getRequestDispatcher(
                    ACCESS_DENIED_VIEW
            ).forward(request, response);

            return;
        }

        preventPageCaching(response);
        filterChain.doFilter(request, response);
    }

    private SessionUser getSessionUser(HttpSession session) {
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

    private void saveOriginalRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        HttpSession session = request.getSession(true);

        Object existingTarget =
                session.getAttribute(
                        SessionConstants.ORIGINAL_REQUEST_URI
                );

        if (existingTarget != null) {
            return;
        }

        String originalRequestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null && !queryString.isBlank()) {
            originalRequestUri =
                    originalRequestUri + "?" + queryString;
        }

        session.setAttribute(
                SessionConstants.ORIGINAL_REQUEST_URI,
                originalRequestUri
        );
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String loginUrl =
                request.getContextPath() + LOGIN_PATH;

        response.sendRedirect(
                response.encodeRedirectURL(loginUrl)
        );
    }

    private void preventPageCaching(
            HttpServletResponse response
    ) {
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}