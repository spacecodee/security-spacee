package com.spacecodee.securityspacee.auth.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

public final class InvalidCredentialsException extends AuthenticationException {

    public InvalidCredentialsException(@NonNull String message) {
        super(message);
    }

}
