package com.chebo16.metroit.web.servlet.equipment;

import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.enums.EquipmentStatus;
import com.chebo16.metroit.service.EquipmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@WebServlet(
        name = "EquipmentListServlet",
        urlPatterns = "/equipment"
)
public final class EquipmentListServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String EQUIPMENT_LIST_VIEW =
            "/WEB-INF/views/equipment/list.jsp";

    private final EquipmentService equipmentService =
            new EquipmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String searchQuery =
                normalizeText(
                        request.getParameter("q")
                );

        String statusValue =
                normalizeText(
                        request.getParameter("status")
                );

        try {
            EquipmentStatus selectedStatus =
                    parseOptionalStatus(statusValue);

            List<Equipment> allEquipment =
                    equipmentService.getAllEquipment();

            List<Equipment> filteredEquipment =
                    filterEquipment(
                            allEquipment,
                            searchQuery,
                            selectedStatus
                    );

            filteredEquipment.sort(
                    Comparator.comparing(
                            Equipment::getInventoryNumber,
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

            long activeEquipment =
                    countByStatus(
                            allEquipment,
                            EquipmentStatus.ACTIVE
                    );

            long inRepairEquipment =
                    countByStatus(
                            allEquipment,
                            EquipmentStatus.IN_REPAIR
                    );

            long inactiveEquipment =
                    countByStatus(
                            allEquipment,
                            EquipmentStatus.INACTIVE
                    );

            long decommissionedEquipment =
                    countByStatus(
                            allEquipment,
                            EquipmentStatus.DECOMMISSIONED
                    );

            request.setAttribute(
                    "equipment",
                    filteredEquipment
            );

            request.setAttribute(
                    "totalEquipment",
                    allEquipment.size()
            );

            request.setAttribute(
                    "displayedEquipment",
                    filteredEquipment.size()
            );

            request.setAttribute(
                    "activeEquipment",
                    activeEquipment
            );

            request.setAttribute(
                    "inRepairEquipment",
                    inRepairEquipment
            );

            request.setAttribute(
                    "inactiveEquipment",
                    inactiveEquipment
            );

            request.setAttribute(
                    "decommissionedEquipment",
                    decommissionedEquipment
            );

            request.setAttribute(
                    "searchQuery",
                    searchQuery
            );

            request.setAttribute(
                    "selectedStatus",
                    selectedStatus == null
                            ? ""
                            : selectedStatus.name()
            );

            request.setAttribute(
                    "availableStatuses",
                    EquipmentStatus.values()
            );

            request.getRequestDispatcher(
                    EQUIPMENT_LIST_VIEW
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
                    "Unable to load the equipment list.",
                    exception
            );

            throw new ServletException(
                    "Unable to load the equipment list.",
                    exception
            );
        }
    }

    private List<Equipment> filterEquipment(
            List<Equipment> equipmentList,
            String searchQuery,
            EquipmentStatus selectedStatus
    ) {

        List<Equipment> filteredEquipment =
                new ArrayList<>();

        String normalizedQuery =
                searchQuery.toLowerCase(
                        Locale.ROOT
                );

        for (Equipment equipment
                : equipmentList) {

            boolean statusMatches =
                    selectedStatus == null
                            || equipment.getStatus()
                            == selectedStatus;

            boolean searchMatches =
                    normalizedQuery.isEmpty()
                            || matchesSearchQuery(
                            equipment,
                            normalizedQuery
                    );

            if (statusMatches
                    && searchMatches) {

                filteredEquipment.add(
                        equipment
                );
            }
        }

        return filteredEquipment;
    }

    private boolean matchesSearchQuery(
            Equipment equipment,
            String normalizedQuery
    ) {

        return containsIgnoreCase(
                equipment.getInventoryNumber(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getName(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getType(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getManufacturer(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getModel(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getSerialNumber(),
                normalizedQuery
        ) || containsIgnoreCase(
                equipment.getIpAddress(),
                normalizedQuery
        );
    }

    private boolean containsIgnoreCase(
            String value,
            String normalizedQuery
    ) {

        if (value == null
                || value.isBlank()) {

            return false;
        }

        return value.toLowerCase(
                Locale.ROOT
        ).contains(
                normalizedQuery
        );
    }

    private long countByStatus(
            List<Equipment> equipmentList,
            EquipmentStatus status
    ) {

        long count = 0;

        for (Equipment equipment
                : equipmentList) {

            if (equipment.getStatus()
                    == status) {

                count++;
            }
        }

        return count;
    }

    private EquipmentStatus parseOptionalStatus(
            String statusValue
    ) {

        if (statusValue.isEmpty()) {
            return null;
        }

        try {
            return EquipmentStatus.valueOf(
                    statusValue.toUpperCase(
                            Locale.ROOT
                    )
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected equipment status is invalid."
            );
        }
    }

    private String normalizeText(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}