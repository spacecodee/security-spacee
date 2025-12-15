package com.spacecodee.securityspacee.user.domain.model;

import java.time.Instant;

import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public record UserSnapshot(
        Long userId,
        Username username,
        Email email,
        Password password,
        UserType userType,
        boolean isActive,
        boolean emailVerified,
        UserProfile profile,
        Instant createdAt,
        Instant updatedAt) {

    @Contract("_, _, _, _, _, _, _, _, _, _ -> new")
    public static @NonNull UserSnapshot of(
            Long userId,
            Username username,
            Email email,
            Password password,
            UserType userType,
            boolean isActive,
            boolean emailVerified,
            UserProfile profile,
            Instant createdAt,
            Instant updatedAt) {
        return new UserSnapshot(userId, username, email, password, userType, isActive, emailVerified, profile,
                createdAt, updatedAt);
    }
}
