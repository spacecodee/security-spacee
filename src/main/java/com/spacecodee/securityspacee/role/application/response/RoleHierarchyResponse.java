package com.spacecodee.securityspacee.role.application.response;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.role.domain.model.RoleHierarchyNode;

public record RoleHierarchyResponse(
        Integer roleId,
        String roleName,
        Integer hierarchyLevel,
        @Nullable RoleHierarchyNode parent,
        List<RoleHierarchyNode> children,
        List<RoleHierarchyNode> ancestors) {
}
