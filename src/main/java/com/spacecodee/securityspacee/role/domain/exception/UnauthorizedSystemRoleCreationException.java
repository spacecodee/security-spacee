package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthorizationException;

public class UnauthorizedSystemRoleCreationException extends AuthorizationException {

    public UnauthorizedSystemRoleCreationException(@NonNull String message) {
        super(message);
    }
}
