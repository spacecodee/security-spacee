package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ConflictException;

import lombok.Getter;

@Getter
public class SystemRoleTagAlreadyUsedException extends ConflictException {

    private final String systemRoleTag;

    public SystemRoleTagAlreadyUsedException(@NonNull String message, @NonNull String systemRoleTag) {
        super(message);
        this.systemRoleTag = systemRoleTag;
    }
}
