package com.chebo16.metroit.web.servlet.admin;

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
        name = "EquipmentCreateServlet",
        urlPatterns = "/admin/equipment/create"
)
public final class EquipmentCreateServlet
        extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String EQUIPMENT_FORM_VIEW =
            "/WEB-INF/views/admin/equipment/form.jsp";

    private final EquipmentService equipmentService =
            new EquipmentService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        prepareCreateForm(request);

        forwardToForm(
                request,
                response
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

        String inventoryNumber =
                normalizeRequiredText(
                        request.getParameter(
                                "inventoryNumber"
                        )
                );

        String name =
                normalizeRequiredText(
                        request.getParameter(
                                "name"
                        )
                );

        String type =
                normalizeRequiredText(
                        request.getParameter(
                                "type"
                        )
                );

        String manufacturer =
                normalizeOptionalText(
                        request.getParameter(
                                "manufacturer"
                        )
                );

        String model =
                normalizeOptionalText(
                        request.getParameter(
                                "model"
                        )
                );

        String serialNumber =
                normalizeOptionalText(
                        request.getParameter(
                                "serialNumber"
                        )
                );

        String ipAddress =
                normalizeOptionalText(
                        request.getParameter(
                                "ipAddress"
                        )
                );

        String statusValue =
                normalizeRequiredText(
                        request.getParameter(
                                "status"
                        )
                );

        String notes =
                normalizeOptionalText(
                        request.getParameter(
                                "notes"
                        )
                );

        try {
            EquipmentStatus status =
                    parseStatus(statusValue);

            Equipment equipment =
                    new Equipment(
                            inventoryNumber,
                            name,
                            type,
                            manufacturer,
                            model,
                            serialNumber,
                            ipAddress,
                            notes
                    );

            equipment.setStatus(status);

            equipmentService.createEquipment(
                    equipment
            );

            response.sendRedirect(
                    response.encodeRedirectURL(
                            request.getContextPath()
                                    + "/equipment"
                                    + "?success=created"
                    )
            );

        } catch (ValidationException exception) {

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    inventoryNumber,
                    name,
                    type,
                    manufacturer,
                    model,
                    serialNumber,
                    ipAddress,
                    statusValue,
                    notes
            );

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            forwardToForm(
                    request,
                    response
            );

        } catch (IllegalArgumentException exception) {

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    inventoryNumber,
                    name,
                    type,
                    manufacturer,
                    model,
                    serialNumber,
                    ipAddress,
                    statusValue,
                    notes
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
                    "Unable to create equipment.",
                    exception
            );

            prepareCreateForm(request);

            preserveFormValues(
                    request,
                    inventoryNumber,
                    name,
                    type,
                    manufacturer,
                    model,
                    serialNumber,
                    ipAddress,
                    statusValue,
                    notes
            );

            request.setAttribute(
                    "errorMessage",
                    "The equipment could not be created. "
                            + "Please try again later."
            );

            forwardToForm(
                    request,
                    response
            );
        }
    }

    private void prepareCreateForm(
            HttpServletRequest request
    ) {

        request.setAttribute(
                "pageTitle",
                "Add equipment"
        );

        request.setAttribute(
                "formMode",
                "create"
        );

        request.setAttribute(
                "formAction",
                request.getContextPath()
                        + "/admin/equipment/create"
        );

        request.setAttribute(
                "submitLabel",
                "Create equipment"
        );

        request.setAttribute(
                "availableStatuses",
                EquipmentStatus.values()
        );

        if (request.getAttribute(
                "selectedStatus"
        ) == null) {

            request.setAttribute(
                    "selectedStatus",
                    EquipmentStatus.ACTIVE.name()
            );
        }
    }

    private void preserveFormValues(
            HttpServletRequest request,
            String inventoryNumber,
            String name,
            String type,
            String manufacturer,
            String model,
            String serialNumber,
            String ipAddress,
            String statusValue,
            String notes
    ) {

        request.setAttribute(
                "inventoryNumber",
                valueOrEmpty(inventoryNumber)
        );

        request.setAttribute(
                "name",
                valueOrEmpty(name)
        );

        request.setAttribute(
                "type",
                valueOrEmpty(type)
        );

        request.setAttribute(
                "manufacturer",
                valueOrEmpty(manufacturer)
        );

        request.setAttribute(
                "model",
                valueOrEmpty(model)
        );

        request.setAttribute(
                "serialNumber",
                valueOrEmpty(serialNumber)
        );

        request.setAttribute(
                "ipAddress",
                valueOrEmpty(ipAddress)
        );

        request.setAttribute(
                "notes",
                valueOrEmpty(notes)
        );

        if (statusValue == null
                || statusValue.isBlank()) {

            request.setAttribute(
                    "selectedStatus",
                    EquipmentStatus.ACTIVE.name()
            );

        } else {

            request.setAttribute(
                    "selectedStatus",
                    statusValue
            );
        }
    }

    private EquipmentStatus parseStatus(
            String statusValue
    ) {

        if (statusValue == null
                || statusValue.isBlank()) {

            return EquipmentStatus.ACTIVE;
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

    private String normalizeRequiredText(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String normalizeOptionalText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalizedValue =
                value.trim();

        if (normalizedValue.isEmpty()) {
            return null;
        }

        return normalizedValue;
    }

    private String valueOrEmpty(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value;
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(
                EQUIPMENT_FORM_VIEW
        ).forward(
                request,
                response
        );
    }
}