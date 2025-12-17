package com.spacecodee.securityspacee.session.domain.exception;

public final class SessionInvalidStateException extends RuntimeException {

    public SessionInvalidStateException() {
        super("session.error.invalid_state");
    }
}
