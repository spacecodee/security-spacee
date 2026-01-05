package com.spacecodee.securityspacee.role.domain.event;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

public record RoleCreatedEvent(
        Integer roleId,
        String roleName,
        String description,
        Integer hierarchyLevel,
        @Nullable Integer parentRoleId,
        @Nullable String systemRoleTag,
        @Nullable Integer maxUsers,
        Instant createdAt) {
}
