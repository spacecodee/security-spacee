package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public class InvalidRoleNameException extends ValidationException {

    public InvalidRoleNameException(@NonNull String message) {
        super(message);
    }
}
