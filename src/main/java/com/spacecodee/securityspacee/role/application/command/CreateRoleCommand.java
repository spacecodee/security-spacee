package com.spacecodee.securityspacee.role.application.command;

import org.jspecify.annotations.Nullable;

public record CreateRoleCommand(
        String name,
        String description,
        @Nullable Integer parentRoleId,
        @Nullable String systemRoleTag,
        @Nullable Integer maxUsers,
        boolean isActive) {
}
