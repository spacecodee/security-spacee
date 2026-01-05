package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

public class RoleAlreadyActiveException extends ConflictException {

    public RoleAlreadyActiveException(@NonNull String message) {
        super(message);
    }
}
