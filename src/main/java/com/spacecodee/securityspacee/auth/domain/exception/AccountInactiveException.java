package com.spacecodee.securityspacee.auth.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthorizationException;

public final class AccountInactiveException extends AuthorizationException {

    public AccountInactiveException(@NonNull String message) {
        super(message);
    }

}
