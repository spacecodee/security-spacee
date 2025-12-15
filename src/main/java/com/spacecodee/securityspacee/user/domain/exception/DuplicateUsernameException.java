package com.spacecodee.securityspacee.user.domain.exception;

import lombok.Getter;

@Getter
public final class DuplicateUsernameException extends RuntimeException {

    private final String username;

    public DuplicateUsernameException(String username) {
        super("user.exception.duplicate_username");
        this.username = username;
    }

}
