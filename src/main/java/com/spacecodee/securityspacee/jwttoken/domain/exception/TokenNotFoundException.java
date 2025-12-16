package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException(String message) {
        super(message);
    }
}
