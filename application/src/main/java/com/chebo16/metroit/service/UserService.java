package com.chebo16.metroit.service;

import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.NotFoundException;
import com.chebo16.metroit.exception.ServiceException;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+"
                            + "@[A-Za-z0-9.-]+"
                            + "\\.[A-Za-z]{2,}$"
            );

    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAO());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(
                userDAO,
                "UserDAO must not be null."
        );
    }

    public List<User> getAllUsers() {

        try {
            return userDAO.findAll();

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load users.",
                    exception
            );
        }
    }

    public User getUserById(long userId) {

        validateId(userId);

        try {
            return userDAO.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "User was not found: "
                                            + userId
                            )
                    );

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load user with ID: "
                            + userId,
                    exception
            );
        }
    }

    public User getUserByUsername(String username) {

        requireText(username, "Username");

        String normalizedUsername =
                username.trim();

        try {
            return userDAO
                    .findByUsername(normalizedUsername)
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "User was not found: "
                                            + normalizedUsername
                            )
                    );

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to load user by username: "
                            + normalizedUsername,
                    exception
            );
        }
    }

    public User createUser(User user) {

        Objects.requireNonNull(
                user,
                "User must not be null."
        );

        normalizeUser(user);
        validateUser(user);
        validateUniqueFields(user, null);

        try {
            long generatedId =
                    userDAO.insert(user);

            return userDAO.findById(generatedId)
                    .orElseThrow(() ->
                            new ServiceException(
                                    "User was created, "
                                            + "but could not be reloaded."
                            )
                    );

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to create user.",
                    exception
            );
        }
    }

    public User updateUser(User user) {

        Objects.requireNonNull(
                user,
                "User must not be null."
        );

        if (user.getId() == null) {
            throw new ValidationException(
                    "User ID is required for update."
            );
        }

        validateId(user.getId());

        // Confirms that the user exists.
        getUserById(user.getId());

        normalizeUser(user);
        validateUser(user);

        validateUniqueFields(
                user,
                user.getId()
        );

        try {
            boolean updated =
                    userDAO.update(user);

            if (!updated) {
                throw new NotFoundException(
                        "User was not found: "
                                + user.getId()
                );
            }

            return userDAO.findById(user.getId())
                    .orElseThrow(() ->
                            new ServiceException(
                                    "User was updated, "
                                            + "but could not be reloaded."
                            )
                    );

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to update user with ID: "
                            + user.getId(),
                    exception
            );
        }
    }

    public User changeActiveStatus(
            long userId,
            boolean active
    ) {

        validateId(userId);

        // Confirms that the user exists.
        getUserById(userId);

        try {
            boolean updated =
                    userDAO.setActive(
                            userId,
                            active
                    );

            if (!updated) {
                throw new NotFoundException(
                        "User was not found: "
                                + userId
                );
            }

            return userDAO.findById(userId)
                    .orElseThrow(() ->
                            new ServiceException(
                                    "User status was updated, "
                                            + "but the user could not "
                                            + "be reloaded."
                            )
                    );

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to change active status "
                            + "for user ID: "
                            + userId,
                    exception
            );
        }
    }

    public User activateUser(long userId) {

        return changeActiveStatus(
                userId,
                true
        );
    }

    public User deactivateUser(long userId) {

        return changeActiveStatus(
                userId,
                false
        );
    }

    private void validateUniqueFields(
            User user,
            Long currentUserId
    ) {

        List<User> existingUsers;

        try {
            existingUsers =
                    userDAO.findAll();

        } catch (SQLException exception) {
            throw new ServiceException(
                    "Failed to validate user uniqueness.",
                    exception
            );
        }

        for (User existingUser : existingUsers) {

            if (currentUserId != null
                    && currentUserId.equals(
                    existingUser.getId()
            )) {
                continue;
            }

            if (user.getUsername().equalsIgnoreCase(
                    existingUser.getUsername()
            )) {

                throw new ValidationException(
                        "Username already exists: "
                                + user.getUsername()
                );
            }

            if (user.getEmail().equalsIgnoreCase(
                    existingUser.getEmail()
            )) {

                throw new ValidationException(
                        "Email already exists: "
                                + user.getEmail()
                );
            }
        }
    }

    private void validateUser(User user) {

        requireText(
                user.getUsername(),
                "Username"
        );

        requireText(
                user.getPasswordHash(),
                "Password hash"
        );

        requireText(
                user.getFullName(),
                "Full name"
        );

        requireText(
                user.getEmail(),
                "Email"
        );

        Objects.requireNonNull(
                user.getRole(),
                "User role must not be null."
        );

        validateMaximumLength(
                user.getUsername(),
                50,
                "Username"
        );

        validateMaximumLength(
                user.getPasswordHash(),
                255,
                "Password hash"
        );

        validateMaximumLength(
                user.getFullName(),
                100,
                "Full name"
        );

        validateMaximumLength(
                user.getEmail(),
                150,
                "Email"
        );

        if (!EMAIL_PATTERN
                .matcher(user.getEmail())
                .matches()) {

            throw new ValidationException(
                    "Invalid email address: "
                            + user.getEmail()
            );
        }
    }

    private void normalizeUser(User user) {

        user.setUsername(
                trimRequired(
                        user.getUsername()
                )
        );

        user.setPasswordHash(
                trimRequired(
                        user.getPasswordHash()
                )
        );

        user.setFullName(
                trimRequired(
                        user.getFullName()
                )
        );

        user.setEmail(
                trimRequired(
                        user.getEmail()
                )
        );
    }

    private void validateId(long userId) {

        if (userId <= 0) {
            throw new ValidationException(
                    "User ID must be greater than zero."
            );
        }
    }

    private void requireText(
            String value,
            String fieldName
    ) {

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    fieldName + " must not be empty."
            );
        }
    }

    private void validateMaximumLength(
            String value,
            int maximumLength,
            String fieldName
    ) {

        if (value != null
                && value.length() > maximumLength) {

            throw new ValidationException(
                    fieldName
                            + " must not exceed "
                            + maximumLength
                            + " characters."
            );
        }
    }

    private String trimRequired(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }
}