package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class TokenHasNotExpiredException extends ValidationException {

    public TokenHasNotExpiredException(@NonNull String message) {
        super(message);
    }

}
