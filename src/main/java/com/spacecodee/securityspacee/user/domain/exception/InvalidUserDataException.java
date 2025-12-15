package com.spacecodee.securityspacee.user.domain.exception;

import lombok.Getter;

@Getter
public final class InvalidUserDataException extends RuntimeException {

    private final String messageKey;
    private final transient Object[] args;

    public InvalidUserDataException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = new Object[0];
    }

    public InvalidUserDataException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}
