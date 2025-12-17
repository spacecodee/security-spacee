package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class TokenHasNotExpiredException extends RuntimeException {

    public TokenHasNotExpiredException(String message) {
        super(message);
    }

}
