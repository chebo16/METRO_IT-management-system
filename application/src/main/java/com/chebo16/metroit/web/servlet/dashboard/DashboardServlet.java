package com.chebo16.metroit.web.servlet.dashboard;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.EquipmentStatus;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.EquipmentService;
import com.chebo16.metroit.service.IncidentService;
import com.chebo16.metroit.service.MaintenanceRecordService;
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
import java.util.List;

@WebServlet(
        name = "DashboardServlet",
        urlPatterns = "/dashboard"
)
public final class DashboardServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DASHBOARD_VIEW =
            "/WEB-INF/views/dashboard/index.jsp";

    private final UserService userService =
            new UserService();

    private final EquipmentService equipmentService =
            new EquipmentService();

    private final IncidentService incidentService =
            new IncidentService();

    private final MaintenanceRecordService maintenanceRecordService =
            new MaintenanceRecordService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        SessionUser sessionUser =
                getSessionUser(
                        request.getSession(false)
                );

        if (sessionUser == null) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/login"
            );

            return;
        }

        try {

            if (sessionUser.isAdmin()) {

                loadAdministratorStatistics(
                        request
                );

            } else {

                loadTechnicianStatistics(
                        request,
                        sessionUser
                );
            }

            request.getRequestDispatcher(
                    DASHBOARD_VIEW
            ).forward(
                    request,
                    response
            );

        } catch (ValidationException exception) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (ServiceException exception) {

            getServletContext().log(
                    "Unable to load dashboard statistics.",
                    exception
            );

            throw new ServletException(
                    "Unable to load dashboard statistics.",
                    exception
            );
        }
    }

    private void loadAdministratorStatistics(
            HttpServletRequest request
    ) {

        List<User> users =
                userService.getAllUsers();

        List<Equipment> equipment =
                equipmentService.getAllEquipment();

        List<Incident> incidents =
                incidentService.getAllIncidents();

        List<MaintenanceRecord> maintenanceRecords =
                maintenanceRecordService.getAllRecords();

        request.setAttribute(
                "totalUsers",
                users.size()
        );

        request.setAttribute(
                "activeUsers",
                countActiveUsers(
                        users
                )
        );

        request.setAttribute(
                "administratorUsers",
                countUsersByRole(
                        users,
                        UserRole.ADMIN
                )
        );

        request.setAttribute(
                "technicianUsers",
                countUsersByRole(
                        users,
                        UserRole.TECHNICIAN
                )
        );

        request.setAttribute(
                "totalEquipment",
                equipment.size()
        );

        request.setAttribute(
                "activeEquipment",
                countEquipmentByStatus(
                        equipment,
                        EquipmentStatus.ACTIVE
                )
        );

        request.setAttribute(
                "equipmentInRepair",
                countEquipmentByStatus(
                        equipment,
                        EquipmentStatus.IN_REPAIR
                )
        );

        request.setAttribute(
                "inactiveEquipment",
                countEquipmentByStatus(
                        equipment,
                        EquipmentStatus.INACTIVE
                )
        );

        request.setAttribute(
                "decommissionedEquipment",
                countEquipmentByStatus(
                        equipment,
                        EquipmentStatus.DECOMMISSIONED
                )
        );

        setIncidentStatistics(
                request,
                incidents
        );

        setMaintenanceStatistics(
                request,
                maintenanceRecords
        );
    }

    private void loadTechnicianStatistics(
            HttpServletRequest request,
            SessionUser sessionUser
    ) {

        List<Incident> incidents =
                incidentService.getIncidentsByTechnician(
                        sessionUser.getId()
                );

        List<MaintenanceRecord> maintenanceRecords =
                maintenanceRecordService.getRecordsByTechnician(
                        sessionUser.getId()
                );

        setIncidentStatistics(
                request,
                incidents
        );

        setMaintenanceStatistics(
                request,
                maintenanceRecords
        );
    }

    private void setIncidentStatistics(
            HttpServletRequest request,
            List<Incident> incidents
    ) {

        request.setAttribute(
                "totalIncidents",
                incidents.size()
        );

        request.setAttribute(
                "newIncidents",
                countIncidentsByStatus(
                        incidents,
                        IncidentStatus.NEW
                )
        );

        request.setAttribute(
                "inProgressIncidents",
                countIncidentsByStatus(
                        incidents,
                        IncidentStatus.IN_PROGRESS
                )
        );

        request.setAttribute(
                "resolvedIncidents",
                countIncidentsByStatus(
                        incidents,
                        IncidentStatus.RESOLVED
                )
        );

        request.setAttribute(
                "closedIncidents",
                countIncidentsByStatus(
                        incidents,
                        IncidentStatus.CLOSED
                )
        );
    }

    private void setMaintenanceStatistics(
            HttpServletRequest request,
            List<MaintenanceRecord> records
    ) {

        request.setAttribute(
                "totalMaintenanceRecords",
                records.size()
        );

        request.setAttribute(
                "successfulMaintenance",
                countMaintenanceByResult(
                        records,
                        MaintenanceResult.SUCCESS
                )
        );

        request.setAttribute(
                "partiallyCompletedMaintenance",
                countMaintenanceByResult(
                        records,
                        MaintenanceResult.PARTIALLY_COMPLETED
                )
        );

        request.setAttribute(
                "failedMaintenance",
                countMaintenanceByResult(
                        records,
                        MaintenanceResult.FAILED
                )
        );
    }

    private long countActiveUsers(
            List<User> users
    ) {

        long count = 0;

        for (User user : users) {

            if (user.isActive()) {
                count++;
            }
        }

        return count;
    }

    private long countUsersByRole(
            List<User> users,
            UserRole role
    ) {

        long count = 0;

        for (User user : users) {

            if (user.getRole() == role) {
                count++;
            }
        }

        return count;
    }

    private long countEquipmentByStatus(
            List<Equipment> equipment,
            EquipmentStatus status
    ) {

        long count = 0;

        for (Equipment item : equipment) {

            if (item.getStatus() == status) {
                count++;
            }
        }

        return count;
    }

    private long countIncidentsByStatus(
            List<Incident> incidents,
            IncidentStatus status
    ) {

        long count = 0;

        for (Incident incident : incidents) {

            if (incident.getStatus() == status) {
                count++;
            }
        }

        return count;
    }

    private long countMaintenanceByResult(
            List<MaintenanceRecord> records,
            MaintenanceResult result
    ) {

        long count = 0;

        for (MaintenanceRecord record : records) {

            if (record.getResult() == result) {
                count++;
            }
        }

        return count;
    }

    private SessionUser getSessionUser(
            HttpSession session
    ) {

        if (session == null) {
            return null;
        }

        Object authenticatedUser =
                session.getAttribute(
                        SessionConstants.AUTHENTICATED_USER
                );

        if (authenticatedUser
                instanceof SessionUser) {

            return (SessionUser)
                    authenticatedUser;
        }

        return null;
    }
}