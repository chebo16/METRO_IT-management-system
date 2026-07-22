-- Sample data
-- Run this script after database/schema.sql
-- The script is intended for an empty database.
-- Password hashes are temporary placeholders.

USE metro_it;

START TRANSACTION;

-- users

INSERT INTO users (
    username,
    password_hash,
    full_name,
    email,
    role,
    active
)
VALUES
(
    'admin',
    'TEMPORARY_HASH_ADMIN',
    'System Administrator',
    'admin@example.com',
    'ADMIN',
    TRUE
),
(
    'technician1',
    'TEMPORARY_HASH_TECHNICIAN1',
    'Technician One',
    'technician1@example.com',
    'TECHNICIAN',
    TRUE
),
(
    'technician2',
    'TEMPORARY_HASH_TECHNICIAN2',
    'Technician Two',
    'technician2@example.com',
    'TECHNICIAN',
    TRUE
);

-- equipment

INSERT INTO equipment (
    inventory_number,
    name,
    type,
    manufacturer,
    model,
    serial_number,
    ip_address,
    status,
    notes
)
VALUES
(
    'EQ-001',
    'Main Network Switch',
    'Switch',
    'Cisco',
    'Catalyst 2960',
    'TEST-SW-001',
    '192.0.2.10',
    'ACTIVE',
    'Main network switch.'
),
(
    'EQ-002',
    'Network Router',
    'Router',
    'TP-Link',
    'Archer AX55',
    'TEST-RT-002',
    '192.0.2.1',
    'ACTIVE',
    'Main network router.'
),
(
    'EQ-003',
    'Self-Checkout Station 1',
    'Self-Checkout',
    'NCR',
    'SCO-100',
    'TEST-SCO-003',
    '192.0.2.21',
    'ACTIVE',
    'Self-checkout station number 1.'
),
(
    'EQ-004',
    'Self-Checkout Station 2',
    'Self-Checkout',
    'NCR',
    'SCO-100',
    'TEST-SCO-004',
    '192.0.2.22',
    'ACTIVE',
    'Self-checkout station number 2.'
),
(
    'EQ-005',
    'Self-Checkout Station 3',
    'Self-Checkout',
    'NCR',
    'SCO-100',
    'TEST-SCO-005',
    '192.0.2.23',
    'ACTIVE',
    'Self-checkout station number 3.'
),
(
    'EQ-006',
    'Office Printer 1',
    'Printer',
    'HP',
    'LaserJet Pro M404',
    'TEST-PR-006',
    '192.0.2.31',
    'ACTIVE',
    'Office printer number 1.'
),
(
    'EQ-007',
    'Office Printer 2',
    'Printer',
    'HP',
    'LaserJet Pro M404',
    'TEST-PR-007',
    '192.0.2.32',
    'ACTIVE',
    'Office printer number 2.'
),
(
    'EQ-008',
    'Barcode Scanner 1',
    'Scanner',
    'Zebra',
    'DS2208',
    'TEST-SC-008',
    NULL,
    'ACTIVE',
    'Barcode scanner number 1.'
),
(
    'EQ-009',
    'Barcode Scanner 2',
    'Scanner',
    'Zebra',
    'DS2208',
    'TEST-SC-009',
    NULL,
    'ACTIVE',
    'Barcode scanner number 2.'
),
(
    'EQ-010',
    'Network Terminal',
    'Network Terminal',
    'Dell',
    'OptiPlex 3080',
    'TEST-NT-010',
    '192.0.2.40',
    'ACTIVE',
    'Network terminal.'
),
(
    'EQ-011',
    'Monitor 1',
    'Monitor',
    'Samsung',
    'S24R350',
    'TEST-MN-011',
    NULL,
    'ACTIVE',
    'Monitor number 1.'
),
(
    'EQ-012',
    'Monitor 2',
    'Monitor',
    'Samsung',
    'S24R350',
    'TEST-MN-012',
    NULL,
    'ACTIVE',
    'Monitor number 2.'
),
(
    'EQ-013',
    'Phone',
    'Phone',
    'Yealink',
    'SIP-T31P',
    'TEST-PH-013',
    '192.0.2.50',
    'ACTIVE',
    'IP phone.'
);

-- incidents

