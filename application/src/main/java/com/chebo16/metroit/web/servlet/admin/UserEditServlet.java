package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.UserService;
import com.chebo16.metroit.util.PasswordUtil;
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
import java.util.Locale;

@WebServlet(
        name = "UserEditServlet",
        urlPatterns = "/admin/users/edit"
)
public final class UserEditServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String USER_FORM_VIEW =
            "/WEB-INF/views/admin/users/form.jsp";

    private static final int MINIMUM_PASSWORD_LENGTH =
            8;

    private static final int MAXIMUM_PASSWORD_BYTES =
            72;

    private final UserService userService =
            new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            long userId =
                    parseUserId(
                            request.getParameter("id")
                    );

            User user =
                    userService.getUserById(userId);

            prepareEditForm(
                    request,
                    user
            );

            forwardToForm(
                    request,
                    response
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
                    "Unable to load the user account.",
                    exception
            );

            throw new ServletException(
                    "Unable to load the user account.",
                    exception
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        String userIdValue =
                request.getParameter("id");

        String username =
                normalizeText(
                        request.getParameter("username")
                );

        String fullName =
                normalizeText(
                        request.getParameter("fullName")
                );

        String email =
                normalizeText(
                        request.getParameter("email")
                );

        String roleValue =
                normalizeText(
                        request.getParameter("role")
                );

        String rawPassword =
                request.getParameter("password");

        String confirmedPassword =
                request.getParameter("confirmPassword");

        try {
            long userId =
                    parseUserId(userIdValue);

            User existingUser =
                    userService.getUserById(userId);

            UserRole selectedRole =
                    parseRole(roleValue);

            validateCurrentAccountRoleChange(
                    request,
                    existingUser,
                    selectedRole
            );

            validatePasswordChange(
                    rawPassword,
                    confirmedPassword
            );

            existingUser.setUsername(username);
            existingUser.setFullName(fullName);
            existingUser.setEmail(email);
            existingUser.setRole(selectedRole);

            if (hasNewPassword(rawPassword)) {

                String passwordHash =
                        PasswordUtil.hashPassword(
                                rawPassword
                        );

                existingUser.setPasswordHash(
                        passwordHash
                );
            }

            userService.updateUser(
                    existingUser
            );

            response.sendRedirect(
                    response.encodeRedirectURL(
                            request.getContextPath()
                                    + "/admin/users"
                                    + "?success=updated"
                    )
            );

        } catch (NotFoundException exception) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );

        } catch (ValidationException
                 | IllegalArgumentException exception) {

            prepareEditForm(
                    request,
                    userIdValue,
                    username,
                    fullName,
                    email,
                    roleValue
            );

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            forwardToForm(
                    request,
                    response
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to update the user account.",
                    exception
            );

            prepareEditForm(
                    request,
                    userIdValue,
                    username,
                    fullName,
                    email,
                    roleValue
            );

            request.setAttribute(
                    "errorMessage",
                    "The user account could not be updated. "
                            + "Please try again later."
            );

            forwardToForm(
                    request,
                    response
            );
        }
    }

    private void prepareEditForm(
            HttpServletRequest request,
            User user
    ) {

        prepareEditForm(
                request,
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private void prepareEditForm(
            HttpServletRequest request,
            String userId,
            String username,
            String fullName,
            String email,
            String roleValue
    ) {

        request.setAttribute(
                "pageTitle",
                "Edit user"
        );

        request.setAttribute(
                "formMode",
                "edit"
        );

        request.setAttribute(
                "formAction",
                request.getContextPath()
                        + "/admin/users/edit"
        );

        request.setAttribute(
                "submitLabel",
                "Save changes"
        );

        request.setAttribute(
                "availableRoles",
                UserRole.values()
        );

        request.setAttribute(
                "userId",
                userId
        );

        request.setAttribute(
                "username",
                username
        );

        request.setAttribute(
                "fullName",
                fullName
        );

        request.setAttribute(
                "email",
                email
        );

        request.setAttribute(
                "selectedRole",
                roleValue
        );
    }

    private long parseUserId(
            String userIdValue
    ) {

        if (userIdValue == null
                || userIdValue.isBlank()) {

            throw new ValidationException(
                    "User ID must be provided."
            );
        }

        try {
            long userId =
                    Long.parseLong(
                            userIdValue.trim()
                    );

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

    private UserRole parseRole(
            String roleValue
    ) {

        if (roleValue == null
                || roleValue.isBlank()) {

            throw new ValidationException(
                    "User role must be selected."
            );
        }

        try {
            return UserRole.valueOf(
                    roleValue
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected user role is invalid."
            );
        }
    }

    private void validateCurrentAccountRoleChange(
            HttpServletRequest request,
            User existingUser,
            UserRole selectedRole
    ) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return;
        }

        Object authenticatedUserAttribute =
                session.getAttribute(
                        SessionConstants.AUTHENTICATED_USER
                );

        if (!(authenticatedUserAttribute
                instanceof SessionUser sessionUser)) {

            return;
        }

        boolean editingCurrentAccount =
                sessionUser.getId().equals(
                        existingUser.getId()
                );

        boolean roleWasChanged =
                existingUser.getRole()
                        != selectedRole;

        if (editingCurrentAccount
                && roleWasChanged) {

            throw new ValidationException(
                    "You cannot change the role of "
                            + "your currently signed-in account."
            );
        }
    }

    private void validatePasswordChange(
            String rawPassword,
            String confirmedPassword
    ) {

        boolean passwordProvided =
                hasNewPassword(rawPassword);

        boolean confirmationProvided =
                confirmedPassword != null
                        && !confirmedPassword.isBlank();

        if (!passwordProvided
                && !confirmationProvided) {

            return;
        }

        if (!passwordProvided) {
            throw new ValidationException(
                    "Enter a new password before "
                            + "confirming it."
            );
        }

        if (rawPassword.length()
                < MINIMUM_PASSWORD_LENGTH) {

            throw new ValidationException(
                    "Password must contain at least "
                            + MINIMUM_PASSWORD_LENGTH
                            + " characters."
            );
        }

        int passwordBytes =
                rawPassword.getBytes(
                        StandardCharsets.UTF_8
                ).length;

        if (passwordBytes
                > MAXIMUM_PASSWORD_BYTES) {

            throw new ValidationException(
                    "Password must not exceed "
                            + MAXIMUM_PASSWORD_BYTES
                            + " UTF-8 bytes."
            );
        }

        if (confirmedPassword == null
                || !rawPassword.equals(
                confirmedPassword
        )) {

            throw new ValidationException(
                    "Password confirmation does not match."
            );
        }
    }

    private boolean hasNewPassword(
            String rawPassword
    ) {

        return rawPassword != null
                && !rawPassword.isBlank();
    }

    private String normalizeText(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(
                USER_FORM_VIEW
        ).forward(
                request,
                response
        );
    }
}