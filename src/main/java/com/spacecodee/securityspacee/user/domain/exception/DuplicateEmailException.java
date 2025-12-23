package com.spacecodee.securityspacee.user.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

import lombok.Getter;

@Getter
public final class DuplicateEmailException extends ConflictException {

    private final String email;

    public DuplicateEmailException(@NonNull String email) {
        super("user.exception.duplicate_email");
        this.email = email;
    }

}
