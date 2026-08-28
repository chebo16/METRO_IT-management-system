package com.chebo16.metroit.dao;

import com.chebo16.metroit.model.Incident;
import com.chebo16.metroit.model.enums.IncidentPriority;
import com.chebo16.metroit.model.enums.IncidentStatus;
import com.chebo16.metroit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class IncidentDAO {

    private static final String SELECT_COLUMNS = """
            SELECT
                id,
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
            FROM incidents
            """;

    private static final String SELECT_ALL_SQL =
            SELECT_COLUMNS + " ORDER BY id";

    private static final String SELECT_BY_ID_SQL =
            SELECT_COLUMNS + " WHERE id = ?";

    private static final String SELECT_BY_STATUS_SQL =
            SELECT_COLUMNS + """
                     WHERE status = ?
                     ORDER BY created_at DESC, id DESC
                    """;

    private static final String SELECT_BY_TECHNICIAN_SQL =
            SELECT_COLUMNS + """
                     WHERE assigned_technician = ?
                     ORDER BY created_at DESC, id DESC
                    """;

    private static final String INSERT_SQL = """
            INSERT INTO incidents (
                title,
                description,
                priority,
                status,
                equipment_id,
                created_by,
                assigned_technician,
                started_at,
                resolved_at,
                closed_at,
                solution_description
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE incidents
            SET
                title = ?,
                description = ?,
                priority = ?,
                status = ?,
                equipment_id = ?,
                created_by = ?,
                assigned_technician = ?,
                started_at = ?,
                resolved_at = ?,
                closed_at = ?,
                solution_description = ?
            WHERE id = ?
            """;

    private static final String ASSIGN_TECHNICIAN_SQL = """
            UPDATE incidents
            SET assigned_technician = ?
            WHERE id = ?
            """;

    private static final String UPDATE_STATUS_SQL = """
            UPDATE incidents
            SET
                status = ?,
                started_at = ?,
                resolved_at = ?,
                closed_at = ?,
                solution_description = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM incidents
            WHERE id = ?
            """;

    public List<Incident> findAll() throws SQLException {
        List<Incident> incidents = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                incidents.add(mapRow(resultSet));
            }
        }

        return incidents;
    }

    public Optional<Incident> findById(long id) throws SQLException {
        validateId(id, "Incident ID");

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

    public List<Incident> findByStatus(IncidentStatus status) throws SQLException {
        Objects.requireNonNull(
                status,
                "Incident status must not be null."
        );

        List<Incident> incidents = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_STATUS_SQL)) {

            statement.setString(1, status.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    incidents.add(mapRow(resultSet));
                }
            }
        }

        return incidents;
    }

    public List<Incident> findByTechnicianId(long technicianId) throws SQLException {
        validateId(technicianId, "Technician ID");

        List<Incident> incidents = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_TECHNICIAN_SQL)) {

            statement.setLong(1, technicianId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    incidents.add(mapRow(resultSet));
                }
            }
        }

        return incidents;
    }

    public long insert(Incident incident) throws SQLException {
        validateIncident(incident);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setIncidentParameters(statement, incident);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Incident insertion failed. Affected rows: " + affectedRows
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    incident.setId(generatedId);
                    return generatedId;
                }
            }

            throw new SQLException(
                    "Incident insertion succeeded, but no generated ID was returned."
            );
        }
    }

    public boolean update(Incident incident) throws SQLException {
        validateIncident(incident);

        if (incident.getId() == null) {
            throw new IllegalArgumentException(
                    "Incident ID is required for update."
            );
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            setIncidentParameters(statement, incident);
            statement.setLong(12, incident.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean assignTechnician(
            long incidentId,
            Long technicianId
    ) throws SQLException {
        validateId(incidentId, "Incident ID");

        if (technicianId != null) {
            validateId(technicianId, "Technician ID");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(ASSIGN_TECHNICIAN_SQL)) {

            setNullableLong(statement, 1, technicianId);
            statement.setLong(2, incidentId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateStatus(
            long incidentId,
            IncidentStatus newStatus,
            String solutionDescription
    ) throws SQLException {
        validateId(incidentId, "Incident ID");

        Objects.requireNonNull(
                newStatus,
                "Incident status must not be null."
        );

        Incident incident = findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Incident was not found: " + incidentId
                ));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startedAt = incident.getStartedAt();
        LocalDateTime resolvedAt = incident.getResolvedAt();
        LocalDateTime closedAt = incident.getClosedAt();
        String finalSolutionDescription = solutionDescription;

        switch (newStatus) {
            case NEW -> {
                startedAt = null;
                resolvedAt = null;
                closedAt = null;
                finalSolutionDescription = null;
            }

            case IN_PROGRESS -> {
                if (startedAt == null) {
                    startedAt = now;
                }

                resolvedAt = null;
                closedAt = null;
                finalSolutionDescription = null;
            }

            case RESOLVED -> {
                requireText(
                        solutionDescription,
                        "Solution description"
                );

                if (startedAt == null) {
                    startedAt = now;
                }

                resolvedAt = now;
                closedAt = null;
            }

            case CLOSED -> {
                requireText(
                        solutionDescription,
                        "Solution description"
                );

                if (startedAt == null) {
                    startedAt = now;
                }

                if (resolvedAt == null) {
                    resolvedAt = now;
                }

                closedAt = now;
            }
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS_SQL)) {

            statement.setString(1, newStatus.name());
            setNullableDateTime(statement, 2, startedAt);
            setNullableDateTime(statement, 3, resolvedAt);
            setNullableDateTime(statement, 4, closedAt);
            statement.setString(5, finalSolutionDescription);
            statement.setLong(6, incidentId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(long incidentId) throws SQLException {
        validateId(incidentId, "Incident ID");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, incidentId);

            return statement.executeUpdate() == 1;
        }
    }

    private Incident mapRow(ResultSet resultSet) throws SQLException {
        Incident incident = new Incident();

        incident.setId(resultSet.getLong("id"));
        incident.setTitle(resultSet.getString("title"));
        incident.setDescription(resultSet.getString("description"));
        incident.setPriority(
                IncidentPriority.valueOf(resultSet.getString("priority"))
        );
        incident.setStatus(
                IncidentStatus.valueOf(resultSet.getString("status"))
        );
        incident.setEquipmentId(resultSet.getLong("equipment_id"));
        incident.setCreatedById(resultSet.getLong("created_by"));
        incident.setAssignedTechnicianId(
                resultSet.getObject("assigned_technician", Long.class)
        );
        incident.setCreatedAt(
                getNullableDateTime(resultSet, "created_at")
        );
        incident.setStartedAt(
                getNullableDateTime(resultSet, "started_at")
        );
        incident.setResolvedAt(
                getNullableDateTime(resultSet, "resolved_at")
        );
        incident.setClosedAt(
                getNullableDateTime(resultSet, "closed_at")
        );
        incident.setSolutionDescription(
                resultSet.getString("solution_description")
        );

        return incident;
    }

    private void setIncidentParameters(
            PreparedStatement statement,
            Incident incident
    ) throws SQLException {
        statement.setString(1, incident.getTitle().trim());
        statement.setString(2, incident.getDescription().trim());
        statement.setString(3, incident.getPriority().name());
        statement.setString(4, incident.getStatus().name());
        statement.setLong(5, incident.getEquipmentId());
        statement.setLong(6, incident.getCreatedById());
        setNullableLong(statement, 7, incident.getAssignedTechnicianId());
        setNullableDateTime(statement, 8, incident.getStartedAt());
        setNullableDateTime(statement, 9, incident.getResolvedAt());
        setNullableDateTime(statement, 10, incident.getClosedAt());
        statement.setString(11, incident.getSolutionDescription());
    }

    private void validateIncident(Incident incident) {
        Objects.requireNonNull(
                incident,
                "Incident must not be null."
        );

        requireText(incident.getTitle(), "Incident title");
        requireText(incident.getDescription(), "Incident description");

        Objects.requireNonNull(
                incident.getPriority(),
                "Incident priority must not be null."
        );

        Objects.requireNonNull(
                incident.getStatus(),
                "Incident status must not be null."
        );

        validateId(incident.getEquipmentId(), "Equipment ID");
        validateId(incident.getCreatedById(), "Created-by user ID");

        if (incident.getAssignedTechnicianId() != null) {
            validateId(
                    incident.getAssignedTechnicianId(),
                    "Assigned technician ID"
            );
        }
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

    private void setNullableLong(
            PreparedStatement statement,
            int parameterIndex,
            Long value
    ) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.BIGINT);
        } else {
            statement.setLong(parameterIndex, value);
        }
    }

    private void setNullableDateTime(
            PreparedStatement statement,
            int parameterIndex,
            LocalDateTime value
    ) throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(
                    parameterIndex,
                    Timestamp.valueOf(value)
            );
        }
    }

    private LocalDateTime getNullableDateTime(
            ResultSet resultSet,
            String columnName
    ) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);

        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}