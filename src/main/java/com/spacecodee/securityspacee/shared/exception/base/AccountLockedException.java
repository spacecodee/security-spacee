package com.spacecodee.securityspacee.shared.exception.base;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

/**
 * Exception for account locked scenarios.
 * <p>
 * Represents situations where a user account is temporarily locked due to
 * failed authentication attempts.
 * Automatically resolves to HTTP 423 LOCKED (requires specific handler).
 * <p>
 * This exception includes the timestamp until which the account remains locked.
 *
 * @see org.springframework.http.HttpStatus#LOCKED
 */
public class AccountLockedException extends RuntimeException {

    private final Instant lockedUntil;

    public AccountLockedException(@NonNull String message, @NonNull Instant lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }

    public AccountLockedException(@NonNull String message, @NonNull Instant lockedUntil,
                                  @NonNull Throwable cause) {
        super(message, cause);
        this.lockedUntil = lockedUntil;
    }

    public @NonNull Instant getLockedUntil() {
        return this.lockedUntil;
    }
}
