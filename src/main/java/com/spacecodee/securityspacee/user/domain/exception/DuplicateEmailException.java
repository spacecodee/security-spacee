package com.spacecodee.securityspacee.user.domain.exception;

import lombok.Getter;

@Getter
public final class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("user.exception.duplicate_email");
        this.email = email;
    }

}
