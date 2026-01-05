package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public class InvalidDescriptionException extends ValidationException {

    public InvalidDescriptionException(@NonNull String message) {
        super(message);
    }
}
