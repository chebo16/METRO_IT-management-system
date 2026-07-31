package com.chebo16.metroit.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Arrays;

public final class PasswordUtil {

    private static final int BCRYPT_COST = 12;

    private PasswordUtil() {
        // Utility class: object creation is not allowed.
    }

    public static String hashPassword(String rawPassword) {

        validateRawPassword(rawPassword);

        char[] passwordCharacters =
                rawPassword.toCharArray();

        try {
            return BCrypt.withDefaults()
                    .hashToString(
                            BCRYPT_COST,
                            passwordCharacters
                    );

        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Password could not be hashed.",
                    exception
            );

        } finally {
            Arrays.fill(
                    passwordCharacters,
                    '\0'
            );
        }
    }

    public static boolean verifyPassword(
            String rawPassword,
            String passwordHash
    ) {

        if (rawPassword == null
                || rawPassword.isBlank()
                || passwordHash == null
                || passwordHash.isBlank()) {

            return false;
        }

        char[] passwordCharacters =
                rawPassword.toCharArray();

        try {
            BCrypt.Result result =
                    BCrypt.verifyer()
                            .verify(
                                    passwordCharacters,
                                    passwordHash
                            );

            return result.verified;

        } catch (IllegalArgumentException exception) {
            return false;

        } finally {
            Arrays.fill(
                    passwordCharacters,
                    '\0'
            );
        }
    }

    private static void validateRawPassword(
            String rawPassword
    ) {

        if (rawPassword == null) {
            throw new IllegalArgumentException(
                    "Password must not be null."
            );
        }

        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Password must not be empty."
            );
        }
    }
}