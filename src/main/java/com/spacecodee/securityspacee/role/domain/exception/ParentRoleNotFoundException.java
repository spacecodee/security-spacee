package com.spacecodee.securityspacee.role.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ResourceNotFoundException;

public class ParentRoleNotFoundException extends ResourceNotFoundException {

    public ParentRoleNotFoundException(@NonNull String message) {
        super(message);
    }
}
