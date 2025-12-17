package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

public final class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(@NonNull String message) {
        super(message);
    }

}
