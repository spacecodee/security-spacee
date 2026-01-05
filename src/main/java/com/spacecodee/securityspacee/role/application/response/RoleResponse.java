package com.spacecodee.securityspacee.role.application.response;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

public record RoleResponse(
        Integer roleId,
        String name,
        String description,
        Integer hierarchyLevel,
        @Nullable String parentRoleName,
        @Nullable String systemRoleTag,
        @Nullable Integer maxUsers,
        Integer currentUserCount,
        boolean isActive,
        Instant createdAt,
        String createdBy) {
}
