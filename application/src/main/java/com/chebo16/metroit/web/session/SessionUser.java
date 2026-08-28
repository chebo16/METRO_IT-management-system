package com.chebo16.metroit.web.session;

import com.chebo16.metroit.model.User;
import com.chebo16.metroit.model.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class SessionUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String fullName;
    private final UserRole role;

    public SessionUser(
            Long id,
            String username,
            String fullName,
            UserRole role
    ) {
        this.id = Objects.requireNonNull(
                id,
                "User ID must not be null."
        );

        this.username = requireText(username, "Username");
        this.fullName = requireText(fullName, "Full name");

        this.role = Objects.requireNonNull(
                role,
                "User role must not be null."
        );
    }

    public static SessionUser fromUser(User user) {
        Objects.requireNonNull(
                user,
                "User must not be null."
        );

        return new SessionUser(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
        );
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isTechnician() {
        return role == UserRole.TECHNICIAN;
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be empty."
            );
        }

        return value.trim();
    }

    @Override
    public String toString() {
        return "SessionUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}