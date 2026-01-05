package com.spacecodee.securityspacee.session.domain.exception;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

public final class MissingAuthenticationContextException extends AuthenticationException {

    public MissingAuthenticationContextException() {
        super("session.error.no_authentication_context");
    }

    public MissingAuthenticationContextException(String messageKey) {
        super(messageKey);
    }
}
