package com.spacecodee.securityspacee.auth.domain.exception;

public final class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
