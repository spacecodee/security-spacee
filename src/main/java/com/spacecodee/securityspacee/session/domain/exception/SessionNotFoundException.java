package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ResourceNotFoundException;

public final class SessionNotFoundException extends ResourceNotFoundException {

    public SessionNotFoundException() {
        super("session.error.not_found");
    }

    public SessionNotFoundException(@NonNull String message) {
        super(message);
    }

    public SessionNotFoundException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
