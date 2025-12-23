package com.spacecodee.securityspacee.jwttoken.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

public final class TokenNotFoundException extends AuthenticationException {

    public TokenNotFoundException(@NonNull String message) {
        super(message);
    }

}
