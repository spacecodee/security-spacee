package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

}
