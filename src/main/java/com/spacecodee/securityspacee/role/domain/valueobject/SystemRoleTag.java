package com.spacecodee.securityspacee.role.domain.valueobject;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public enum SystemRoleTag {

    ADMIN, USER, AUDITOR;

    @Contract("_ -> new")
    public static @NonNull SystemRoleTag of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("role.validation.system_role_tag.required");
        }

        try {
            return SystemRoleTag.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new IllegalArgumentException("role.validation.system_role_tag.invalid");
        }
    }

    public boolean canDeleteUsers() {
        return this == ADMIN;
    }

    public boolean canViewAuditLogs() {
        return this == ADMIN || this == AUDITOR;
    }

    public boolean canManageRoles() {
        return this == ADMIN;
    }
}
