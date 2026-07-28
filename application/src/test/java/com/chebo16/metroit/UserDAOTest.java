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
            System.out.println("Testing UserDAO operations...");
            System.out.println();

            // READ ALL

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

            // CREATE

            String uniqueSuffix =
                    String.valueOf(System.currentTimeMillis());

            String originalUsername =
                    "dao_test_" + uniqueSuffix;

            String originalEmail =
                    "dao_test_" + uniqueSuffix
                            + "@example.com";

            User testUser = new User(
                    originalUsername,
                    "TEMPORARY_TEST_PASSWORD_HASH",
                    "Temporary DAO Test User",
                    originalEmail,
                    UserRole.TECHNICIAN
            );

            createdUserId =
                    userDAO.insert(testUser);

            if (createdUserId <= 0) {
                throw new IllegalStateException(
                        "Generated user ID is invalid."
                );
            }

            System.out.println(
                    "2. insert() completed successfully."
            );

            System.out.println(
                    "Generated user ID: "
                            + createdUserId
            );

            System.out.println();

            // READ BY ID

            Optional<User> insertedUserOptional =
                    userDAO.findById(createdUserId);

            if (insertedUserOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Inserted user was not found by ID."
                );
            }

            User insertedUser =
                    insertedUserOptional.get();

            System.out.println(
                    "3. findById() completed successfully."
            );

            System.out.println("Inserted user:");
            System.out.println(insertedUser);
            System.out.println();

            // READ BY USERNAME

            Optional<User> userByUsernameOptional =
                    userDAO.findByUsername(originalUsername);

            if (userByUsernameOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Inserted user was not found by username."
                );
            }

            if (!createdUserId.equals(
                    userByUsernameOptional.get().getId()
            )) {
                throw new IllegalStateException(
                        "findByUsername() returned "
                                + "an unexpected user."
                );
            }

            System.out.println(
                    "4. findByUsername() completed successfully."
            );

            System.out.println(
                    "Found username: "
                            + userByUsernameOptional
                            .get()
                            .getUsername()
            );

            System.out.println();

            // UPDATE

            String updatedUsername =
                    "dao_updated_" + uniqueSuffix;

            String updatedEmail =
                    "dao_updated_" + uniqueSuffix
                            + "@example.com";

            insertedUser.setUsername(updatedUsername);

            insertedUser.setPasswordHash(
                    "UPDATED_TEMPORARY_TEST_HASH"
            );

            insertedUser.setFullName(
                    "Updated DAO Test User"
            );

            insertedUser.setEmail(updatedEmail);

            insertedUser.setRole(
                    UserRole.ADMIN
            );

            boolean updated =
                    userDAO.update(insertedUser);

            if (!updated) {
                throw new IllegalStateException(
                        "User update returned false."
                );
            }

            Optional<User> updatedUserOptional =
                    userDAO.findById(createdUserId);

            if (updatedUserOptional.isEmpty()) {
                throw new IllegalStateException(
                        "Updated user was not found."
                );
            }

            User updatedUser =
                    updatedUserOptional.get();

            if (!updatedUsername.equals(
                    updatedUser.getUsername()
            )) {
                throw new IllegalStateException(
                        "Username was not updated."
                );
            }

            if (!"Updated DAO Test User".equals(
                    updatedUser.getFullName()
            )) {
                throw new IllegalStateException(
                        "Full name was not updated."
                );
            }

            if (updatedUser.getRole()
                    != UserRole.ADMIN) {

                throw new IllegalStateException(
                        "User role was not updated."
                );
            }

            System.out.println(
                    "5. update() completed successfully."
            );

            System.out.println("Updated user:");
            System.out.println(updatedUser);
            System.out.println();

            // DEACTIVATE

            boolean deactivated =
                    userDAO.setActive(
                            createdUserId,
                            false
                    );

            if (!deactivated) {
                throw new IllegalStateException(
                        "User deactivation returned false."
                );
            }

            Optional<User> inactiveUserOptional =
                    userDAO.findById(createdUserId);

            if (inactiveUserOptional.isEmpty()
                    || inactiveUserOptional
                    .get()
                    .isActive()) {

                throw new IllegalStateException(
                        "User was not deactivated."
                );
            }

            System.out.println(
                    "6. setActive(false) completed successfully."
            );

            System.out.println(
                    "User active status: "
                            + inactiveUserOptional
                            .get()
                            .isActive()
            );

            System.out.println();

            // ACTIVATE

            boolean activated =
                    userDAO.setActive(
                            createdUserId,
                            true
                    );

            if (!activated) {
                throw new IllegalStateException(
                        "User activation returned false."
                );
            }

            Optional<User> activeUserOptional =
                    userDAO.findById(createdUserId);

            if (activeUserOptional.isEmpty()
                    || !activeUserOptional
                    .get()
                    .isActive()) {

                throw new IllegalStateException(
                        "User was not activated."
                );
            }

            System.out.println(
                    "7. setActive(true) completed successfully."
            );

            System.out.println(
                    "User active status: "
                            + activeUserOptional
                            .get()
                            .isActive()
            );

            System.out.println();

            // CLEANUP

            cleanupCompleted =
                    deleteTemporaryUser(createdUserId);

            if (!cleanupCompleted) {
                throw new IllegalStateException(
                        "Temporary user cleanup returned false."
                );
            }

            Optional<User> deletedUser =
                    userDAO.findById(createdUserId);

            if (deletedUser.isPresent()) {
                throw new IllegalStateException(
                        "Temporary user still exists "
                                + "after cleanup."
                );
            }

            System.out.println(
                    "8. Temporary test user "
                            + "was deleted successfully."
            );

            System.out.println(
                    "Deleted user ID: "
                            + createdUserId
            );

            System.out.println();

            // FINAL COUNT CHECK

            int countAfterCleanup =
                    userDAO.findAll().size();

            if (countAfterCleanup != countBeforeInsert) {
                throw new IllegalStateException(
                        "User count changed after the test. "
                                + "Before: "
                                + countBeforeInsert
                                + ", after: "
                                + countAfterCleanup
                );
            }

            System.out.println(
                    "Users after cleanup: "
                            + countAfterCleanup
            );

            System.out.println();

            System.out.println(
                    "All UserDAO operations "
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

        } catch (RuntimeException exception) {

            System.err.println(
                    "UserDAO test failed."
            );

            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

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
}
