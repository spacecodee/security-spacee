package com.spacecodee.securityspacee.user.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

import lombok.Getter;

@Getter
public final class DuplicateUsernameException extends ConflictException {

    private final String username;

    public DuplicateUsernameException(@NonNull String username) {
        super("user.exception.duplicate_username");
        this.username = username;
    }

}
