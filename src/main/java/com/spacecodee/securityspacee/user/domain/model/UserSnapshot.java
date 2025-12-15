package com.spacecodee.securityspacee.user.domain.model;

import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

public record UserSnapshot(
        Long userId,
        Username username,
        Email email,
        Password password,
        UserType userType,
        boolean isActive,
        boolean emailVerified,
        UserProfile profile,
        int failedLoginAttempts,
        Instant lockedUntil,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt) {

    @Contract("_, _, _, _, _, _, _, _, _, _, _, _, _ -> new")
    public static @NonNull UserSnapshot of(
            Long userId,
            Username username,
            Email email,
            Password password,
            UserType userType,
            boolean isActive,
            boolean emailVerified,
            UserProfile profile,
            int failedLoginAttempts,
            Instant lockedUntil,
            Instant lastLoginAt,
            Instant createdAt,
            Instant updatedAt) {
        return new UserSnapshot(userId, username, email, password, userType, isActive, emailVerified, profile,
                failedLoginAttempts, lockedUntil, lastLoginAt, createdAt, updatedAt);
    }
}
