package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public class CannotDeactivateSystemRoleException extends ConflictException {

    public CannotDeactivateSystemRoleException(@NonNull String message) {
        super(message);
    }
}
