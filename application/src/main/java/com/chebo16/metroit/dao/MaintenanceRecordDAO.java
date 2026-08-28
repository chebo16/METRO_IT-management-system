package com.chebo16.metroit.dao;

import com.chebo16.metroit.model.MaintenanceRecord;
import com.chebo16.metroit.model.enums.MaintenanceResult;
import com.chebo16.metroit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MaintenanceRecordDAO {

    private static final String SELECT_COLUMNS = """
            SELECT
                id,
                incident_id,
                equipment_id,
                technician_id,
                work_description,
                replaced_components,
                result,
                performed_at
            FROM maintenance_records
            """;

    private static final String SELECT_ALL_SQL =
            SELECT_COLUMNS + """
                     ORDER BY performed_at DESC, id DESC
                    """;

    private static final String SELECT_BY_ID_SQL =
            SELECT_COLUMNS + """
                     WHERE id = ?
                    """;

    private static final String SELECT_BY_INCIDENT_ID_SQL =
            SELECT_COLUMNS + """
                     WHERE incident_id = ?
                     ORDER BY performed_at DESC, id DESC
                    """;

    private static final String SELECT_BY_EQUIPMENT_ID_SQL =
            SELECT_COLUMNS + """
                     WHERE equipment_id = ?
                     ORDER BY performed_at DESC, id DESC
                    """;

    private static final String SELECT_BY_TECHNICIAN_ID_SQL =
            SELECT_COLUMNS + """
                     WHERE technician_id = ?
                     ORDER BY performed_at DESC, id DESC
                    """;

    private static final String INSERT_SQL = """
            INSERT INTO maintenance_records (
                incident_id,
                equipment_id,
                technician_id,
                work_description,
                replaced_components,
                result
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE maintenance_records
            SET
                incident_id = ?,
                equipment_id = ?,
                technician_id = ?,
                work_description = ?,
                replaced_components = ?,
                result = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM maintenance_records
            WHERE id = ?
            """;

    public List<MaintenanceRecord> findAll() throws SQLException {
        List<MaintenanceRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                records.add(mapRow(resultSet));
            }
        }

        return records;
    }

    public Optional<MaintenanceRecord> findById(long id) throws SQLException {
        validateId(id, "Maintenance record ID");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<MaintenanceRecord> findByIncidentId(long incidentId)
            throws SQLException {

        validateId(incidentId, "Incident ID");

        return findByForeignKey(
                SELECT_BY_INCIDENT_ID_SQL,
                incidentId
        );
    }

    public List<MaintenanceRecord> findByEquipmentId(long equipmentId)
            throws SQLException {

        validateId(equipmentId, "Equipment ID");

        return findByForeignKey(
                SELECT_BY_EQUIPMENT_ID_SQL,
                equipmentId
        );
    }

    public List<MaintenanceRecord> findByTechnicianId(long technicianId)
            throws SQLException {

        validateId(technicianId, "Technician ID");

        return findByForeignKey(
                SELECT_BY_TECHNICIAN_ID_SQL,
                technicianId
        );
    }

    public long insert(MaintenanceRecord record) throws SQLException {
        validateRecord(record);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setRecordParameters(statement, record);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Maintenance record insertion failed. Affected rows: "
                                + affectedRows
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    record.setId(generatedId);
                    return generatedId;
                }
            }

            throw new SQLException(
                    "Maintenance record insertion succeeded, "
                            + "but no generated ID was returned."
            );
        }
    }

    public boolean update(MaintenanceRecord record) throws SQLException {
        validateRecord(record);

        if (record.getId() == null) {
            throw new IllegalArgumentException(
                    "Maintenance record ID is required for update."
            );
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            setRecordParameters(statement, record);
            statement.setLong(7, record.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        validateId(id, "Maintenance record ID");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);

            return statement.executeUpdate() == 1;
        }
    }

    private List<MaintenanceRecord> findByForeignKey(
            String sql,
            long foreignKeyId
    ) throws SQLException {

        List<MaintenanceRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, foreignKeyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapRow(resultSet));
                }
            }
        }

        return records;
    }

    private MaintenanceRecord mapRow(ResultSet resultSet)
            throws SQLException {

        MaintenanceRecord record = new MaintenanceRecord();

        record.setId(resultSet.getLong("id"));
        record.setIncidentId(resultSet.getLong("incident_id"));
        record.setEquipmentId(resultSet.getLong("equipment_id"));
        record.setTechnicianId(resultSet.getLong("technician_id"));
        record.setWorkDescription(
                resultSet.getString("work_description")
        );
        record.setReplacedComponents(
                resultSet.getString("replaced_components")
        );
        record.setResult(
                MaintenanceResult.valueOf(resultSet.getString("result"))
        );

        Timestamp performedAt = resultSet.getTimestamp("performed_at");

        if (performedAt != null) {
            record.setPerformedAt(performedAt.toLocalDateTime());
        }

        return record;
    }

    private void setRecordParameters(
            PreparedStatement statement,
            MaintenanceRecord record
    ) throws SQLException {

        statement.setLong(1, record.getIncidentId());
        statement.setLong(2, record.getEquipmentId());
        statement.setLong(3, record.getTechnicianId());
        statement.setString(4, record.getWorkDescription().trim());
        statement.setString(5, record.getReplacedComponents());
        statement.setString(6, record.getResult().name());
    }

    private void validateRecord(MaintenanceRecord record) {
        Objects.requireNonNull(
                record,
                "Maintenance record must not be null."
        );

        validateId(record.getIncidentId(), "Incident ID");
        validateId(record.getEquipmentId(), "Equipment ID");
        validateId(record.getTechnicianId(), "Technician ID");

        requireText(
                record.getWorkDescription(),
                "Work description"
        );

        Objects.requireNonNull(
                record.getResult(),
                "Maintenance result must not be null."
        );
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be greater than zero."
            );
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}