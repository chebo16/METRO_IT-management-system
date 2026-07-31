package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet(
        name = "UserListServlet",
        urlPatterns = "/admin/users"
)
public final class UserListServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String USER_LIST_VIEW =
            "/WEB-INF/views/admin/users/list.jsp";

    private final UserService userService =
            new UserService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<User> users =
                    userService.getAllUsers();

            long activeUsers =
                    users.stream()
                            .filter(User::isActive)
                            .count();

            long inactiveUsers =
                    users.size() - activeUsers;

            long adminUsers =
                    users.stream()
                            .filter(user ->
                                    user.getRole()
                                            == UserRole.ADMIN
                            )
                            .count();

            long technicianUsers =
                    users.stream()
                            .filter(user ->
                                    user.getRole()
                                            == UserRole.TECHNICIAN
                            )
                            .count();

            request.setAttribute(
                    "users",
                    users
            );

            request.setAttribute(
                    "totalUsers",
                    users.size()
            );

            request.setAttribute(
                    "activeUsers",
                    activeUsers
            );

            request.setAttribute(
                    "inactiveUsers",
                    inactiveUsers
            );

            request.setAttribute(
                    "adminUsers",
                    adminUsers
            );

            request.setAttribute(
                    "technicianUsers",
                    technicianUsers
            );

            request.getRequestDispatcher(
                    USER_LIST_VIEW
            ).forward(
                    request,
                    response
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to load the user list.",
                    exception
            );

            throw new ServletException(
                    "Unable to load the user list.",
                    exception
            );
        }
    }
}