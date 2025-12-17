package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

public final class RevokedTokenException extends RuntimeException {

    public RevokedTokenException(@NonNull String message) {
        super(message);
    }

    public RevokedTokenException(@NonNull String message, Throwable cause) {
        super(message, cause);
    }
}
