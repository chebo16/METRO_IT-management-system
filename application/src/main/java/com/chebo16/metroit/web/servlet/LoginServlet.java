package com.chebo16.metroit.web.servlet;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.service.AuthService;
import com.chebo16.metroit.web.session.SessionConstants;
import com.chebo16.metroit.web.session.SessionUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;

@WebServlet("/login")
public final class LoginServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String LOGIN_VIEW =
            "/WEB-INF/views/auth/login.jsp";

    private static final String ERROR_MESSAGE_ATTRIBUTE =
            "errorMessage";

    private static final String USERNAME_ATTRIBUTE =
            "username";

    private final AuthService authService =
            new AuthService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (isAuthenticated(session)) {
            redirectToHome(request, response);
            return;
        }

        forwardToLogin(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        String username =
                request.getParameter("username");

        String rawPassword =
                request.getParameter("password");

        try {
            User authenticatedUser =
                    authService.authenticate(
                            username,
                            rawPassword
                    );

            SessionUser sessionUser =
                    SessionUser.fromUser(
                            authenticatedUser
                    );

            HttpSession previousSession =
                    request.getSession(false);

            String originalRequestUri =
                    extractOriginalRequestUri(
                            previousSession
                    );

            /*
             * The previous session is invalidated after login
             * to protect the application from session fixation.
             */
            if (previousSession != null) {
                previousSession.invalidate();
            }

            HttpSession newSession =
                    request.getSession(true);

            newSession.setMaxInactiveInterval(
                    SessionConstants
                            .SESSION_TIMEOUT_SECONDS
            );

            newSession.setAttribute(
                    SessionConstants.AUTHENTICATED_USER,
                    sessionUser
            );

            String redirectTarget =
                    resolveRedirectTarget(
                            request,
                            originalRequestUri
                    );

            response.sendRedirect(
                    response.encodeRedirectURL(
                            redirectTarget
                    )
            );

        } catch (ValidationException exception) {

            request.setAttribute(
                    ERROR_MESSAGE_ATTRIBUTE,
                    exception.getMessage()
            );

            request.setAttribute(
                    USERNAME_ATTRIBUTE,
                    normalizeUsernameForView(username)
            );

            forwardToLogin(request, response);

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Authentication service error.",
                    exception
            );

            request.setAttribute(
                    ERROR_MESSAGE_ATTRIBUTE,
                    "Authentication service is temporarily "
                            + "unavailable. Please try again later."
            );

            request.setAttribute(
                    USERNAME_ATTRIBUTE,
                    normalizeUsernameForView(username)
            );

            forwardToLogin(request, response);
        }
    }

    private boolean isAuthenticated(
            HttpSession session
    ) {

        if (session == null) {
            return false;
        }

        return session.getAttribute(
                SessionConstants.AUTHENTICATED_USER
        ) instanceof SessionUser;
    }

    private String extractOriginalRequestUri(
            HttpSession session
    ) {

        if (session == null) {
            return null;
        }

        Object value =
                session.getAttribute(
                        SessionConstants
                                .ORIGINAL_REQUEST_URI
                );

        if (value instanceof String requestUri) {
            return requestUri;
        }

        return null;
    }

    private String resolveRedirectTarget(
            HttpServletRequest request,
            String originalRequestUri
    ) {

        if (isSafeInternalTarget(
                request,
                originalRequestUri
        )) {
            return originalRequestUri;
        }

        return request.getContextPath() + "/";
    }

    private boolean isSafeInternalTarget(
            HttpServletRequest request,
            String target
    ) {

        if (target == null || target.isBlank()) {
            return false;
        }

        if (target.contains("\r")
                || target.contains("\n")) {
            return false;
        }

        String contextPath =
                request.getContextPath();

        String requiredPrefix =
                contextPath.isEmpty()
                        ? "/"
                        : contextPath + "/";

        if (!target.startsWith(requiredPrefix)) {
            return false;
        }

        if (contextPath.isEmpty()
                && target.startsWith("//")) {
            return false;
        }

        String loginPath =
                contextPath + "/login";

        String logoutPath =
                contextPath + "/logout";

        return !target.equals(loginPath)
                && !target.startsWith(loginPath + "?")
                && !target.equals(logoutPath)
                && !target.startsWith(logoutPath + "?");
    }

    private String normalizeUsernameForView(
            String username
    ) {

        if (username == null) {
            return "";
        }

        return username.trim();
    }

    private void forwardToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(LOGIN_VIEW)
                .forward(request, response);
    }

    private void redirectToHome(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendRedirect(
                response.encodeRedirectURL(
                        request.getContextPath() + "/"
                )
        );
    }
}