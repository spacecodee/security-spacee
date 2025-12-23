package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public final class TokenAlreadyRevokedException extends ConflictException {

    public TokenAlreadyRevokedException(@NonNull String message) {
        super(message);
    }

}
