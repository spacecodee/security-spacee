package com.spacecodee.securityspacee.session.domain.exception;

public final class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException() {
        super("session.error.not_found");
    }
}
