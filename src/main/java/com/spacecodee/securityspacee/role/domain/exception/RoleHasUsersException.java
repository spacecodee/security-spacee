package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

import lombok.Getter;

@Getter
public class RoleHasUsersException extends ConflictException {

    private final Integer userCount;

    public RoleHasUsersException(@NonNull String message, Integer userCount) {
        super(message);
        this.userCount = userCount;
    }
}
