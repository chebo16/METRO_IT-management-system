package com.chebo16.metroit;

import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.EquipmentStatus;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        User admin = new User(
                "admin",
                "TEMPORARY_HASH_ADMIN",
                "System Administrator",
                "admin@example.com",
                UserRole.ADMIN
        );

        admin.setId(1L);
        admin.setCreatedAt(LocalDateTime.now());

        Equipment equipment = new Equipment(
                "EQ-001",
                "Main Network Switch",
                "Switch",
                "Cisco",
                "Catalyst 2960",
                "TEST-SW-001",
                "192.0.2.10",
                "Main network switch."
        );

        equipment.setId(1L);
        equipment.setCreatedAt(LocalDateTime.now());

        Incident incident = new Incident(
                "Main network switch is not responding",
                "The main network switch stopped responding.",
                IncidentPriority.CRITICAL,
                equipment.getId(),
                admin.getId(),
                2L
        );

        incident.setId(1L);
        incident.setCreatedAt(LocalDateTime.now());

        validateDefaults(admin, equipment, incident);

        incident.setStatus(IncidentStatus.IN_PROGRESS);
        incident.setStartedAt(LocalDateTime.now());

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord(
                incident.getId(),
                equipment.getId(),
                2L,
                "Replaced the damaged Ethernet cable and tested the connection.",
                "CAT6 Ethernet cable",
                MaintenanceResult.SUCCESS
        );

        maintenanceRecord.setId(1L);
        maintenanceRecord.setPerformedAt(LocalDateTime.now());

        printModels(
                admin,
                equipment,
                incident,
                maintenanceRecord
        );

        testDatabaseConnection();
    }

    private static void validateDefaults(
            User user,
            Equipment equipment,
            Incident incident
    ) {
        if (!user.isActive()) {
            throw new IllegalStateException(
                    "New user must be active by default."
            );
        }

        if (equipment.getStatus() != EquipmentStatus.ACTIVE) {
            throw new IllegalStateException(
                    "New equipment must have ACTIVE status."
            );
        }

        if (incident.getStatus() != IncidentStatus.NEW) {
            throw new IllegalStateException(
                    "New incident must have NEW status."
            );
        }
    }

    private static void printModels(
            User user,
            Equipment equipment,
            Incident incident,
            MaintenanceRecord maintenanceRecord
    ) {
        System.out.println("User:");
        System.out.println(user);

        System.out.println("\nEquipment:");
        System.out.println(equipment);

        System.out.println("\nIncident:");
        System.out.println(incident);

        System.out.println("\nMaintenance record:");
        System.out.println(maintenanceRecord);

        System.out.println(
                "\nModel validation completed successfully."
        );
    }

    private static void testDatabaseConnection() {
        System.out.println();
        System.out.println("Testing connection to MySQL...");

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            System.out.println("Database connection successful.");
            System.out.println(
                    "Connected database: " + connection.getCatalog()
            );
            System.out.println(
                    "Connection valid: " + connection.isValid(2)
            );

        } catch (SQLException exception) {
            System.err.println("Database connection failed.");
            System.err.println(
                    "SQL error code: " + exception.getErrorCode()
            );
            System.err.println(
                    "SQL state: " + exception.getSQLState()
            );
            System.err.println(
                    "Reason: " + exception.getMessage()
            );

        } catch (ExceptionInInitializerError error) {
            System.err.println(
                    "Database configuration initialization failed."
            );

            Throwable cause = error.getCause();

            System.err.println(
                    "Reason: "
                            + (cause != null
                            ? cause.getMessage()
                            : error.getMessage())
            );
        }
    }
}