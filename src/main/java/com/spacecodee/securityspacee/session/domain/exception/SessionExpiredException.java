package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

public final class SessionExpiredException extends AuthenticationException {

    public SessionExpiredException() {
        super("session.error.expired");
    }

    public SessionExpiredException(@NonNull String message) {
        super(message);
    }

    public SessionExpiredException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
