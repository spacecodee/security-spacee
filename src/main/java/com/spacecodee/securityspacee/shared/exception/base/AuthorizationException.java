package com.spacecodee.securityspacee.shared.exception.base;

import org.jspecify.annotations.NonNull;

/**
 * Base exception for authorization-related errors.
 * <p>
 * Represents issues where the user is authenticated but lacks required
 * permissions.
 * All subclasses automatically resolve to HTTP 403 FORBIDDEN.
 * <p>
 * Examples:
 * - User account is inactive
 * - User lacks required roles/permissions
 * - Access denied to resource
 *
 * @see org.springframework.http.HttpStatus#FORBIDDEN
 */
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(@NonNull String message) {
        super(message);
    }

    public AuthorizationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
