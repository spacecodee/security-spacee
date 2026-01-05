package com.spacecodee.securityspacee.shared.exception.base;

import org.jspecify.annotations.NonNull;

/**
 * Base exception for conflict-related errors.
 * <p>
 * Represents situations where the request conflicts with existing data or
 * invalid state transitions.
 * All subclasses automatically resolve to HTTP 409 CONFLICT.
 * <p>
 * Examples:
 * - Duplicate username or email
 * - Token already revoked
 * - Resource already exists with same identifier
 *
 * @see org.springframework.http.HttpStatus#CONFLICT
 */
public class ConflictException extends RuntimeException {

    public ConflictException(@NonNull String message) {
        super(message);
    }

    public ConflictException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