INSERT INTO incidents (
    title,
    description,
    priority,
    status,
    equipment_id,
    created_by,
    assigned_technician,
    created_at,
    started_at,
    resolved_at,
    closed_at,
    solution_description
)
VALUES

-- Main Network Switch
(
    'Main network switch is not responding',
    'The main network switch stopped responding and network connectivity was interrupted.',
    'CRITICAL',
    'CLOSED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-001'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-15 08:30:00',
    '2026-07-15 08:40:00',
    '2026-07-15 09:30:00',
    '2026-07-15 09:45:00',
    'The damaged uplink Ethernet cable was replaced and the switch was restarted.'
),

-- Network Router
(
    'Network router periodically loses connection',
    'The network router periodically loses connection and interrupts access to network services.',
    'CRITICAL',
    'IN_PROGRESS',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-002'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-22 09:00:00',
    '2026-07-22 09:10:00',
    NULL,
    NULL,
    NULL
),

-- Self-Checkout Station 1
(
    'Self-checkout station 1 cannot connect to the network',
    'Self-checkout station 1 cannot communicate with the internal network.',
    'HIGH',
    'RESOLVED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-003'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-16 10:00:00',
    '2026-07-16 10:15:00',
    '2026-07-16 11:00:00',
    NULL,
    'The IP configuration was corrected and network connectivity was successfully restored.'
),

-- Self-Checkout Station 2
(
    'Self-checkout station 2 does not start correctly',
    'Self-checkout station 2 powers on but does not complete the startup process.',
    'HIGH',
    'CLOSED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-004'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-16 12:00:00',
    '2026-07-16 12:10:00',
    '2026-07-16 13:00:00',
    '2026-07-16 13:15:00',
    'The power and data cables were reconnected and the station was restarted.'
),

-- Self-Checkout Station 3
(
    'Self-checkout station 3 does not print receipts',
    'Self-checkout station 3 completes transactions but does not print customer receipts.',
    'HIGH',
    'NEW',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-005'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-20 08:30:00',
    NULL,
    NULL,
    NULL,
    NULL
),

-- Office Printer 1
(
    'Office printer 1 does not print',
    'Office printer 1 receives print jobs but does not print the documents.',
    'MEDIUM',
    'IN_PROGRESS',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-006'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-21 09:00:00',
    '2026-07-21 09:20:00',
    NULL,
    NULL,
    NULL
),

-- Office Printer 2
(
    'Office printer 2 reports a paper jam',
    'Office printer 2 displays a paper jam error and cannot complete printing.',
    'MEDIUM',
    'CLOSED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-007'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-17 11:00:00',
    '2026-07-17 11:10:00',
    '2026-07-17 11:45:00',
    '2026-07-17 12:00:00',
    'The jammed paper was removed, the rollers were cleaned and printing was tested.'
),

-- Barcode Scanner 1
(
    'Barcode scanner 1 does not read barcodes',
    'Barcode scanner 1 is powered on but does not correctly recognize barcodes.',
    'MEDIUM',
    'RESOLVED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-008'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-18 09:00:00',
    '2026-07-18 09:10:00',
    '2026-07-18 09:40:00',
    NULL,
    'The scanner lens was cleaned and the device was recalibrated.'
),

-- Barcode Scanner 2
(
    'Barcode scanner 2 disconnects intermittently',
    'Barcode scanner 2 intermittently disconnects during operation.',
    'MEDIUM',
    'NEW',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-009'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-20 14:00:00',
    NULL,
    NULL,
    NULL,
    NULL
),

-- Network Terminal
(
    'Network terminal cannot access the internal system',
    'The network terminal is connected to the network but cannot access the internal system.',
    'MEDIUM',
    'IN_PROGRESS',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-010'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-21 13:00:00',
    '2026-07-21 13:15:00',
    NULL,
    NULL,
    NULL
),

-- Monitor 1
(
    'Monitor 1 does not display an image',
    'Monitor 1 powers on but does not display an image from the connected computer.',
    'LOW',
    'CLOSED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-011'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-18 13:00:00',
    '2026-07-18 13:10:00',
    '2026-07-18 13:35:00',
    '2026-07-18 13:45:00',
    'The HDMI cable was replaced and the monitor was successfully tested.'
),

