package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public final class SessionAlreadyLoggedOutException extends ConflictException {

    public SessionAlreadyLoggedOutException(@NonNull String message) {
        super(message);
    }

}
