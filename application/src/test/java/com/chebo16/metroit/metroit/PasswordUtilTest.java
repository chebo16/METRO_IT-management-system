package com.chebo16.metroit;

import com.chebo16.metroit.util.PasswordUtil;

public class PasswordUtilTest {

    public static void main(String[] args) {
        try {
            System.out.println(
                    "Testing PasswordUtil operations..."
            );
            System.out.println();

            String rawPassword =
                    "PasswordUtilTest_2026_Aa1!";

            String firstHash =
                    PasswordUtil.hashPassword(rawPassword);

            requireCondition(
                    firstHash != null
                            && !firstHash.isBlank(),
                    "Generated password hash is empty."
            );

            requireCondition(
                    firstHash.length() == 60,
                    "Generated BCrypt hash "
                            + "does not have 60 characters."
            );

            requireCondition(
                    firstHash.startsWith("$2"),
                    "Generated value is not "
                            + "a BCrypt hash."
            );

            requireCondition(
                    !rawPassword.equals(firstHash),
                    "Raw password was not hashed."
            );

            System.out.println(
                    "1. hashPassword() completed successfully."
            );
            System.out.println();

            boolean correctPasswordVerified =
                    PasswordUtil.verifyPassword(
                            rawPassword,
                            firstHash
                    );

            requireCondition(
                    correctPasswordVerified,
                    "Correct password was not verified."
            );

            System.out.println(
                    "2. Correct password "
                            + "was verified successfully."
            );
            System.out.println();

            boolean incorrectPasswordVerified =
                    PasswordUtil.verifyPassword(
                            rawPassword + "_wrong",
                            firstHash
                    );

            requireCondition(
                    !incorrectPasswordVerified,
                    "Incorrect password was accepted."
            );

            System.out.println(
                    "3. Incorrect password "
                            + "was rejected successfully."
            );
            System.out.println();

            String secondHash =
                    PasswordUtil.hashPassword(rawPassword);

            requireCondition(
                    !firstHash.equals(secondHash),
                    "Two hashes for the same password "
                            + "must be different."
            );

            requireCondition(
                    PasswordUtil.verifyPassword(
                            rawPassword,
                            secondHash
                    ),
                    "The second hash could not "
                            + "verify the password."
            );

            System.out.println(
                    "4. BCrypt generated different hashes "
                            + "for the same password successfully."
            );
            System.out.println();

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            null,
                            firstHash
                    ),
                    "Null password was accepted."
            );

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            "",
                            firstHash
                    ),
                    "Empty password was accepted."
            );

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            "   ",
                            firstHash
                    ),
                    "Blank password was accepted."
            );

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            rawPassword,
                            null
                    ),
                    "Null password hash was accepted."
            );

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            rawPassword,
                            ""
                    ),
                    "Empty password hash was accepted."
            );

            requireCondition(
                    !PasswordUtil.verifyPassword(
                            rawPassword,
                            "invalid-bcrypt-hash"
                    ),
                    "Invalid BCrypt hash was accepted."
            );

            System.out.println(
                    "5. Invalid verification inputs "
                            + "were rejected successfully."
            );
            System.out.println();

            expectIllegalArgumentException(
                    () -> PasswordUtil.hashPassword(null),
                    "Null password did not produce "
                            + "IllegalArgumentException."
            );

            expectIllegalArgumentException(
                    () -> PasswordUtil.hashPassword(""),
                    "Empty password did not produce "
                            + "IllegalArgumentException."
            );

            expectIllegalArgumentException(
                    () -> PasswordUtil.hashPassword("   "),
                    "Blank password did not produce "
                            + "IllegalArgumentException."
            );

            System.out.println(
                    "6. Invalid hashing inputs produced "
                            + "IllegalArgumentException successfully."
            );
            System.out.println();

            System.out.println(
                    "All PasswordUtil operations "
                            + "completed successfully."
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "PasswordUtil test failed."
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );

            throw exception;
        }
    }

    private static void expectIllegalArgumentException(
            Runnable operation,
            String failureMessage
    ) {
        try {
            operation.run();

        } catch (IllegalArgumentException exception) {
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