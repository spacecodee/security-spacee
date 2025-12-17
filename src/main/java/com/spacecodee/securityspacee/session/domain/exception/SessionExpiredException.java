package com.spacecodee.securityspacee.session.domain.exception;

public final class SessionExpiredException extends RuntimeException {

    public SessionExpiredException() {
        super("session.error.expired");
    }
}
