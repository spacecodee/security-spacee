package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public class CannotDeleteSystemRoleException extends ConflictException {

    public CannotDeleteSystemRoleException(@NonNull String message) {
        super(message);
    }
}