-- Monitor 2
(
    'Monitor 2 flickers during operation',
    'Monitor 2 periodically flickers while displaying an image.',
    'LOW',
    'RESOLVED',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-012'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    '2026-07-19 10:00:00',
    '2026-07-19 10:15:00',
    '2026-07-19 10:50:00',
    NULL,
    'The power cable was reconnected and the display settings were adjusted.'
),

-- Phone
(
    'Phone has no network connection',
    'The phone is powered on but cannot connect to the network.',
    'LOW',
    'NEW',
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-013'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'admin'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    '2026-07-22 10:00:00',
    NULL,
    NULL,
    NULL,
    NULL
);

-- maintenance_records

INSERT INTO maintenance_records (
    incident_id,
    equipment_id,
    technician_id,
    work_description,
    replaced_components,
    result,
    performed_at
)
VALUES

-- Main Network Switch: diagnosis
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-001'
          AND i.title = 'Main network switch is not responding'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-001'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Checked the switch power supply, uplink ports and Ethernet cable connections.',
    NULL,
    'PARTIALLY_COMPLETED',
    '2026-07-15 08:55:00'
),

-- Main Network Switch: repair
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-001'
          AND i.title = 'Main network switch is not responding'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-001'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Replaced the damaged uplink Ethernet cable, restarted the switch and tested network connectivity.',
    'CAT6 Ethernet cable',
    'SUCCESS',
    '2026-07-15 09:25:00'
),

-- Network Router
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-002'
          AND i.title = 'Network router periodically loses connection'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-002'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    'Checked the router configuration, network cables and connection stability. Additional monitoring is required.',
    NULL,
    'PARTIALLY_COMPLETED',
    '2026-07-22 09:40:00'
),

-- Self-Checkout Station 1
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-003'
          AND i.title = 'Self-checkout station 1 cannot connect to the network'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-003'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Checked the network connection, corrected the IP configuration and tested access to the internal network.',
    NULL,
    'SUCCESS',
    '2026-07-16 10:50:00'
),

-- Self-Checkout Station 2
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-004'
          AND i.title = 'Self-checkout station 2 does not start correctly'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-004'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    'Checked and reconnected the power and data cables, restarted the station and tested the startup process.',
    NULL,
    'SUCCESS',
    '2026-07-16 12:50:00'
),

-- Office Printer 1
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-006'
          AND i.title = 'Office printer 1 does not print'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-006'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Checked the print queue, restarted the printer and cleaned the printing mechanism. Further testing is required.',
    NULL,
    'PARTIALLY_COMPLETED',
    '2026-07-21 10:00:00'
),

-- Office Printer 2
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-007'
          AND i.title = 'Office printer 2 reports a paper jam'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-007'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    'Removed the jammed paper, cleaned the paper-feed rollers and successfully tested the printing process.',
    NULL,
    'SUCCESS',
    '2026-07-17 11:40:00'
),

-- Barcode Scanner 1
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-008'
          AND i.title = 'Barcode scanner 1 does not read barcodes'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-008'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Cleaned the scanner lens, recalibrated the device and successfully tested barcode recognition.',
    NULL,
    'SUCCESS',
    '2026-07-18 09:35:00'
),

-- Network Terminal
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-010'
          AND i.title = 'Network terminal cannot access the internal system'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-010'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Checked the physical network connection, IP configuration and access parameters. The investigation is still in progress.',
    NULL,
    'PARTIALLY_COMPLETED',
    '2026-07-21 13:45:00'
),

-- Monitor 1
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-011'
          AND i.title = 'Monitor 1 does not display an image'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-011'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician2'
    ),
    'Checked the video output, replaced the damaged HDMI cable and successfully tested the monitor.',
    'HDMI cable',
    'SUCCESS',
    '2026-07-18 13:30:00'
),

-- Monitor 2
(
    (
        SELECT i.id
        FROM incidents i
        JOIN equipment e
            ON e.id = i.equipment_id
        WHERE e.inventory_number = 'EQ-012'
          AND i.title = 'Monitor 2 flickers during operation'
        LIMIT 1
    ),
    (
        SELECT id
        FROM equipment
        WHERE inventory_number = 'EQ-012'
    ),
    (
        SELECT id
        FROM users
        WHERE username = 'technician1'
    ),
    'Reconnected the power cable, checked the video connection and adjusted the display settings.',
    NULL,
    'SUCCESS',
    '2026-07-19 10:45:00'
);

COMMIT;
