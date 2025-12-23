package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class SessionInvalidStateException extends ValidationException {

    public SessionInvalidStateException() {
        super("session.error.invalid_state");
    }

    public SessionInvalidStateException(@NonNull String message) {
        super(message);
    }

    public SessionInvalidStateException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
