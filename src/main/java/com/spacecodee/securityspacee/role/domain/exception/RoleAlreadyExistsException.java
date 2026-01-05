package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

import lombok.Getter;

@Getter
public class RoleAlreadyExistsException extends ConflictException {

    private final String roleName;

    public RoleAlreadyExistsException(@NonNull String message, @NonNull String roleName) {
        super(message);
        this.roleName = roleName;
    }
}
