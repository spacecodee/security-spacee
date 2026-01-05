package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthorizationException;

public final class UnauthorizedSessionAccessException extends AuthorizationException {

    public UnauthorizedSessionAccessException(@NonNull String message) {
        super(message);
    }

}
