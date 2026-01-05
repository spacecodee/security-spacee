package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class InvalidTokenTypeException extends ValidationException {

    public InvalidTokenTypeException(@NonNull String message) {
        super(message);
    }

}
