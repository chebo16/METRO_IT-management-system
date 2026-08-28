package com.chebo16.metroit.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties properties = new Properties();

        try (InputStream inputStream =
                     DatabaseConnection.class
                             .getClassLoader()
                             .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Database configuration file was not found: "
                                + PROPERTIES_FILE
                );
            }

            properties.load(inputStream);

            String driver = getRequiredProperty(properties, "db.driver");

            URL = getRequiredProperty(properties, "db.url");
            USERNAME = getRequiredProperty(properties, "db.username");
            PASSWORD = getRequiredProperty(properties, "db.password");

            Class.forName(driver);

        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private static String getRequiredProperty(
            Properties properties,
            String propertyName
    ) {
        String value = properties.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required database property is missing: " + propertyName
            );
        }

        return value.trim();
    }
}