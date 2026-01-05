package com.spacecodee.securityspacee.role.domain.exception;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public class RoleHierarchyCycleException extends ValidationException {

    public RoleHierarchyCycleException(String message) {
        super(message);
    }
}
