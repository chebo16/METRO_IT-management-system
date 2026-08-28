package com.chebo16.metroit;

import com.chebo16.metroit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSelectTest {

    private static final String SELECT_USERS_SQL = """
            SELECT
                id,
                username,
                role,
                active
            FROM users
            ORDER BY id
            """;

    public static void main(String[] args) {
        System.out.println(
                "Testing SELECT query from the users table..."
        );
        System.out.println();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                SELECT_USERS_SQL
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            int userCount = 0;

            while (resultSet.next()) {
                long id =
                        resultSet.getLong("id");

                String username =
                        resultSet.getString("username");

                String role =
                        resultSet.getString("role");

                boolean active =
                        resultSet.getBoolean("active");

                System.out.println(
                        "User #"
                                + id
                                + " | "
                                + username
                                + " | "
                                + role
                                + " | active="
                                + active
                );

                userCount++;
            }

            System.out.println();
            System.out.println(
                    "SELECT query completed successfully."
            );
            System.out.println(
                    "Users found: " + userCount
            );

        } catch (SQLException exception) {
            System.err.println(
                    "SELECT query failed."
            );
            System.err.println(
                    "SQL error code: "
                            + exception.getErrorCode()
            );
            System.err.println(
                    "SQL state: "
                            + exception.getSQLState()
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw new IllegalStateException(
                    "Database SELECT test failed.",
                    exception
            );
        }
    }
}