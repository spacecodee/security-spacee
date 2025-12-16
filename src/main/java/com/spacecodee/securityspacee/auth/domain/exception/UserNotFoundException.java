package com.spacecodee.securityspacee.auth.domain.exception;

public final class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
