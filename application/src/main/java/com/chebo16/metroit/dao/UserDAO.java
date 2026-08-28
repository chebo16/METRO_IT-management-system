package com.chebo16.metroit.dao;

import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
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

public final class UserDAO {

    private static final String SELECT_ALL_SQL = """
            SELECT
                id,
                username,
                password_hash,
                full_name,
                email,
                role,
                active,
                created_at
            FROM users
            ORDER BY id
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT
                id,
                username,
                password_hash,
                full_name,
                email,
                role,
                active,
                created_at
            FROM users
            WHERE id = ?
            """;

    private static final String SELECT_BY_USERNAME_SQL = """
            SELECT
                id,
                username,
                password_hash,
                full_name,
                email,
                role,
                active,
                created_at
            FROM users
            WHERE username = ?
            """;

    private static final String INSERT_SQL = """
            INSERT INTO users (
                username,
                password_hash,
                full_name,
                email,
                role,
                active
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE users
            SET
                username = ?,
                password_hash = ?,
                full_name = ?,
                email = ?,
                role = ?,
                active = ?
            WHERE id = ?
            """;

    private static final String SET_ACTIVE_SQL = """
            UPDATE users
            SET active = ?
            WHERE id = ?
            """;

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        }

        return users;
    }

    public Optional<User> findById(long id) throws SQLException {
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

    public Optional<User> findByUsername(String username)
            throws SQLException {

        requireText(username, "Username");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_USERNAME_SQL)) {

            statement.setString(1, username.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public long insert(User user) throws SQLException {
        validateUser(user);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_SQL,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setUserParameters(statement, user);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "User insertion failed. Affected rows: " + affectedRows
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    user.setId(generatedId);
                    return generatedId;
                }
            }

            throw new SQLException(
                    "User insertion succeeded, but no generated ID was returned."
            );
        }
    }

    public boolean update(User user) throws SQLException {
        validateUser(user);

        if (user.getId() == null) {
            throw new IllegalArgumentException(
                    "User ID is required for update."
            );
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            setUserParameters(statement, user);
            statement.setLong(7, user.getId());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean setActive(long userId, boolean active)
            throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SET_ACTIVE_SQL)) {

            statement.setBoolean(1, active);
            statement.setLong(2, userId);

            return statement.executeUpdate() == 1;
        }
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setRole(
                UserRole.valueOf(resultSet.getString("role"))
        );
        user.setActive(resultSet.getBoolean("active"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }

    private void setUserParameters(
            PreparedStatement statement,
            User user
    ) throws SQLException {

        statement.setString(1, user.getUsername().trim());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getFullName().trim());
        statement.setString(4, user.getEmail().trim());
        statement.setString(5, user.getRole().name());
        statement.setBoolean(6, user.isActive());
    }

    private void validateUser(User user) {
        Objects.requireNonNull(
                user,
                "User must not be null."
        );

        requireText(user.getUsername(), "Username");
        requireText(user.getPasswordHash(), "Password hash");
        requireText(user.getFullName(), "Full name");
        requireText(user.getEmail(), "Email");

        Objects.requireNonNull(
                user.getRole(),
                "User role must not be null."
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