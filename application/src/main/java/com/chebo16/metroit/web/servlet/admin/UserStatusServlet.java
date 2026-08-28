package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.service.UserService;
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

@WebServlet(
        name = "UserStatusServlet",
        urlPatterns = "/admin/users/status"
)
public final class UserStatusServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UserService userService =
            new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "User status can only be changed using a POST request."
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            long userId =
                    parseUserId(request.getParameter("id"));

            boolean requestedActiveStatus =
                    parseActiveStatus(
                            request.getParameter("active")
                    );

            User user = userService.getUserById(userId);

            validateCurrentAccountStatusChange(
                    request,
                    user
            );

            if (user.isActive() != requestedActiveStatus) {
                user.setActive(requestedActiveStatus);
                userService.updateUser(user);
            }

            String successValue =
                    requestedActiveStatus
                            ? "activated"
                            : "deactivated";

            String redirectUrl =
                    request.getContextPath()
                            + "/admin/users"
                            + "?success="
                            + successValue;

            response.sendRedirect(
                    response.encodeRedirectURL(redirectUrl)
            );

        } catch (ValidationException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (NotFoundException exception) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );

        } catch (ServiceException exception) {
            getServletContext().log(
                    "Unable to change the user account status.",
                    exception
            );

            throw new ServletException(
                    "Unable to change the user account status.",
                    exception
            );
        }
    }

    private long parseUserId(String userIdValue) {
        if (userIdValue == null || userIdValue.isBlank()) {
            throw new ValidationException(
                    "User ID must be provided."
            );
        }

        try {
            long userId =
                    Long.parseLong(userIdValue.trim());

            if (userId <= 0) {
                throw new ValidationException(
                        "User ID must be greater than zero."
                );
            }

            return userId;

        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "User ID must be a valid number."
            );
        }
    }

    private boolean parseActiveStatus(String activeValue) {
        if (activeValue == null || activeValue.isBlank()) {
            throw new ValidationException(
                    "User status must be provided."
            );
        }

        String normalizedValue = activeValue.trim();

        if ("true".equalsIgnoreCase(normalizedValue)) {
            return true;
        }

        if ("false".equalsIgnoreCase(normalizedValue)) {
            return false;
        }

        throw new ValidationException(
                "User status must be true or false."
        );
    }

    private void validateCurrentAccountStatusChange(
            HttpServletRequest request,
            User targetUser
    ) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        Object authenticatedUser = session.getAttribute(
                SessionConstants.AUTHENTICATED_USER
        );

        if (!(authenticatedUser instanceof SessionUser sessionUser)) {
            return;
        }

        boolean changingCurrentAccount =
                sessionUser.getId().equals(targetUser.getId());

        if (changingCurrentAccount) {
            throw new ValidationException(
                    "You cannot activate or deactivate "
                            + "your currently signed-in account."
            );
        }
    }
}