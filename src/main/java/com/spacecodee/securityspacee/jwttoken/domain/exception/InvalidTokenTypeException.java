package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class InvalidTokenTypeException extends RuntimeException {

    public InvalidTokenTypeException(String message) {
        super(message);
    }

}
