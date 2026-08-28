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
        name = "EquipmentEditServlet",
        urlPatterns = "/admin/equipment/edit"
)
public final class EquipmentEditServlet extends HttpServlet {

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

        try {
            long equipmentId =
                    parseEquipmentId(request.getParameter("id"));

            Equipment equipment =
                    equipmentService.getEquipmentById(equipmentId);

            prepareEditForm(request, equipment);
            forwardToForm(request, response);

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
                    "Unable to load equipment.",
                    exception
            );

            throw new ServletException(
                    "Unable to load equipment.",
                    exception
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String equipmentIdValue =
                request.getParameter("id");

        String inventoryNumber =
                normalizeRequiredText(
                        request.getParameter("inventoryNumber")
                );

        String name =
                normalizeRequiredText(
                        request.getParameter("name")
                );

        String type =
                normalizeRequiredText(
                        request.getParameter("type")
                );

        String manufacturer =
                normalizeOptionalText(
                        request.getParameter("manufacturer")
                );

        String model =
                normalizeOptionalText(
                        request.getParameter("model")
                );

        String serialNumber =
                normalizeOptionalText(
                        request.getParameter("serialNumber")
                );

        String ipAddress =
                normalizeOptionalText(
                        request.getParameter("ipAddress")
                );

        String statusValue =
                normalizeRequiredText(
                        request.getParameter("status")
                );

        String notes =
                normalizeOptionalText(
                        request.getParameter("notes")
                );

        try {
            long equipmentId =
                    parseEquipmentId(equipmentIdValue);

            EquipmentStatus status =
                    parseStatus(statusValue);

            Equipment existingEquipment =
                    equipmentService.getEquipmentById(equipmentId);

            existingEquipment.setInventoryNumber(inventoryNumber);
            existingEquipment.setName(name);
            existingEquipment.setType(type);
            existingEquipment.setManufacturer(manufacturer);
            existingEquipment.setModel(model);
            existingEquipment.setSerialNumber(serialNumber);
            existingEquipment.setIpAddress(ipAddress);
            existingEquipment.setStatus(status);
            existingEquipment.setNotes(notes);

            equipmentService.updateEquipment(existingEquipment);

            response.sendRedirect(
                    response.encodeRedirectURL(
                            request.getContextPath()
                                    + "/equipment"
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
                    equipmentIdValue,
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

            forwardToForm(request, response);

        } catch (ServiceException exception) {
            getServletContext().log(
                    "Unable to update equipment.",
                    exception
            );

            prepareEditForm(
                    request,
                    equipmentIdValue,
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
                    "The equipment could not be updated. "
                            + "Please try again later."
            );

            forwardToForm(request, response);
        }
    }

    private void prepareEditForm(
            HttpServletRequest request,
            Equipment equipment
    ) {
        prepareEditForm(
                request,
                String.valueOf(equipment.getId()),
                equipment.getInventoryNumber(),
                equipment.getName(),
                equipment.getType(),
                equipment.getManufacturer(),
                equipment.getModel(),
                equipment.getSerialNumber(),
                equipment.getIpAddress(),
                equipment.getStatus().name(),
                equipment.getNotes()
        );
    }

    private void prepareEditForm(
            HttpServletRequest request,
            String equipmentId,
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
        request.setAttribute("pageTitle", "Edit equipment");
        request.setAttribute("formMode", "edit");

        request.setAttribute(
                "formAction",
                request.getContextPath()
                        + "/admin/equipment/edit"
        );

        request.setAttribute(
                "submitLabel",
                "Save changes"
        );

        request.setAttribute(
                "availableStatuses",
                EquipmentStatus.values()
        );

        request.setAttribute(
                "equipmentId",
                valueOrEmpty(equipmentId)
        );

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

        if (statusValue == null || statusValue.isBlank()) {
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

    private long parseEquipmentId(String equipmentIdValue) {
        if (equipmentIdValue == null
                || equipmentIdValue.isBlank()) {

            throw new ValidationException(
                    "Equipment ID must be provided."
            );
        }

        try {
            long equipmentId =
                    Long.parseLong(equipmentIdValue.trim());

            if (equipmentId <= 0) {
                throw new ValidationException(
                        "Equipment ID must be greater than zero."
                );
            }

            return equipmentId;

        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "Equipment ID must be a valid number."
            );
        }
    }

    private EquipmentStatus parseStatus(String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            throw new ValidationException(
                    "Equipment status must be selected."
            );
        }

        try {
            return EquipmentStatus.valueOf(
                    statusValue.trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(
                    "Selected equipment status is invalid."
            );
        }
    }

    private String normalizeRequiredText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();

        if (normalizedValue.isEmpty()) {
            return null;
        }

        return normalizedValue;
    }

    private String valueOrEmpty(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(EQUIPMENT_FORM_VIEW)
                .forward(request, response);
    }
}