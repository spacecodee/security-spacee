package com.spacecodee.securityspacee.user.domain.model;

import java.time.Instant;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.domain.exception.InvalidUserDataException;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

import lombok.Getter;

@Getter
public final class User {

    private final Long userId;
    private final Username username;
    private final Email email;
    private final Password password;
    private final UserType userType;
    private final boolean isActive;
    private final boolean emailVerified;
    private final UserProfile profile;
    private final int failedLoginAttempts;
    private final Instant lockedUntil;
    private final Instant lastLoginAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(@NonNull UserSnapshot snapshot) {
        this.userId = snapshot.userId();
        this.username = Objects.requireNonNull(snapshot.username());
        this.email = Objects.requireNonNull(snapshot.email());
        this.password = Objects.requireNonNull(snapshot.password());
        this.userType = Objects.requireNonNull(snapshot.userType());
        this.isActive = snapshot.isActive();
        this.emailVerified = snapshot.emailVerified();
        this.profile = snapshot.profile();
        this.failedLoginAttempts = snapshot.failedLoginAttempts();
        this.lockedUntil = snapshot.lockedUntil();
        this.lastLoginAt = snapshot.lastLoginAt();
        this.createdAt = snapshot.createdAt() != null ? snapshot.createdAt() : Instant.now();
        this.updatedAt = snapshot.updatedAt() != null ? snapshot.updatedAt() : Instant.now();

        if (userType.requiresProfile() && profile == null) {
            throw new InvalidUserDataException("user.exception.human_profile_required");
        }

        if (!userType.requiresProfile() && profile != null) {
            throw new InvalidUserDataException("user.exception.non_human_profile_forbidden", userType);
        }
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NonNull User create(
            Username username,
            Email email,
            Password password,
            UserType userType,
            UserProfile profile) {
        UserSnapshot snapshot = new UserSnapshot(
                null, username, email, password, userType, true, false, profile,
                0, null, null, Instant.now(), Instant.now());
        return new User(snapshot);
    }

    @Contract("_ -> new")
    public static @NonNull User reconstitute(UserSnapshot snapshot) {
        return new User(snapshot);
    }

    @Contract(" -> new")
    public @NonNull User deactivate() {
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                false, this.emailVerified, this.profile, this.failedLoginAttempts,
                this.lockedUntil, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract(" -> new")
    public @NonNull User verifyEmail() {
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                this.isActive, true, this.profile, this.failedLoginAttempts,
                this.lockedUntil, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract("_ -> new")
    public @NonNull User changePassword(Password newPassword) {
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, newPassword, this.userType,
                this.isActive, this.emailVerified, this.profile, this.failedLoginAttempts,
                this.lockedUntil, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract(" -> new")
    public @NonNull User recordSuccessfulLogin() {
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                this.isActive, this.emailVerified, this.profile,
                0, null, Instant.now(), this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract("_ -> new")
    public @NonNull User incrementFailedLoginAttempts(int maxAttempts) {
        int newAttempts = this.failedLoginAttempts + 1;

        if (newAttempts >= maxAttempts) {
            throw new InvalidUserDataException("user.exception.max_login_attempts_reached");
        }

        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                this.isActive, this.emailVerified, this.profile,
                newAttempts, this.lockedUntil, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract("_ -> new")
    public @NonNull User lockAccount(long lockDurationMs) {
        Instant lockUntil = Instant.now().plusMillis(lockDurationMs);
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                this.isActive, this.emailVerified, this.profile,
                this.failedLoginAttempts, lockUntil, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    @Contract(" -> new")
    public @NonNull User unlockAccount() {
        UserSnapshot snapshot = new UserSnapshot(
                this.userId, this.username, this.email, this.password, this.userType,
                this.isActive, this.emailVerified, this.profile,
                0, null, this.lastLoginAt, this.createdAt, Instant.now());
        return new User(snapshot);
    }

    public boolean isLocked() {
        return this.lockedUntil != null && Instant.now().isBefore(this.lockedUntil);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;

        if (userId != null && user.userId != null) {
            return Objects.equals(userId, user.userId);
        }
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return userId != null ? Objects.hash(userId) : Objects.hash(username, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username=" + username +
                ", email=" + email +
                ", userType=" + userType +
                ", isActive=" + isActive +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
