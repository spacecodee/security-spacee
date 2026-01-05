package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class CannotLogoutCurrentSessionException extends ValidationException {

    public CannotLogoutCurrentSessionException() {
        super("session.error.cannot_logout_current_session");
    }

    public CannotLogoutCurrentSessionException(@NonNull String message) {
        super(message);
    }

    public CannotLogoutCurrentSessionException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
