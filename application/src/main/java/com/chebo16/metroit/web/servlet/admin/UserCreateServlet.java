package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.UserService;
import com.chebo16.metroit.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@WebServlet(
        name = "UserCreateServlet",
        urlPatterns = "/admin/users/create"
)
public final class UserCreateServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String USER_FORM_VIEW =
            "/WEB-INF/views/admin/users/form.jsp";

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_BYTES = 72;

    private final UserService userService =
            new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        prepareCreateForm(request);

        request.getRequestDispatcher(USER_FORM_VIEW)
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String username =
                normalizeText(request.getParameter("username"));

        String fullName =
                normalizeText(request.getParameter("fullName"));

        String email =
                normalizeText(request.getParameter("email"));

        String roleValue =
                normalizeText(request.getParameter("role"));

        String rawPassword =
                request.getParameter("password");

        String confirmedPassword =
                request.getParameter("confirmPassword");

        try {
            UserRole role = parseRole(roleValue);

            validatePasswords(
                    rawPassword,
                    confirmedPassword
            );

            String passwordHash =
                    PasswordUtil.hashPassword(rawPassword);

            User newUser = new User(
                    username,
                    passwordHash,
                    fullName,
                    email,
                    role
            );

            userService.createUser(newUser);

            response.sendRedirect(
                    response.encodeRedirectURL(
                            request.getContextPath()
                                    + "/admin/users"
                                    + "?success=created"
                    )
            );

        } catch (ValidationException
                 | IllegalArgumentException exception) {

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    username,
                    fullName,
                    email,
                    roleValue
            );

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.getRequestDispatcher(USER_FORM_VIEW)
                    .forward(request, response);

        } catch (ServiceException exception) {
            getServletContext().log(
                    "Unable to create the user account.",
                    exception
            );

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    username,
                    fullName,
                    email,
                    roleValue
            );

            request.setAttribute(
                    "errorMessage",
                    "The user account could not be created. "
                            + "Please try again later."
            );

            request.getRequestDispatcher(USER_FORM_VIEW)
                    .forward(request, response);
        }
    }

    private void prepareCreateForm(HttpServletRequest request) {
        request.setAttribute("pageTitle", "Add user");
        request.setAttribute("formMode", "create");

        request.setAttribute(
                "formAction",
                request.getContextPath()
                        + "/admin/users/create"
        );

        request.setAttribute(
                "submitLabel",
                "Create user"
        );

        request.setAttribute(
                "availableRoles",
                UserRole.values()
        );

        if (request.getAttribute("selectedRole") == null) {
            request.setAttribute(
                    "selectedRole",
                    UserRole.TECHNICIAN.name()
            );
        }
    }

    private void preserveFormValues(
            HttpServletRequest request,
            String username,
            String fullName,
            String email,
            String roleValue
    ) {
        request.setAttribute("username", username);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);

        if (roleValue == null || roleValue.isBlank()) {
            request.setAttribute(
                    "selectedRole",
                    UserRole.TECHNICIAN.name()
            );
        } else {
            request.setAttribute(
                    "selectedRole",
                    roleValue
            );
        }
    }

    private UserRole parseRole(String roleValue) {
        if (roleValue == null || roleValue.isBlank()) {
            throw new ValidationException(
                    "User role must be selected."
            );
        }

        try {
            return UserRole.valueOf(
                    roleValue.trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(
                    "Selected user role is invalid."
            );
        }
    }

    private void validatePasswords(
            String rawPassword,
            String confirmedPassword
    ) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ValidationException(
                    "Password must not be empty."
            );
        }

        if (rawPassword.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new ValidationException(
                    "Password must contain at least "
                            + MINIMUM_PASSWORD_LENGTH
                            + " characters."
            );
        }

        int passwordBytes =
                rawPassword.getBytes(StandardCharsets.UTF_8).length;

        if (passwordBytes > MAXIMUM_PASSWORD_BYTES) {
            throw new ValidationException(
                    "Password must not exceed "
                            + MAXIMUM_PASSWORD_BYTES
                            + " UTF-8 bytes."
            );
        }

        if (confirmedPassword == null
                || !rawPassword.equals(confirmedPassword)) {

            throw new ValidationException(
                    "Password confirmation does not match."
            );
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}