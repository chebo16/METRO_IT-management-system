package com.chebo16.metroit.service;

import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public final class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE =
            "Invalid username or password.";

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(
                userDAO,
                "UserDAO must not be null."
        );
    }

    public User authenticate(
            String username,
            String rawPassword
    ) {

        String normalizedUsername =
                normalizeUsername(username);

        validatePassword(rawPassword);

        try {
            Optional<User> userOptional =
                    userDAO.findByUsername(
                            normalizedUsername
                    );

            if (userOptional.isEmpty()) {
                throw new ValidationException(
                        INVALID_CREDENTIALS_MESSAGE
                );
            }

            User user = userOptional.get();

            boolean passwordMatches =
                    PasswordUtil.verifyPassword(
                            rawPassword,
                            user.getPasswordHash()
                    );

            if (!passwordMatches) {
                throw new ValidationException(
                        INVALID_CREDENTIALS_MESSAGE
                );
            }

            if (!user.isActive()) {
                throw new ValidationException(
                        "User account is inactive."
                );
            }

            return user;

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Authentication failed because "
                            + "the user database could not be accessed.",
                    exception
            );
        }
    }

    private String normalizeUsername(
            String username
    ) {

        if (username == null) {
            throw new ValidationException(
                    "Username must not be null."
            );
        }

        String normalizedUsername =
                username.trim();

        if (normalizedUsername.isEmpty()) {
            throw new ValidationException(
                    "Username must not be empty."
            );
        }

        if (normalizedUsername.length() > 50) {
            throw new ValidationException(
                    "Username must not exceed "
                            + "50 characters."
            );
        }

        return normalizedUsername;
    }

    private void validatePassword(
            String rawPassword
    ) {

        if (rawPassword == null) {
            throw new ValidationException(
                    "Password must not be null."
            );
        }

        if (rawPassword.isBlank()) {
            throw new ValidationException(
                    "Password must not be empty."
            );
        }
    }
}