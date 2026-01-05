package com.spacecodee.securityspacee.role.domain.model;

import java.util.Objects;

public record RoleHierarchyNode(
        Integer roleId,
        String roleName,
        Integer hierarchyLevel) {

    public RoleHierarchyNode {
        Objects.requireNonNull(roleId, "roleId is required");
        Objects.requireNonNull(roleName, "roleName is required");
        Objects.requireNonNull(hierarchyLevel, "hierarchyLevel is required");
    }
}
