package com.chebo16.metroit.dao;

import com.chebo16.metroit.model.Equipment;
import com.chebo16.metroit.model.enums.EquipmentStatus;
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

public final class EquipmentDAO {

    private static final String SELECT_ALL_SQL = """
            SELECT
                id,
                inventory_number,
                name,
                type,
                manufacturer,
                model,
                serial_number,
                ip_address,
                status,
                created_at,
                notes
            FROM equipment
            ORDER BY id
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                inventory_number,
                name,
                type,
                manufacturer,
                model,
                serial_number,
                ip_address,
                status,
                created_at,
                notes
            FROM equipment
            WHERE id = ?
            """;

    private static final String INSERT_SQL = """
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
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE equipment
            SET
                inventory_number = ?,
                name = ?,
                type = ?,
                manufacturer = ?,
                model = ?,
                serial_number = ?,
                ip_address = ?,
                status = ?,
                notes = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM equipment
            WHERE id = ?
            """;

    public List<Equipment> findAll() throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                equipmentList.add(mapRow(resultSet));
            }
        }

        return equipmentList;
    }

    public Optional<Equipment> findById(long id) throws SQLException {
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

    public long insert(Equipment equipment) throws SQLException {
        validateEquipment(equipment);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setEquipmentParameters(statement, equipment);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Equipment insertion failed. Affected rows: " + affectedRows
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    equipment.setId(generatedId);
                    return generatedId;
                }
            }

            throw new SQLException(
                    "Equipment insertion succeeded, but no generated ID was returned."
            );
        }
    }

    public boolean update(Equipment equipment) throws SQLException {
        validateEquipment(equipment);

        if (equipment.getId() == null) {
            throw new IllegalArgumentException(
                    "Equipment ID is required for update."
            );
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            setEquipmentParameters(statement, equipment);
            statement.setLong(10, equipment.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private Equipment mapRow(ResultSet resultSet) throws SQLException {
        Equipment equipment = new Equipment();

        equipment.setId(resultSet.getLong("id"));
        equipment.setInventoryNumber(resultSet.getString("inventory_number"));
        equipment.setName(resultSet.getString("name"));
        equipment.setType(resultSet.getString("type"));
        equipment.setManufacturer(resultSet.getString("manufacturer"));
        equipment.setModel(resultSet.getString("model"));
        equipment.setSerialNumber(resultSet.getString("serial_number"));
        equipment.setIpAddress(resultSet.getString("ip_address"));
        equipment.setStatus(
                EquipmentStatus.valueOf(resultSet.getString("status"))
        );

        Timestamp createdAt = resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            equipment.setCreatedAt(createdAt.toLocalDateTime());
        }

        equipment.setNotes(resultSet.getString("notes"));

        return equipment;
    }

    private void setEquipmentParameters(
            PreparedStatement statement,
            Equipment equipment
    ) throws SQLException {
        statement.setString(1, equipment.getInventoryNumber());
        statement.setString(2, equipment.getName());
        statement.setString(3, equipment.getType());
        statement.setString(4, equipment.getManufacturer());
        statement.setString(5, equipment.getModel());
        statement.setString(6, equipment.getSerialNumber());
        statement.setString(7, equipment.getIpAddress());
        statement.setString(8, equipment.getStatus().name());
        statement.setString(9, equipment.getNotes());
    }

    private void validateEquipment(Equipment equipment) {
        Objects.requireNonNull(
                equipment,
                "Equipment must not be null."
        );

        requireText(equipment.getInventoryNumber(), "Inventory number");
        requireText(equipment.getName(), "Equipment name");
        requireText(equipment.getType(), "Equipment type");

        Objects.requireNonNull(
                equipment.getStatus(),
                "Equipment status must not be null."
        );
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }
    }
}