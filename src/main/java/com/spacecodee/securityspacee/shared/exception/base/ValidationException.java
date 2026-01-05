package com.spacecodee.securityspacee.shared.exception.base;

import org.jspecify.annotations.NonNull;

/**
 * Base exception for validation-related errors.
 * <p>
 * Represents issues with invalid input data, business rule violations, or
 * invalid state transitions.
 * All subclasses automatically resolve to HTTP 400 BAD_REQUEST.
 * <p>
 * Examples:
 * - Invalid user data format
 * - Token in wrong state for operation
 * - Session in invalid state
 * - Business rule violations
 *
 * @see org.springframework.http.HttpStatus#BAD_REQUEST
 */
public class ValidationException extends RuntimeException {

    public ValidationException(@NonNull String message) {
        super(message);
    }

    public ValidationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
