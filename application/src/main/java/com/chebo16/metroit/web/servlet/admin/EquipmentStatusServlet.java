package com.chebo16.metroit.web.servlet.admin;

import com.chebo16.metroit.exception.NotFoundException;
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
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@WebServlet(
        name = "EquipmentStatusServlet",
        urlPatterns = "/admin/equipment/status"
)
public final class EquipmentStatusServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private final EquipmentService equipmentService =
            new EquipmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        /*
         * Equipment status changes must only be
         * performed through POST requests.
         */
        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Equipment status can only be changed "
                        + "using a POST request."
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        try {
            long equipmentId =
                    parseEquipmentId(
                            request.getParameter("id")
                    );

            EquipmentStatus requestedStatus =
                    parseStatus(
                            request.getParameter("status")
                    );

            Equipment equipment =
                    equipmentService.getEquipmentById(
                            equipmentId
                    );

            if (equipment.getStatus()
                    != requestedStatus) {

                equipment.setStatus(
                        requestedStatus
                );

                equipmentService.updateEquipment(
                        equipment
                );
            }

            String redirectUrl =
                    request.getContextPath()
                            + "/equipment"
                            + "?success=status-updated";

            response.sendRedirect(
                    response.encodeRedirectURL(
                            redirectUrl
                    )
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
                    "Unable to change equipment status.",
                    exception
            );

            throw new ServletException(
                    "Unable to change equipment status.",
                    exception
            );
        }
    }

    private long parseEquipmentId(
            String equipmentIdValue
    ) {

        if (equipmentIdValue == null
                || equipmentIdValue.isBlank()) {

            throw new ValidationException(
                    "Equipment ID must be provided."
            );
        }

        try {
            long equipmentId =
                    Long.parseLong(
                            equipmentIdValue.trim()
                    );

            if (equipmentId <= 0) {

                throw new ValidationException(
                        "Equipment ID must be greater "
                                + "than zero."
                );
            }

            return equipmentId;

        } catch (NumberFormatException exception) {

            throw new ValidationException(
                    "Equipment ID must be a valid number."
            );
        }
    }

    private EquipmentStatus parseStatus(
            String statusValue
    ) {

        if (statusValue == null
                || statusValue.isBlank()) {

            throw new ValidationException(
                    "Equipment status must be provided."
            );
        }

        try {
            return EquipmentStatus.valueOf(
                    statusValue
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException exception) {

            throw new ValidationException(
                    "Selected equipment status is invalid."
            );
        }
    }
}