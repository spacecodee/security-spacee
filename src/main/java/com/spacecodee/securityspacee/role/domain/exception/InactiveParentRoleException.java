package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public class InactiveParentRoleException extends ValidationException {

    public InactiveParentRoleException(@NonNull String message) {
        super(message);
    }
}
