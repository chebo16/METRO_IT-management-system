CREATE DATABASE IF NOT EXISTS metro_it
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE metro_it;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role ENUM(
        'ADMIN',
        'TECHNICIAN'
    ) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    inventory_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    serial_number VARCHAR(100) UNIQUE,
    ip_address VARCHAR(45),
    status ENUM(
        'ACTIVE',
        'IN_REPAIR',
        'INACTIVE',
        'DECOMMISSIONED'
    ) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    INDEX idx_equipment_type (type),
    INDEX idx_equipment_status (status)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS incidents (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM(
        'LOW',
        'MEDIUM',
        'HIGH',
        'CRITICAL'
    ) NOT NULL DEFAULT 'MEDIUM',
    status ENUM(
        'NEW',
        'IN_PROGRESS',
        'RESOLVED',
        'CLOSED'
    ) NOT NULL DEFAULT 'NEW',
    equipment_id BIGINT UNSIGNED NOT NULL,
    created_by BIGINT UNSIGNED NOT NULL,
    assigned_technician BIGINT UNSIGNED,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    resolved_at DATETIME,
    closed_at DATETIME,
    solution_description TEXT,

    CONSTRAINT fk_incident_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_incident_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_incident_technician
        FOREIGN KEY (assigned_technician)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_incident_status (status),
    INDEX idx_incident_priority (priority),
    INDEX idx_incident_equipment (equipment_id),
    INDEX idx_incident_technician (assigned_technician),
    INDEX idx_incident_created_at (created_at)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS maintenance_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT UNSIGNED NOT NULL,
    equipment_id BIGINT UNSIGNED NOT NULL,
    technician_id BIGINT UNSIGNED NOT NULL,
    work_description TEXT NOT NULL,
    replaced_components TEXT,
    result ENUM(
        'SUCCESS',
        'PARTIALLY_COMPLETED',
        'FAILED'
    ) NOT NULL,
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_incident
        FOREIGN KEY (incident_id)
        REFERENCES incidents(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_maintenance_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipment(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_maintenance_technician
        FOREIGN KEY (technician_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_maintenance_incident (incident_id),
    INDEX idx_maintenance_equipment (equipment_id),
    INDEX idx_maintenance_technician (technician_id),
    INDEX idx_maintenance_result (result),
    INDEX idx_maintenance_performed_at (performed_at)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;
