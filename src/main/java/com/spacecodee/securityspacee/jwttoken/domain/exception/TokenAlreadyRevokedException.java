package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class TokenAlreadyRevokedException extends RuntimeException {

    public TokenAlreadyRevokedException(String message) {
        super(message);
    }

}
