package com.spacecodee.securityspacee.shared.exception.base;

import org.jspecify.annotations.NonNull;

/**
 * Base exception for authentication-related errors.
 * <p>
 * Represents issues with token validation, credential verification, or session
 * state.
 * All subclasses automatically resolve to HTTP 401 UNAUTHORIZED.
 * <p>
 * Examples:
 * - Invalid or expired tokens
 * - Incorrect credentials
 * - Token signature verification failures
 * - Revoked tokens
 * - Expired sessions
 *
 * @see org.springframework.http.HttpStatus#UNAUTHORIZED
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(@NonNull String message) {
        super(message);
    }

    public AuthenticationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
