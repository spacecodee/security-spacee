package com.spacecodee.securityspacee.session.domain.exception;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class InvalidSessionStateException extends ValidationException {

    public InvalidSessionStateException() {
        super("session.error.invalid_session_state");
    }

    public InvalidSessionStateException(String messageKey) {
        super(messageKey);
    }
}
