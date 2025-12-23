package com.spacecodee.securityspacee.shared.exception.base;

import org.jspecify.annotations.NonNull;

/**
 * Base exception for resource not found errors.
 * <p>
 * Represents situations where a requested resource (session, user, token, etc.)
 * does not exist.
 * All subclasses automatically resolve to HTTP 404 NOT_FOUND.
 * <p>
 * Examples:
 * - Session not found
 * - Token record not found in database
 * - User not found in system
 *
 * @see org.springframework.http.HttpStatus#NOT_FOUND
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(@NonNull String message) {
        super(message);
    }

    public ResourceNotFoundException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
