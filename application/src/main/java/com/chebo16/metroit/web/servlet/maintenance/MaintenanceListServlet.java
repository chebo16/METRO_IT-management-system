package com.chebo16.metroit.web.servlet.maintenance;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.service.MaintenanceRecordService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(
        name = "MaintenanceListServlet",
        urlPatterns = "/maintenance"
)
public final class MaintenanceListServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MAINTENANCE_LIST_VIEW =
            "/WEB-INF/views/maintenance/list.jsp";

    private final MaintenanceRecordService maintenanceRecordService =
            new MaintenanceRecordService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SessionUser sessionUser = getSessionUser(session);

        if (sessionUser == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication is required."
            );
            return;
        }

        try {
            List<MaintenanceRecord> records =
                    loadRecords(sessionUser);

            String search =
                    normalizeParameter(
                            request.getParameter("search")
                    );

            String result =
                    normalizeParameter(
                            request.getParameter("result")
                    );

            List<MaintenanceRecord> filteredRecords =
                    applyFilters(
                            records,
                            search,
                            result
                    );

            long totalRecords = records.size();

            long successfulRecords = countByResult(
                    records,
                    MaintenanceResult.SUCCESS
            );

            long partiallyCompletedRecords = countByResult(
                    records,
                    MaintenanceResult.PARTIALLY_COMPLETED
            );

            long failedRecords = countByResult(
                    records,
                    MaintenanceResult.FAILED
            );

            request.setAttribute("records", filteredRecords);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute(
                    "successfulRecords",
                    successfulRecords
            );
            request.setAttribute(
                    "partiallyCompletedRecords",
                    partiallyCompletedRecords
            );
            request.setAttribute(
                    "failedRecords",
                    failedRecords
            );
            request.setAttribute("search", search);
            request.setAttribute("selectedResult", result);
            request.setAttribute(
                    "results",
                    MaintenanceResult.values()
            );

            request.getRequestDispatcher(MAINTENANCE_LIST_VIEW)
                    .forward(request, response);

        } catch (ValidationException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (ServiceException exception) {
            getServletContext().log(
                    "Unable to load maintenance history.",
                    exception
            );

            throw new ServletException(
                    "Unable to load maintenance history.",
                    exception
            );
        }
    }

    private List<MaintenanceRecord> loadRecords(
            SessionUser sessionUser
    ) {
        if (sessionUser.isAdmin()) {
            return maintenanceRecordService.getAllRecords();
        }

        return maintenanceRecordService.getRecordsByTechnician(
                sessionUser.getId()
        );
    }

    private List<MaintenanceRecord> applyFilters(
            List<MaintenanceRecord> records,
            String search,
            String result
    ) {
        List<MaintenanceRecord> filteredRecords =
                new ArrayList<>();

        for (MaintenanceRecord record : records) {
            if (!matchesSearch(record, search)) {
                continue;
            }

            if (!matchesResult(record, result)) {
                continue;
            }

            filteredRecords.add(record);
        }

        return filteredRecords;
    }

    private boolean matchesSearch(
            MaintenanceRecord record,
            String search
    ) {
        if (search.isEmpty()) {
            return true;
        }

        String normalizedSearch =
                search.toLowerCase(Locale.ROOT);

        String workDescription =
                record.getWorkDescription() == null
                        ? ""
                        : record.getWorkDescription()
                        .toLowerCase(Locale.ROOT);

        String replacedComponents =
                record.getReplacedComponents() == null
                        ? ""
                        : record.getReplacedComponents()
                        .toLowerCase(Locale.ROOT);

        String incidentId =
                record.getIncidentId() == null
                        ? ""
                        : record.getIncidentId().toString();

        String equipmentId =
                record.getEquipmentId() == null
                        ? ""
                        : record.getEquipmentId().toString();

        return workDescription.contains(normalizedSearch)
                || replacedComponents.contains(normalizedSearch)
                || incidentId.contains(normalizedSearch)
                || equipmentId.contains(normalizedSearch);
    }

    private boolean matchesResult(
            MaintenanceRecord record,
            String result
    ) {
        if (result.isEmpty()) {
            return true;
        }

        if (record.getResult() == null) {
            return false;
        }

        return record.getResult()
                .name()
                .equalsIgnoreCase(result);
    }

    private long countByResult(
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

    private SessionUser getSessionUser(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object authenticatedUser = session.getAttribute(
                SessionConstants.AUTHENTICATED_USER
        );

        if (authenticatedUser instanceof SessionUser sessionUser) {
            return sessionUser;
        }

        return null;
    }

    private String normalizeParameter(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}