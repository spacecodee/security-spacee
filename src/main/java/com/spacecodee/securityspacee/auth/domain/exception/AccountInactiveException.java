package com.spacecodee.securityspacee.auth.domain.exception;

public final class AccountInactiveException extends RuntimeException {

    public AccountInactiveException(String message) {
        super(message);
    }
}
