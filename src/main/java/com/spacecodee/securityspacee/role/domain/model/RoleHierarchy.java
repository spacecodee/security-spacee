package com.spacecodee.securityspacee.role.domain.model;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class RoleHierarchy {

    private final Integer roleId;
    private final String roleName;
    private final Integer hierarchyLevel;
    private final @Nullable RoleHierarchyNode parent;
    private final @NonNull List<RoleHierarchyNode> children;
    private final @NonNull List<RoleHierarchyNode> ancestors;

    RoleHierarchy(
            Integer roleId,
            String roleName,
            Integer hierarchyLevel,
            @Nullable RoleHierarchyNode parent,
            @NonNull List<RoleHierarchyNode> children,
            @NonNull List<RoleHierarchyNode> ancestors) {
        this.roleId = Objects.requireNonNull(roleId, "roleId is required");
        this.roleName = Objects.requireNonNull(roleName, "roleName is required");
        this.hierarchyLevel = Objects.requireNonNull(hierarchyLevel, "hierarchyLevel is required");
        this.parent = parent;
        this.children = Objects.requireNonNull(children, "children is required");
        this.ancestors = Objects.requireNonNull(ancestors, "ancestors is required");
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NonNull RoleHierarchy create(
            Integer roleId,
            String roleName,
            Integer hierarchyLevel,
            @Nullable RoleHierarchyNode parent,
            @NonNull List<RoleHierarchyNode> children,
            @NonNull List<RoleHierarchyNode> ancestors) {
        return RoleHierarchy.builder()
                .roleId(roleId)
                .roleName(roleName)
                .hierarchyLevel(hierarchyLevel)
                .parent(parent)
                .children(children)
                .ancestors(ancestors)
                .build();
    }

    public boolean isRoot() {
        return this.parent == null && this.hierarchyLevel == 1;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public int getDescendantCount() {
        return this.children.size();
    }

    public int getAncestorCount() {
        return this.ancestors.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleHierarchy that = (RoleHierarchy) o;
        return Objects.equals(this.roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.roleId);
    }
}
