package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public final class MaxSessionsExceededException extends ConflictException {

    public MaxSessionsExceededException() {
        super("session.exception.max_sessions_exceeded");
    }

    public MaxSessionsExceededException(@NonNull String message) {
        super(message);
    }

    public MaxSessionsExceededException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
