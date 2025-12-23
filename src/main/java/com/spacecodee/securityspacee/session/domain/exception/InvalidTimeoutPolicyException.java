package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class InvalidTimeoutPolicyException extends ValidationException {

    public InvalidTimeoutPolicyException(@NonNull String message) {
        super(message);
    }

}
