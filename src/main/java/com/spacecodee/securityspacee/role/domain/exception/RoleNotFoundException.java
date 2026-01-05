package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {

    public RoleNotFoundException(@NonNull String message) {
        super(message);
    }
}
