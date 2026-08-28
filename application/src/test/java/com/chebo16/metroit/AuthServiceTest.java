package com.chebo16.metroit;

import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.exception.ValidationException;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.service.AuthService;
import com.chebo16.metroit.util.DatabaseConnection;
import com.chebo16.metroit.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthServiceTest {

    private static final String DELETE_TEST_USER_SQL = """
            DELETE FROM users
            WHERE id = ?
            """;

    private static final String INVALID_CREDENTIALS_MESSAGE =
            "Invalid username or password.";

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        UserDAO userDAO = new UserDAO();

        Long createdUserId = null;
        boolean cleanupCompleted = false;

        try {
            System.out.println("Testing AuthService operations...");
            System.out.println();

            int usersBeforeCreate = userDAO.findAll().size();

            System.out.println(
                    "1. Existing users were loaded successfully."
            );
            System.out.println(
                    "Users before test: " + usersBeforeCreate
            );
            System.out.println();

            String uniqueSuffix =
                    String.valueOf(System.currentTimeMillis());

            String username =
                    "auth_test_" + uniqueSuffix;

            String email =
                    "auth_test_" + uniqueSuffix + "@example.com";

            String rawPassword =
                    "AuthTest_" + uniqueSuffix + "_Aa1!";

            String passwordHash =
                    PasswordUtil.hashPassword(rawPassword);

            User testUser = new User(
                    username,
                    passwordHash,
                    "Temporary Authentication Test User",
                    email,
                    UserRole.TECHNICIAN
            );

            createdUserId = userDAO.insert(testUser);

            if (createdUserId == null || createdUserId <= 0) {
                throw new IllegalStateException(
                        "Generated user ID is invalid."
                );
            }

            final long userId = createdUserId;

            System.out.println(
                    "2. Temporary user was created successfully."
            );
            System.out.println(
                    "Generated user ID: " + userId
            );
            System.out.println();

            User authenticatedUser =
                    authService.authenticate(
                            username,
                            rawPassword
                    );

            requireCondition(
                    authenticatedUser.getId() != null
                            && authenticatedUser.getId() == userId,
                    "Authentication returned an unexpected user."
            );

            requireCondition(
                    username.equals(
                            authenticatedUser.getUsername()
                    ),
                    "Authenticated username is incorrect."
            );

            requireCondition(
                    authenticatedUser.isActive(),
                    "Authenticated user must be active."
            );

            requireCondition(
                    authenticatedUser.getRole()
                            == UserRole.TECHNICIAN,
                    "Authenticated user role is incorrect."
            );

            System.out.println(
                    "3. Correct credentials were accepted successfully."
            );
            System.out.println(
                    "Authenticated username: "
                            + authenticatedUser.getUsername()
            );
            System.out.println();

            User authenticatedWithSpaces =
                    authService.authenticate(
                            "   " + username + "   ",
                            rawPassword
                    );

            requireCondition(
                    authenticatedWithSpaces.getId() != null
                            && authenticatedWithSpaces.getId() == userId,
                    "Username spaces were not normalized."
            );

            System.out.println(
                    "4. Username normalization completed successfully."
            );
            System.out.println();

            expectValidationExceptionMessage(
                    () -> authService.authenticate(
                            username,
                            rawPassword + "_wrong"
                    ),
                    INVALID_CREDENTIALS_MESSAGE,
                    "Incorrect password was not rejected."
            );

            System.out.println(
                    "5. Incorrect password was rejected successfully."
            );
            System.out.println();

            expectValidationExceptionMessage(
                    () -> authService.authenticate(
                            "missing_user_" + uniqueSuffix,
                            rawPassword
                    ),
                    INVALID_CREDENTIALS_MESSAGE,
                    "Unknown username was not rejected."
            );

            System.out.println(
                    "6. Unknown username was rejected successfully."
            );
            System.out.println();

            System.out.println(
                    "7. Generic invalid-credentials response "
                            + "was verified successfully."
            );
            System.out.println();

            expectValidationException(
                    () -> authService.authenticate(
                            null,
                            rawPassword
                    ),
                    "Null username was not rejected."
            );

            expectValidationException(
                    () -> authService.authenticate(
                            "",
                            rawPassword
                    ),
                    "Empty username was not rejected."
            );

            expectValidationException(
                    () -> authService.authenticate(
                            "   ",
                            rawPassword
                    ),
                    "Blank username was not rejected."
            );

            expectValidationException(
                    () -> authService.authenticate(
                            "a".repeat(51),
                            rawPassword
                    ),
                    "Username longer than 50 characters "
                            + "was not rejected."
            );

            System.out.println(
                    "8. Invalid username inputs "
                            + "were rejected successfully."
            );
            System.out.println();

            expectValidationException(
                    () -> authService.authenticate(
                            username,
                            null
                    ),
                    "Null password was not rejected."
            );

            expectValidationException(
                    () -> authService.authenticate(
                            username,
                            ""
                    ),
                    "Empty password was not rejected."
            );

            expectValidationException(
                    () -> authService.authenticate(
                            username,
                            "   "
                    ),
                    "Blank password was not rejected."
            );

            System.out.println(
                    "9. Invalid password inputs "
                            + "were rejected successfully."
            );
            System.out.println();

            boolean deactivated =
                    userDAO.setActive(userId, false);

            requireCondition(
                    deactivated,
                    "Temporary user deactivation returned false."
            );

            expectValidationExceptionMessage(
                    () -> authService.authenticate(
                            username,
                            rawPassword
                    ),
                    "User account is inactive.",
                    "Inactive user was allowed to authenticate."
            );

            System.out.println(
                    "10. Inactive user authentication "
                            + "was rejected successfully."
            );
            System.out.println();

            boolean reactivated =
                    userDAO.setActive(userId, true);

            requireCondition(
                    reactivated,
                    "Temporary user activation returned false."
            );

            User reactivatedUser =
                    authService.authenticate(
                            username,
                            rawPassword
                    );

            requireCondition(
                    reactivatedUser.isActive(),
                    "Reactivated user could not authenticate."
            );

            System.out.println(
                    "11. Reactivated user authenticated successfully."
            );
            System.out.println();

            cleanupCompleted =
                    deleteTemporaryUser(userId);

            requireCondition(
                    cleanupCompleted,
                    "Temporary user cleanup returned false."
            );

            expectValidationExceptionMessage(
                    () -> authService.authenticate(
                            username,
                            rawPassword
                    ),
                    INVALID_CREDENTIALS_MESSAGE,
                    "Deleted user was still able to authenticate."
            );

            System.out.println(
                    "12. Temporary user was deleted successfully."
            );
            System.out.println(
                    "Deleted user ID: " + userId
            );
            System.out.println();

            int usersAfterCleanup =
                    userDAO.findAll().size();

            requireCondition(
                    usersAfterCleanup == usersBeforeCreate,
                    "User count changed after the test. "
                            + "Before: "
                            + usersBeforeCreate
                            + ", after: "
                            + usersAfterCleanup
            );

            System.out.println(
                    "Users after cleanup: " + usersAfterCleanup
            );
            System.out.println();

            System.out.println(
                    "13. All AuthService operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {
            System.err.println(
                    "AuthService test SQL error."
            );
            System.err.println(
                    "SQL error code: " + exception.getErrorCode()
            );
            System.err.println(
                    "SQL state: " + exception.getSQLState()
            );
            System.err.println(
                    "Reason: " + exception.getMessage()
            );

            throw new IllegalStateException(
                    "AuthService test failed because of a database error.",
                    exception
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "AuthService test failed."
            );
            System.err.println(
                    "Reason: " + exception.getMessage()
            );

            throw exception;

        } finally {
            if (createdUserId != null && !cleanupCompleted) {
                try {
                    boolean removed =
                            deleteTemporaryUser(createdUserId);

                    if (removed) {
                        System.out.println();
                        System.out.println(
                                "Temporary authentication user "
                                        + "was removed during "
                                        + "automatic cleanup."
                        );
                    }

                } catch (SQLException cleanupException) {
                    System.err.println();
                    System.err.println(
                            "Automatic authentication user "
                                    + "cleanup failed."
                    );
                    System.err.println(
                            "Temporary user ID: " + createdUserId
                    );
                    System.err.println(
                            "Reason: "
                                    + cleanupException.getMessage()
                    );
                }
            }
        }
    }

    private static boolean deleteTemporaryUser(long userId)
            throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                DELETE_TEST_USER_SQL
                        )
        ) {
            statement.setLong(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private static void expectValidationException(
            Runnable operation,
            String failureMessage
    ) {
        try {
            operation.run();

        } catch (ValidationException exception) {
            return;
        }

        throw new IllegalStateException(failureMessage);
    }

    private static void expectValidationExceptionMessage(
            Runnable operation,
            String expectedMessage,
            String failureMessage
    ) {
        try {
            operation.run();

        } catch (ValidationException exception) {
            requireCondition(
                    expectedMessage.equals(
                            exception.getMessage()
                    ),
                    "Unexpected validation message. "
                            + "Expected: '"
                            + expectedMessage
                            + "', actual: '"
                            + exception.getMessage()
                            + "'."
            );

            return;
        }

        throw new IllegalStateException(failureMessage);
    }

    private static void requireCondition(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}