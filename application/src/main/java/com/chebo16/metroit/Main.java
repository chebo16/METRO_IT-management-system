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

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        // User model test

        User admin = new User(
                "admin",
                "TEMPORARY_HASH_ADMIN",
                "System Administrator",
                "admin@example.com",
                UserRole.ADMIN
        );

        admin.setId(1L);
        admin.setCreatedAt(LocalDateTime.now());

        // Equipment model test

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

        // Incident model test

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

        // Verification of default values

        if (!admin.isActive()) {
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

        // Simulating the start of work on the incident
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        incident.setStartedAt(LocalDateTime.now());

        // MaintenanceRecord model test

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

        // Output

        System.out.println("User:");
        System.out.println(admin);

        System.out.println("\nEquipment:");
        System.out.println(equipment);

        System.out.println("\nIncident:");
        System.out.println(incident);

        System.out.println("\nMaintenanceRecord:");
        System.out.println(maintenanceRecord);

        System.out.println(
                "\nAll Java models and enums work correctly."
        );
    }
}
