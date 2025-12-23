package com.spacecodee.securityspacee.user.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

import lombok.Getter;

@Getter
public final class InvalidUserDataException extends ValidationException {

    private final String messageKey;
    private final transient Object[] args;

    public InvalidUserDataException(@NonNull String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = new Object[0];
    }

    public InvalidUserDataException(@NonNull String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}
