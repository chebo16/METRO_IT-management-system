package com.chebo16.metroit;

import com.chebo16.metroit.dao.UserDAO;
import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;
import com.chebo16.metroit.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDAOTest {

    private static final String DELETE_TEST_USER_SQL = """
            DELETE FROM users
            WHERE id = ?
            """;

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        Long createdUserId = null;
        boolean cleanupCompleted = false;

        try {
            System.out.println(
                    "Testing UserDAO operations..."
            );
            System.out.println();

            List<User> usersBeforeInsert =
                    userDAO.findAll();

            int countBeforeInsert =
                    usersBeforeInsert.size();

            System.out.println(
                    "1. findAll() completed successfully."
            );
            System.out.println(
                    "Users before insert: "
                            + countBeforeInsert
            );
            System.out.println();

            String uniqueSuffix =
                    String.valueOf(System.currentTimeMillis());

            String originalUsername =
                    "dao_test_" + uniqueSuffix;

            String originalEmail =
                    "dao_test_"
                            + uniqueSuffix
                            + "@example.com";

            String originalPasswordHash =
                    "TEMPORARY_TEST_HASH_" + uniqueSuffix;

            User testUser = new User(
                    originalUsername,
                    originalPasswordHash,
                    "Temporary DAO Test User",
                    originalEmail,
                    UserRole.TECHNICIAN
            );

            createdUserId =
                    userDAO.insert(testUser);

            requireCondition(
                    createdUserId != null
                            && createdUserId > 0,
                    "Generated user ID is invalid."
            );

            final long userId =
                    createdUserId;

            System.out.println(
                    "2. insert() completed successfully."
            );
            System.out.println(
                    "Generated user ID: " + userId
            );
            System.out.println();

            Optional<User> insertedUserOptional =
                    userDAO.findById(userId);

            requireCondition(
                    insertedUserOptional.isPresent(),
                    "Inserted user was not found by ID."
            );

            User insertedUser =
                    insertedUserOptional.get();

            requireCondition(
                    originalUsername.equals(
                            insertedUser.getUsername()
                    ),
                    "Inserted username is incorrect."
            );

            requireCondition(
                    originalEmail.equals(
                            insertedUser.getEmail()
                    ),
                    "Inserted email is incorrect."
            );

            requireCondition(
                    originalPasswordHash.equals(
                            insertedUser.getPasswordHash()
                    ),
                    "Inserted password hash is incorrect."
            );

            requireCondition(
                    insertedUser.getRole()
                            == UserRole.TECHNICIAN,
                    "Inserted user role is incorrect."
            );

            requireCondition(
                    insertedUser.isActive(),
                    "New user must be active."
            );

            System.out.println(
                    "3. findById() completed successfully."
            );
            System.out.println();

            Optional<User> userByUsernameOptional =
                    userDAO.findByUsername(
                            originalUsername
                    );

            requireCondition(
                    userByUsernameOptional.isPresent(),
                    "Inserted user was not found by username."
            );

            requireCondition(
                    userByUsernameOptional.get().getId() != null
                            && userByUsernameOptional
                            .get()
                            .getId() == userId,
                    "findByUsername() returned "
                            + "an unexpected user."
            );

            System.out.println(
                    "4. findByUsername() completed successfully."
            );
            System.out.println();

            String updatedUsername =
                    "dao_updated_" + uniqueSuffix;

            String updatedEmail =
                    "dao_updated_"
                            + uniqueSuffix
                            + "@example.com";

            String updatedPasswordHash =
                    "UPDATED_TEMPORARY_TEST_HASH_"
                            + uniqueSuffix;

            insertedUser.setUsername(
                    updatedUsername
            );
            insertedUser.setPasswordHash(
                    updatedPasswordHash
            );
            insertedUser.setFullName(
                    "Updated DAO Test User"
            );
            insertedUser.setEmail(
                    updatedEmail
            );
            insertedUser.setRole(
                    UserRole.ADMIN
            );

            boolean updated =
                    userDAO.update(insertedUser);

            requireCondition(
                    updated,
                    "User update returned false."
            );

            User updatedUser =
                    userDAO.findById(userId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated user was not found."
                                    )
                            );

            requireCondition(
                    updatedUsername.equals(
                            updatedUser.getUsername()
                    ),
                    "Username was not updated."
            );

            requireCondition(
                    updatedPasswordHash.equals(
                            updatedUser.getPasswordHash()
                    ),
                    "Password hash was not updated."
            );

            requireCondition(
                    "Updated DAO Test User".equals(
                            updatedUser.getFullName()
                    ),
                    "Full name was not updated."
            );

            requireCondition(
                    updatedEmail.equals(
                            updatedUser.getEmail()
                    ),
                    "Email was not updated."
            );

            requireCondition(
                    updatedUser.getRole()
                            == UserRole.ADMIN,
                    "User role was not updated."
            );

            Optional<User> userByUpdatedUsername =
                    userDAO.findByUsername(
                            updatedUsername
                    );

            requireCondition(
                    userByUpdatedUsername.isPresent()
                            && userByUpdatedUsername
                            .get()
                            .getId() != null
                            && userByUpdatedUsername
                            .get()
                            .getId() == userId,
                    "Updated username could not "
                            + "be found correctly."
            );

            System.out.println(
                    "5. update() completed successfully."
            );
            System.out.println();

            boolean deactivated =
                    userDAO.setActive(
                            userId,
                            false
                    );

            requireCondition(
                    deactivated,
                    "User deactivation returned false."
            );

            User inactiveUser =
                    userDAO.findById(userId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Deactivated user "
                                                    + "was not found."
                                    )
                            );

            requireCondition(
                    !inactiveUser.isActive(),
                    "User was not deactivated."
            );

            System.out.println(
                    "6. setActive(false) completed successfully."
            );
            System.out.println();

            boolean activated =
                    userDAO.setActive(
                            userId,
                            true
                    );

            requireCondition(
                    activated,
                    "User activation returned false."
            );

            User activeUser =
                    userDAO.findById(userId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Activated user "
                                                    + "was not found."
                                    )
                            );

            requireCondition(
                    activeUser.isActive(),
                    "User was not activated."
            );

            System.out.println(
                    "7. setActive(true) completed successfully."
            );
            System.out.println();

            cleanupCompleted =
                    deleteTemporaryUser(userId);

            requireCondition(
                    cleanupCompleted,
                    "Temporary user cleanup returned false."
            );

            requireCondition(
                    userDAO.findById(userId).isEmpty(),
                    "Temporary user still exists "
                            + "after cleanup."
            );

            requireCondition(
                    userDAO.findByUsername(
                            updatedUsername
                    ).isEmpty(),
                    "Deleted user is still available "
                            + "by username."
            );

            System.out.println(
                    "8. Temporary test user "
                            + "was deleted successfully."
            );
            System.out.println(
                    "Deleted user ID: " + userId
            );
            System.out.println();

            int countAfterCleanup =
                    userDAO.findAll().size();

            requireCondition(
                    countAfterCleanup == countBeforeInsert,
                    "User count changed after the test. "
                            + "Before: "
                            + countBeforeInsert
                            + ", after: "
                            + countAfterCleanup
            );

            System.out.println(
                    "Users after cleanup: "
                            + countAfterCleanup
            );
            System.out.println();

            System.out.println(
                    "9. All UserDAO operations "
                            + "completed successfully."
            );

        } catch (SQLException exception) {
            System.err.println(
                    "UserDAO SQL test failed."
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
                    "UserDAO test failed "
                            + "because of a database error.",
                    exception
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "UserDAO test failed."
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw exception;

        } finally {
            if (createdUserId != null
                    && !cleanupCompleted) {

                try {
                    boolean removed =
                            deleteTemporaryUser(
                                    createdUserId
                            );

                    if (removed) {
                        System.out.println();
                        System.out.println(
                                "Temporary user was removed "
                                        + "during automatic cleanup."
                        );
                    }

                } catch (SQLException cleanupException) {
                    System.err.println();
                    System.err.println(
                            "Automatic user cleanup failed."
                    );
                    System.err.println(
                            "Temporary user ID: "
                                    + createdUserId
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

    private static void requireCondition(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}