package com.spacecodee.securityspacee.jwttoken.domain.exception;

public final class InvalidSignatureException extends RuntimeException {

    public InvalidSignatureException(String message) {
        super(message);
    }

}
